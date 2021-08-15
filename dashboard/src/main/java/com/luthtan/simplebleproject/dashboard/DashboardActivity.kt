package com.luthtan.simplebleproject.dashboard

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.customview.widget.ExploreByTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.ktx.options
import com.google.gson.Gson
import com.luthtan.simplebleproject.dashboard.adapter.DashboardAdapter
import com.luthtan.simplebleproject.dashboard.databinding.ActivityDashboardBinding
import com.luthtan.simplebleproject.dashboard.service.BleAdvertiserService
import com.luthtan.simplebleproject.dashboard.utils.*
import com.luthtan.simplebleproject.dashboard.viewmodel.DashboardViewModel
import com.luthtan.simplebleproject.data.network.StatusResponse
import com.luthtan.simplebleproject.data.repository.PreferencesRepository
import com.luthtan.simplebleproject.domain.entities.dashboard.BleEntity
import com.luthtan.simplebleproject.domain.response.dashboard.BleResponse
import org.jetbrains.anko.alert
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    private val preference: PreferencesRepository by inject()
    private val dashboardViewModel: DashboardViewModel by viewModel()

    private val database: DatabaseReference by inject()

    private lateinit var activityDashboardBinding: ActivityDashboardBinding

    private lateinit var dashboardAdapter: DashboardAdapter

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(activityDashboardBinding.root)
        setSupportActionBar(activityDashboardBinding.toolbarDashboard)

//        setBluetooth()

//        startService(getServiceIntent(this))

        dashboardAdapter = DashboardAdapter()

        setBleHistoryAdapter()

        addPostEventListener(database)

//        getDataSample()

        activityDashboardBinding.btnDashboardStartStopAdvertising.setOnClickListener(this)
    }

    private fun getServiceIntent(context: Context) : Intent {
        return Intent(context, BleAdvertiserService::class.java)
    }

    override fun onResume() {
        super.onResume()

        if (BleAdvertiserService.running) {
            Log.d("RUNNING", "RUNNING")
        }

        val filterIntent = IntentFilter()
        filterIntent.addAction(ADVERTISING_FAILED)
        filterIntent.addAction(BLUETOOTH_BROADCAST_RECEIVER)
        registerReceiver(adviserBroadcast, filterIntent)
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(adviserBroadcast)
    }


    override fun onClick(v: View) {
        when(v.id) {
            R.id.btn_dashboard_start_stop_advertising -> {
//                stopService(getServiceIntent(this))
            }
        }
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.value!!
                val jsonString = Gson().toJson(post)
                val jsonInit = JSONObject(jsonString)
                val dashboard = jsonInit.getJSONObject("dashboard")
                val uuid = dashboard.getJSONObject("uuid") //change uuid with real uuid
                val bleEntity = Gson().fromJson(uuid.toString(), BleEntity::class.java)
                setAttributeUI(bleEntity)
                if (!bleEntity.status) {
                    dashboardViewModel.insertRealtime(bleEntity)
                    setBleHistoryAdapter()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled ${error.message}")
            }
        }
        postReference.addValueEventListener(postListener)
    }

    private fun setAttributeUI(bleEntity: BleEntity) {
        activityDashboardBinding.tvDashboardName.text = bleEntity.name
        activityDashboardBinding.tvDashboardPosition.text = bleEntity.position
        activityDashboardBinding.btnDashboardStartStopAdvertising.isEnabled = !bleEntity.status
        if (bleEntity.status) {
            activityDashboardBinding.tvDashboardRoomDescription.text = bleEntity.room
            activityDashboardBinding.tvDashboardNumberPersonInRoom.text = bleEntity.numberPersonInRoom.toString()
        } else {
            activityDashboardBinding.tvDashboardRoomDescription.text = "-"
            activityDashboardBinding.tvDashboardNumberPersonInRoom.text = "-"
        }
    }

    private fun setBleHistoryAdapter() {
        dashboardViewModel.getAllUserData().observe(this, { list ->
            if (list != null) {
                Log.d("LIST_TESTING", list.toString())
                dashboardAdapter.setBleLogHistory(list)
                activityDashboardBinding.rvDashboardHistory.apply {
                    layoutManager = LinearLayoutManager(this@DashboardActivity)
                    adapter = dashboardAdapter
                    scheduleLayoutAnimation()
                    setHasFixedSize(true)
                }
            }
        })

    }


    private fun getDataSample() {
        dashboardViewModel.getUserData().observe(this, { result ->
        })
    }

    private fun setBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
            if (!bluetoothAdapter.isMultipleAdvertisementSupported) {
                runOnUiThread {
                    alert {
                        message = "Bluetooth not supported advertisement"
                        isCancelable = false
                        positiveButton(R.string.ok) {
                            finish()
                        }
                    }.show()
                }
            }
        }
    }

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startForResult.launch(enableBtIntent)
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            runOnUiThread {
                alert {
                    title = "Bluetooth turn on required"
                    message = "To running this app you should turn your bluetooth on"
                    isCancelable = false
                    positiveButton(R.string.ok) {
                        promptEnableBluetooth()
                    }
                }.show()
            }
        }
    }

    private val adviserBroadcast: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    if (intent.action.equals(BLUETOOTH_BROADCAST_RECEIVER)) {
                        when(intent.getIntExtra(BLUETOOTH_EXTRA, ExploreByTouchHelper.INVALID_ID)) {
                            BLUETOOTH_EXTRA_INT -> {
                                runOnUiThread {
                                    alert {
                                        title = "Reject!"
                                        message = "Don't turn off your Bluetooth, system will return your bluetooth on"
                                        isCancelable = false
                                        positiveButton(R.string.ok) {
                                            bluetoothAdapter.enable()
                                        }
                                    }.show()
                                }
                            }
                        }
                    } else {
                        val errorCode = intent.getIntExtra(ADVERTISING_FAILED_EXTRA_CODE, -1)

                        var errorMessage = getString(R.string.start_error_prefix)

                        errorMessage += when(errorCode) {
                            AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED ->
                                errorMessage.plus(" ${getString(R.string.start_error_already_started)}")
                            AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE ->
                                errorMessage.plus(" ${getString(R.string.start_error_too_large)}")
                            AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED ->
                                errorMessage.plus(" ${getString(R.string.start_error_unsupported)}")
                            AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR ->
                                errorMessage.plus(" ${getString(R.string.start_error_internal)}")
                            AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS ->
                                errorMessage.plus(" ${getString(R.string.start_error_too_many)}")
                            else -> errorMessage.plus(" ${getString(R.string.start_error_unknown)}")
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }

}
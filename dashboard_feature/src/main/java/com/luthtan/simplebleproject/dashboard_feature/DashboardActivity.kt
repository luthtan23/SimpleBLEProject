package com.luthtan.simplebleproject.dashboard_feature

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.gson.Gson
import com.luthtan.simplebleproject.dashboard_feature.adapter.DashboardAdapter
import com.luthtan.simplebleproject.dashboard_feature.databinding.ActivityDashboardBinding
import com.luthtan.simplebleproject.dashboard_feature.di.dashboardModule
import com.luthtan.simplebleproject.dashboard_feature.service.BleAdvertiserService
import com.luthtan.simplebleproject.dashboard_feature.utils.*
import com.luthtan.simplebleproject.dashboard_feature.viewmodel.DashboardViewModel
import com.luthtan.simplebleproject.data.repository.PreferencesRepository
import com.luthtan.simplebleproject.data.utils.USER_NAME_KEY_LOGIN_TO_DASHBOARD
import com.luthtan.simplebleproject.data.utils.UUID_KEY_LOGIN_TO_DASHBOARD
import com.luthtan.simplebleproject.domain.entities.dashboard.BleEntity
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import java.util.*


class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    private val database: DatabaseReference by inject()

    private val preferences: PreferencesRepository by inject()

    private val dashboardViewModel: DashboardViewModel by viewModel()

    private lateinit var activityDashboardBinding: ActivityDashboardBinding

    private val dashboardAdapter by lazy {
        DashboardAdapter()
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSetting = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private var isScanning = false
        set(value) {
            field = value
            runOnUiThread{ }
        }

    private val scanResults = mutableListOf<ScanResult>()

    private val isLocationPermissionGranted get() = hasPermission(LOCATION_FINE_PERM)

    private val usernameLogin by lazy {
        val extras = intent.extras
        if (extras != null) {
            extras.getString(USER_NAME_KEY_LOGIN_TO_DASHBOARD)
        } else {
            preferences.getUsernameRequest()
        }
    }

    private val uuidLogin by lazy {
        val extras = intent.extras
        if (extras != null) {
            extras.getString(UUID_KEY_LOGIN_TO_DASHBOARD)
        } else {
            preferences.getUuidRequest()
        }
    }

    private var isStateAdvertising = preferences.getAdvertisingState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(dashboardModule)

        requestLocationPermission()

        activityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(activityDashboardBinding.root)

        setBluetooth()

        setBleHistoryAdapter()

        UserProfile.username = usernameLogin.toString()

//        addPostEventListener(database)

        activityDashboardBinding.btnDashboardStartStopAdvertising.setOnClickListener(this)
        activityDashboardBinding.tvDashboardUsername.text = usernameLogin
        activityDashboardBinding.etDashboardUuid.text = uuidLogin?.toEditable()
        when (isStateAdvertising) {
            true -> activityDashboardBinding.btnDashboardStartStopAdvertising.text = "Stop Advertising"
            false -> activityDashboardBinding.btnDashboardStartStopAdvertising.text = "Start Advertising"
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unloadKoinModules(dashboardModule)
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
                preferences.setAdvertisingState(!isStateAdvertising)
                isStateAdvertising = !isStateAdvertising
                if (isStateAdvertising) {
                    activityDashboardBinding.btnDashboardStartStopAdvertising.text = "Stop Advertising"
                    UserProfile.setServiceUUID(activityDashboardBinding.etDashboardUuid.text.toString(), "brad")
                    startService(getServiceIntent(this))
                } else {
                    activityDashboardBinding.btnDashboardStartStopAdvertising.text = "Start Advertising"
                    stopService(getServiceIntent(this))
                }
                /*stopService(getServiceIntent(this))
                startBleScan()*/

            }
        }
    }

    private fun getServiceIntent(context: Context) : Intent {
        return Intent(context, BleAdvertiserService::class.java)
    }

    private fun startBleScan() {
        if (!isLocationPermissionGranted) {
            requestLocationPermission()
        } else {
            scanResults.clear()
            bleScanner.startScan(null, scanSetting, scanCallback)
            isScanning = true
        }
    }

    private fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false
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
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled ${error.message}")
            }
        }
        postReference.addValueEventListener(postListener)
    }

    private fun setBleHistoryAdapter() {
        dashboardViewModel.getAllUserData().observe(this, { list ->
            if (list != null) {
                if (list.isEmpty()) {
                    setNotFoundAttributeUI(false)
                } else {
                    setNotFoundAttributeUI(true)
                    dashboardAdapter.setBleLogHistory(list)
                    activityDashboardBinding.rvDashboardHistory.apply {
                        layoutManager = LinearLayoutManager(this@DashboardActivity)
                        adapter = dashboardAdapter
                        scheduleLayoutAnimation()
                        setHasFixedSize(true)
                    }
                }
            }
        })
    }

    private fun setAttributeUI(bleEntity: BleEntity) {
        activityDashboardBinding.tvDashboardUsername.text = bleEntity.name
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

    private fun setNotFoundAttributeUI(statusLog: Boolean) {
        if (statusLog) {
            activityDashboardBinding.apply {
                imgDashboardNotFound.visibility = View.GONE
                tvDashboardNotFoundDescription.visibility = View.GONE
            }
        } else {
            activityDashboardBinding.apply {
                imgDashboardNotFound.visibility = View.VISIBLE
                tvDashboardNotFoundDescription.visibility = View.VISIBLE
            }
        }
    }

    private fun setBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
            if (!bluetoothAdapter.isMultipleAdvertisementSupported) {
                runOnUiThread {
                    /*alert {
                        message = "Bluetooth not supported advertisement"
                        isCancelable = false
                        positiveButton(R.string.ok) {
                            finish()
                        }
                    }.show()*/
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

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            //force to turn on bluetooth
            bluetoothAdapter.enable()
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
            } else {
                with(result.device) {
                    Log.d(
                        "SCAN_BLE",
                        "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address, rssi: ${result.rssi}"
                    )
                    try {
                        if (name.equals("Stella Smart")) {
                            Toast.makeText(
                                this@DashboardActivity,
                                "Advertising Rerun",
                                Toast.LENGTH_LONG
                            ).show()
//                            startService(getServiceIntent(this@DashboardActivity))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                scanResults.add(result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("FAILED TO SCAN", errorCode.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            PERMISSION_REQUEST_LOCATION -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    requestLocationPermission()
                } else {
                    startBleScan()
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {

            return
        }
        requestPermission(
            LOCATION_FINE_PERM,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private val adviserBroadcast: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val errorCode = intent!!.getIntExtra(ADVERTISING_FAILED_EXTRA_CODE, -1)

                val errorMessage = getString(R.string.start_error_prefix)

                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) == PackageManager.PERMISSION_GRANTED
    }

    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

}
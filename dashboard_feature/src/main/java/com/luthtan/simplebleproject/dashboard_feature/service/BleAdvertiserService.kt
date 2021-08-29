package com.luthtan.simplebleproject.dashboard_feature.service

import android.app.*
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.customview.widget.ExploreByTouchHelper
import com.luthtan.simplebleproject.dashboard_feature.DashboardActivity
import com.luthtan.simplebleproject.dashboard_feature.R
import com.luthtan.simplebleproject.dashboard_feature.utils.*
import java.util.concurrent.TimeUnit

class BleAdvertiserService : Service() {

    private val TAG = "AdvertiserService"
    private var bluetoothLeAdvertiser: BluetoothLeAdvertiser? = null
    private var advertiseCallback: AdvertiseCallback? = null
    private var handler: Handler? = null
    private lateinit var bluetoothAdapter: BluetoothAdapter

    /**
     * Length of time to allow advertising before automatically shutting off. (10 minutes)
     */
    private val TIMEOUT: Long = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)
    private val TIME_BLUETOOTH_CHECKED: Long = TimeUnit.MILLISECONDS.convert(2, TimeUnit.SECONDS)

    override fun onBind(intent: Intent?): IBinder? {
        return null // no binding necessary. This Service will only be started
    }

    override fun onCreate() {
        running = true
        initialize()
        startAdvertising()
        goForeground()
        setTimeout()
        val filterIntent = IntentFilter(BLUETOOTH_BROADCAST_RECEIVER)
        registerReceiver(adviserBroadcast, filterIntent)
        super.onCreate()
    }

    override fun onDestroy() {
        running = false
        stopAdvertising()
        handler?.removeCallbacksAndMessages(null) // this is a generic way for removing tasks
        stopForeground(true)
        unregisterReceiver(adviserBroadcast)
        super.onDestroy()
    }

    private fun initialize() {
        if (bluetoothLeAdvertiser == null) {
            val manager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = manager.adapter
            bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        }
    }

    private fun startAdvertising() {
        Log.d(TAG, "Service: Starting Advertising")
        if (advertiseCallback == null) {
            Log.d(TAG, "Service: Not Null")
            val settings: AdvertiseSettings = buildAdvertiseSettings()
            val data: AdvertiseData = buildAdvertiseData()
            advertiseCallback = sampleAdvertiseCallback()
            bluetoothLeAdvertiser?.startAdvertising(settings, data, advertiseCallback)
        }
    }

    private fun stopAdvertising() {
        if (bluetoothLeAdvertiser != null) {
            bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
            advertiseCallback = null
        }
    }

    private fun goForeground() {
        val notificationIntent = Intent(this, DashboardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val nBuilder = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val bleNotificationChannel = NotificationChannel(
                    BLE_NOTIFICATION_CHANNEL_ID, "BLE",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                val nManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                nManager.createNotificationChannel(bleNotificationChannel)
                Notification.Builder(this,
                    BLE_NOTIFICATION_CHANNEL_ID
                )
            }
            else -> Notification.Builder(this)
        }

        val notification = nBuilder.setContentTitle(getString(R.string.bt_notif_title))
            .setContentText(getString(R.string.bt_notif_txt))
            .setSmallIcon(R.drawable.ic_baseline_groups_24)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun buildAdvertiseSettings() = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
        .setTimeout(0).build()

    private fun buildAdvertiseData() = AdvertiseData.Builder()
        .addServiceUuid(ScanFilterService_UUID).setIncludeDeviceName(true).build()

    private fun sampleAdvertiseCallback() = object : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.d(TAG, "Advertising failed")
            broadcastFailureIntent(errorCode)
            stopSelf()
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            Log.d(TAG, "Advertising successfully started")
        }
    }

    private fun broadcastFailureIntent(errorCode: Int) {
        val failureIntent = Intent().setAction(ADVERTISING_FAILED).putExtra(
            BT_ADVERTISING_FAILED_EXTRA_CODE, errorCode
        )
        sendBroadcast(failureIntent)
    }

    private fun setTimeout() {
        handler = Handler(Looper.myLooper()!!)
        val runnable = Runnable {
            Log.d(
                TAG,
                "run: AdvertiserService has reached timeout of $TIMEOUT milliseconds, stopping advertising."
            )
            broadcastFailureIntent(ADVERTISING_TIMED_OUT)
        }
        handler?.postDelayed(runnable, TIMEOUT)
    }

    companion object {
        /**
         * A global variable to let DashboardFragment check if the Service is running without needing
         * to start or bind to it.
         * This is the best practice method as defined here:
         * https://groups.google.com/forum/#!topic/android-developers/jEvXMWgbgzE
         */
        var running: Boolean = false
    }

    private val adviserBroadcast: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    intentBroadcastBluetooth(intent)
                }
            }
        }
    }

    private fun intentBroadcastBluetooth(intent: Intent) {
        if (intent.action.equals(BLUETOOTH_BROADCAST_RECEIVER)) {
            when(intent.getIntExtra(BLUETOOTH_EXTRA, ExploreByTouchHelper.INVALID_ID)) {
                BLUETOOTH_EXTRA_INT -> {
                    val runnable = Runnable {
                        bluetoothAdapter.enable()
                        bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
                        stopAdvertising()
                        startAdvertising()
                    }
                    Handler(Looper.myLooper()!!).postDelayed(runnable, TIME_BLUETOOTH_CHECKED)
                }
            }
        }
    }

    private val mGattServerCallback = object : BluetoothGattServerCallback() {
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {

        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {

        }

        override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
        }

        override fun onDescriptorReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            descriptor: BluetoothGattDescriptor?
        ) {

        }

        override fun onDescriptorWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            descriptor: BluetoothGattDescriptor?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {

        }

        override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
            super.onExecuteWrite(device, requestId, execute)
        }

        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            super.onNotificationSent(device, status)
        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            super.onServiceAdded(status, service)
        }
    }

}
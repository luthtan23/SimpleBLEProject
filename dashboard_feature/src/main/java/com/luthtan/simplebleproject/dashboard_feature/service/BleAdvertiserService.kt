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
import android.os.*
import android.util.Log
import androidx.customview.widget.ExploreByTouchHelper
import com.luthtan.simplebleproject.dashboard_feature.DashboardActivity
import com.luthtan.simplebleproject.dashboard_feature.R
import com.luthtan.simplebleproject.dashboard_feature.utils.*
import java.util.*
import java.util.concurrent.TimeUnit

class BleAdvertiserService : Service() {

    private val TAG = "AdvertiserService"
    private var bluetoothLeAdvertiser: BluetoothLeAdvertiser? = null
    private var bluetoothGattServer: BluetoothGattServer? = null
    private var advertiseCallback: AdvertiseCallback? = null
    private var handler: Handler? = null
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothManager: BluetoothManager

    private var isGattRegistered = false

    private val registeredDevices = mutableSetOf<BluetoothDevice>()

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
        startBle()
        setTimeout()
        val filterIntent = IntentFilter(BLUETOOTH_BROADCAST_RECEIVER)
        registerReceiver(adviserBroadcast, filterIntent)
        super.onCreate()
    }

    override fun onDestroy() {
        running = false
        stopBle()
        handler?.removeCallbacksAndMessages(null) // this is a generic way for removing tasks
        stopForeground(true)
        unregisterReceiver(adviserBroadcast)
        super.onDestroy()
    }

    private fun initialize() {
        if (bluetoothLeAdvertiser == null) {
            bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
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
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    private fun buildAdvertiseSettings() = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
        .setConnectable(true)
        .setTimeout(0).build()

    private fun buildAdvertiseData() = AdvertiseData.Builder()
        .addServiceUuid(ScanFilterServiceUUID)
        .setIncludeDeviceName(true).build()

    private fun sampleAdvertiseCallback() = object : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.d(TAG, "Advertising failed: $errorCode")
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
                    stopBle()
                    val runnable = Runnable {
                        bluetoothAdapter.enable()
                        bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
                        startBle()
                    }
                    Handler(Looper.myLooper()!!).postDelayed(runnable, TIME_BLUETOOTH_CHECKED)
                }
            }
        }
    }

    private fun startBle() {
        startAdvertising()
        startServer()
    }

    private fun stopBle() {
        stopAdvertising()
        stopServer()
    }

    private fun startServer() {
        Log.i(TAG, "Start Server BLE, UUID: ${UserProfile.getServiceUUID()}")
        bluetoothGattServer?.clearServices()
        bluetoothGattServer?.close()
        bluetoothGattServer = bluetoothManager.openGattServer(applicationContext, mGattServerCallback)

        if (bluetoothGattServer != null) {
            bluetoothGattServer?.addService(UserProfile.createUserService())
        }
    }

    private fun stopServer() {
        bluetoothGattServer?.clearServices()
        bluetoothGattServer?.close()
    }

    private val mGattServerCallback = object : BluetoothGattServerCallback() {

        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: $device")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: $device")
                //Remove device from any active subscriptions
                registeredDevices.remove(device)
            } else {
                registeredDevices.remove(device)
            }
        }

        override fun onCharacteristicReadRequest(device: BluetoothDevice, requestId: Int, offset: Int,
                                                 characteristic: BluetoothGattCharacteristic) {
            val now = System.currentTimeMillis()
            goForeground()
            when {
                UserProfile.getServiceUUID() == characteristic.uuid -> {
                    Log.i(TAG, "Read CurrentTime")
                    bluetoothGattServer?.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        UserProfile.getUserName("JOHN"))
                }
                UserProfile.LOCAL_TIME_INFO == characteristic.uuid -> {
                    Log.i(TAG, "Read LocalTimeInfo")
                    bluetoothGattServer?.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        UserProfile.getLocalTimeInfo(now))
                }
                else -> {
                    // Invalid characteristic
                    Log.w(TAG, "Invalid Characteristic Read: " + characteristic.uuid)
                    bluetoothGattServer?.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null)
                }
            }
        }

        override fun onDescriptorReadRequest(device: BluetoothDevice, requestId: Int, offset: Int,
                                             descriptor: BluetoothGattDescriptor) {
            if (UserProfile.CLIENT_CONFIG == descriptor.uuid) {
                Log.d(TAG, "Config descriptor read")
                val returnValue = if (registeredDevices.contains(device)) {
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                } else {
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                }
                bluetoothGattServer?.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    0,
                    returnValue)
            } else {
                Log.w(TAG, "Unknown descriptor read request")
                bluetoothGattServer?.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0, null)
            }
        }

        override fun onDescriptorWriteRequest(device: BluetoothDevice, requestId: Int,
                                              descriptor: BluetoothGattDescriptor,
                                              preparedWrite: Boolean, responseNeeded: Boolean,
                                              offset: Int, value: ByteArray) {
            if (UserProfile.CLIENT_CONFIG == descriptor.uuid) {
                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Subscribe device to notifications: $device")
                    registeredDevices.add(device)
                } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Unsubscribe device from notifications: $device")
                    registeredDevices.remove(device)
                }

                if (responseNeeded) {
                    bluetoothGattServer?.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0, null)
                }
            } else {
                Log.w(TAG, "Unknown descriptor write request")
                if (responseNeeded) {
                    bluetoothGattServer?.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0, null)
                }
            }
        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            super.onServiceAdded(status, service)

        }
    }

    private fun notifyRegisteredDevices() {
        if (registeredDevices.isEmpty()) {
            Log.i(TAG, "No subscribers registered")
            return
        }

        val exactUser = UserProfile.getUserName("Brad")

        Log.i(TAG, "Sending update to ${registeredDevices.size} subscribers")

        for (device in registeredDevices) {
            bluetoothGattServer?.cancelConnection(device)
            val userCharacteristic = bluetoothGattServer
                ?.getService(UserProfile.getServiceUUID())
                ?.getCharacteristic(UserProfile.getNameCharUUID())
            userCharacteristic?.value = exactUser
            bluetoothGattServer?.notifyCharacteristicChanged(device, userCharacteristic, false)
        }
        if (registeredDevices.isNotEmpty()) {
            Log.i(TAG, "No subscribers registered")
            startServer()
        }
    }

}
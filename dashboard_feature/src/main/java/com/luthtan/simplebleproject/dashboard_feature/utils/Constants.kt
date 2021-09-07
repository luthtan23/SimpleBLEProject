package com.luthtan.simplebleproject.dashboard_feature.utils

import android.Manifest
import android.os.ParcelUuid

const val BT_ADVERTISING_FAILED_EXTRA_CODE = "bt_adv_failure_code"
const val INVALID_CODE = -1
const val ADVERTISING_TIMED_OUT = 6
const val BLE_NOTIFICATION_CHANNEL_ID = "bleChl"
const val FOREGROUND_NOTIFICATION_ID = 3
const val ADVERTISING_FAILED = "com.example.android.bluetoothadvertisements.advertising_failed"
const val REQUEST_ENABLE_BT = 11
const val PERMISSION_REQUEST_LOCATION = 101
/**
 * Saving the permission type here, under a shorter name, makes calling the permission type
 * from multiple sites more efficient
 */
const val LOCATION_FINE_PERM = Manifest.permission.ACCESS_FINE_LOCATION
const val LOCATION_PERMISSION_REQUEST_CODE = 2
val ScanFilterServiceUUID: ParcelUuid = ParcelUuid.fromString("0000fff2-0000-1000-8000-00805f9b34fb")

const val BLUETOOTH_BROADCAST_RECEIVER = "android.bluetooth.adapter.action.STATE_CHANGED"
const val BLUETOOTH_EXTRA = "android.bluetooth.adapter.extra.STATE"
const val BLUETOOTH_EXTRA_INT = 10

const val ADVERTISING_FAILED_EXTRA_CODE = "com.example.android.bluetoothadvertisements.advertising_failed"


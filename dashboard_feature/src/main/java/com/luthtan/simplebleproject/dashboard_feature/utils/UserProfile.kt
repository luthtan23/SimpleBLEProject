package com.luthtan.simplebleproject.dashboard_feature.utils

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import java.util.*

object UserProfile {

    var serviceUuid: String = ""
    var nameCharUuid: ByteArray? = null

    val LOCAL_TIME_INFO: UUID = UUID.fromString("00002a0f-0000-1000-8000-00805f9b34fb")
    /* Mandatory Client Characteristic Config Descriptor */
    val CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    val CUSTOM_CHAR: UUID = UUID.fromString("0774ec72-f28e-48ff-b7e8-0c53b457c56f")

    fun setServiceUUID(serviceUuid: String, nameCharStr: String) {
        this.serviceUuid = serviceUuid
        this.nameCharUuid = nameCharStr.toByteArray(Charsets.UTF_8)
    }

    fun getServiceUUID(): UUID {
        return UUID.fromString(serviceUuid)
    }

    fun getNameCharUUID(): UUID {
        return UUID.nameUUIDFromBytes(nameCharUuid)
    }

    fun createUserService(): BluetoothGattService {
        val service = BluetoothGattService(getServiceUUID(), BluetoothGattService.SERVICE_TYPE_PRIMARY)

        val nameCharacteristic = BluetoothGattCharacteristic(getNameCharUUID(),
            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ)

        val configDescriptor = BluetoothGattDescriptor(CLIENT_CONFIG,
            //Read/write descriptor
            BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE)
        nameCharacteristic.addDescriptor(configDescriptor)

        // Local Time Information characteristic
        val localTime = BluetoothGattCharacteristic(LOCAL_TIME_INFO,
            //Read-only characteristic
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ)

        service.addCharacteristic(nameCharacteristic)
        service.addCharacteristic(localTime)

        return service
    }

    fun getUserName(name: String): ByteArray {
        return name.toByteArray(Charsets.UTF_8)
    }

    fun getExactTime(timestamp: Long, adjustReason: Byte): ByteArray {
        val time = Calendar.getInstance()
        time.timeInMillis = timestamp

        val field = ByteArray(10)

        // Year
        val year = time.get(Calendar.YEAR)
        field[0] = (year and 0xFF).toByte()
        field[1] = (year shr 8 and 0xFF).toByte()
        // Month
        field[2] = (time.get(Calendar.MONTH) + 1).toByte()
        // Day
        field[3] = time.get(Calendar.DATE).toByte()
        // Hours
        field[4] = time.get(Calendar.HOUR_OF_DAY).toByte()
        // Minutes
        field[5] = time.get(Calendar.MINUTE).toByte()
        // Seconds
        field[6] = time.get(Calendar.SECOND).toByte()
        // Day of Week (1-7)
        field[7] = getDayOfWeekCode(time.get(Calendar.DAY_OF_WEEK))
        // Fractions256
        field[8] = (time.get(Calendar.MILLISECOND) / 256).toByte()

        field[9] = adjustReason

        return field
    }

    /**
     * Construct the field values for a Local Time Information characteristic
     * from the given epoch timestamp.
     */
    fun getLocalTimeInfo(timestamp: Long): ByteArray {
        val time = Calendar.getInstance()
        time.timeInMillis = timestamp

        val field = ByteArray(2)

        // Time zone
        val zoneOffset = time.get(Calendar.ZONE_OFFSET) / FIFTEEN_MINUTE_MILLIS // 15 minute intervals
        field[0] = zoneOffset.toByte()

        // DST Offset
        val dstOffset = time.get(Calendar.DST_OFFSET) / HALF_HOUR_MILLIS // 30 minute intervals
        field[1] = getDstOffsetCode(dstOffset)

        return field
    }

    fun customByteName(name: String): ByteArray {

        return name.toByteArray(Charsets.UTF_8)
    }

    /**
     * Convert a [Calendar] weekday value to the corresponding
     * Bluetooth weekday code.
     */
    private fun getDayOfWeekCode(dayOfWeek: Int): Byte = when (dayOfWeek) {
        Calendar.MONDAY -> DAY_MONDAY
        Calendar.TUESDAY -> DAY_TUESDAY
        Calendar.WEDNESDAY -> DAY_WEDNESDAY
        Calendar.THURSDAY -> DAY_THURSDAY
        Calendar.FRIDAY -> DAY_FRIDAY
        Calendar.SATURDAY -> DAY_SATURDAY
        Calendar.SUNDAY -> DAY_SUNDAY
        else -> DAY_UNKNOWN
    }

    /**
     * Convert a raw DST offset (in 30 minute intervals) to the
     * corresponding Bluetooth DST offset code.
     */
    private fun getDstOffsetCode(rawOffset: Int): Byte = when (rawOffset) {
        0 -> DST_STANDARD
        1 -> DST_HALF
        2 -> DST_SINGLE
        4 -> DST_DOUBLE
        else -> DST_UNKNOWN
    }


    // Adjustment Flags
    const val ADJUST_NONE: Byte = 0x0
    const val ADJUST_MANUAL: Byte = 0x1
    const val ADJUST_EXTERNAL: Byte = 0x2
    const val ADJUST_TIMEZONE: Byte = 0x4
    const val ADJUST_DST: Byte = 0x8

    /* Time bucket constants for local time information */
    private const val FIFTEEN_MINUTE_MILLIS = 900000
    private const val HALF_HOUR_MILLIS = 1800000

    /* Bluetooth Weekday Codes */
    private const val DAY_UNKNOWN: Byte = 0
    private const val DAY_MONDAY: Byte = 1
    private const val DAY_TUESDAY: Byte = 2
    private const val DAY_WEDNESDAY: Byte = 3
    private const val DAY_THURSDAY: Byte = 4
    private const val DAY_FRIDAY: Byte = 5
    private const val DAY_SATURDAY: Byte = 6
    private const val DAY_SUNDAY: Byte = 7

    /* Bluetooth DST Offset Codes */
    private const val DST_STANDARD: Byte = 0x0
    private const val DST_HALF: Byte = 0x2
    private const val DST_SINGLE: Byte = 0x4
    private const val DST_DOUBLE: Byte = 0x8
    private const val DST_UNKNOWN = 0xFF.toByte()

}
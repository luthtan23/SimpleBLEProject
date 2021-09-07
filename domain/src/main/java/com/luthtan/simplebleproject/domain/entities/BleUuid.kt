package com.luthtan.simplebleproject.domain.entities

data class BleUuid (
    var advertiseUuid: String = "00001805-0000-1000-8000-00805f9b34fb",
    var serverUuid: String = "00002a2b-0000-1000-8000-00805f9b34fb",
    var characteristicUuid: String = "00002a0f-0000-1000-8000-00805f9b34fb"
)
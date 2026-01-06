package com.msa.msahub.core.platform.network.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import com.msa.msahub.core.common.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BleDevice(val name: String?, val address: String)

class BleManager(
    private val context: Context,
    private val logger: Logger
) {
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = bluetoothManager.adapter
    private val scanner = adapter?.bluetoothLeScanner

    private val _discoveredDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    val discoveredDevices = _discoveredDevices.asStateFlow()

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = BleDevice(result.device.name, result.device.address)
            if (device !in _discoveredDevices.value) {
                _discoveredDevices.value = _discoveredDevices.value + device
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (adapter?.isEnabled == false) return
        _discoveredDevices.value = emptyList()
        scanner?.startScan(scanCallback)
        logger.d("BLE Scan started")
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanner?.stopScan(scanCallback)
        logger.d("BLE Scan stopped")
    }
}

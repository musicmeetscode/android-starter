package ug.global.temp.printer

import android.bluetooth.BluetoothDevice

interface PrinterService {
    fun connect(device: BluetoothDevice, onConnected: () -> Unit, onError: (String) -> Unit)
    fun disconnect()
    fun printText(text: String)
    fun printImage(imagePath: String)
    fun isConnected(): Boolean
}

package ug.global.temp.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

import javax.inject.Inject

@SuppressLint("MissingPermission")
class GenericBluetoothPrinterService @Inject constructor() : PrinterService {

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    
    // Standard SPP UUID
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun connect(device: BluetoothDevice, onConnected: () -> Unit, onError: (String) -> Unit) {
        scope.launch {
            try {
                socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                socket?.connect()
                outputStream = socket?.outputStream
                withContext(Dispatchers.Main) {
                    onConnected()
                }
            } catch (e: Exception) {
                Log.e("GenericPrinter", "Connection failed", e)
                try {
                    socket?.close()
                } catch (closeException: IOException) {}
                socket = null
                outputStream = null
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Connection failed")
                }
            }
        }
    }

    override fun disconnect() {
        try {
            outputStream?.close()
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        outputStream = null
        socket = null
    }

    override fun printText(text: String) {
        scope.launch {
            try {
                outputStream?.write(text.toByteArray())
                outputStream?.flush()
            } catch (e: Exception) {
                Log.e("GenericPrinter", "Print failed", e)
            }
        }
    }

    override fun printImage(imagePath: String) {
        // Complex implementation omitted for brevity, usually involves converting Bitmap to ESC/POS or CPCL bytes
    }

    override fun isConnected(): Boolean {
        return socket?.isConnected == true
    }
}

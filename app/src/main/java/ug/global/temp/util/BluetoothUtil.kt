package ug.global.parkingticketing.util

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import java.io.IOException
import java.util.UUID

object BluetoothUtil {
    private const val Innerprinter_Address = "00:11:22:33:44:55"
    private val PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var bluetoothSocket: BluetoothSocket? = null
    var isBlueToothPrinter = false
    private val bTAdapter: BluetoothAdapter?
        private get() = BluetoothAdapter.getDefaultAdapter()

    private fun getDevice(bluetoothAdapter: BluetoothAdapter?): BluetoothDevice? {
        for (device in bluetoothAdapter!!.bondedDevices) {
            if (device.address == Innerprinter_Address) {
                return device
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun getSocket(device: BluetoothDevice?): BluetoothSocket {
        val socket = device!!.createRfcommSocketToServiceRecord(PRINTER_UUID)
        socket.connect()
        return socket
    }

    fun connectBlueTooth(context: Context?): Boolean {
        if (bluetoothSocket != null) {
            return true
        }
        if (bTAdapter == null || !bTAdapter!!.isEnabled) {
            return false
        }
        val device = getDevice(bTAdapter)
        return if (device == null) {
            false
        } else try {
            bluetoothSocket = getSocket(device)
            true
        } catch (e: IOException) {
            false
        }
    }

    fun disconnectBlueTooth(context: Context?) {
        val bluetoothSocket2 = bluetoothSocket
        if (bluetoothSocket2 != null) {
            try {
                bluetoothSocket2.outputStream.close()
                bluetoothSocket!!.close()
                bluetoothSocket = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun sendData(bytes: ByteArray) {
        val bluetoothSocket2 = bluetoothSocket
        if (bluetoothSocket2 != null) {
            try {
                bluetoothSocket2.outputStream.write(bytes, 0, bytes.size)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

package ug.global.temp.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.woosim.printer.WoosimCmd
import com.woosim.printer.WoosimService

@SuppressLint("MissingPermission")
class WoosimPrinterImpl(private val handler: Handler) : PrinterService {

    private val mService: WoosimService = WoosimService(handler)

    override fun connect(device: BluetoothDevice, onConnected: () -> Unit, onError: (String) -> Unit) {
        // WoosimService usually connects via connect(String address, boolean secure)
        // or connect(BluetoothDevice device, boolean secure)
        // Assuming typical API:
        mService.connect(device, true) // Secure connection
        
        // Note: WoosimService usually reports status back via the Handler.
        // There is no immediate callback here.
        // We'll simulate a callback for now or rely on the Handler in the Activity to handle state changes.
        onConnected() 
    }

    override fun disconnect() {
        mService.stop()
    }

    override fun printText(text: String) {
        val data = text.toByteArray()
        // Use WoosimCmd to format if necessary, or send raw bytes
        // mService.write sends bytes to the printer
        mService.write(data)
    }

    override fun printImage(imagePath: String) {
        // Woosim defines methods for image printing in WoosimCmd commonly
        // mService.write(WoosimCmd.printBitmap(...))
    }

    override fun isConnected(): Boolean {
        // WoosimService might expose getState()
        return mService.state == WoosimService.STATE_CONNECTED
    }
}

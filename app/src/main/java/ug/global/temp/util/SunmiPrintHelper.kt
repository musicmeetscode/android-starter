package ug.global.parkingticketing.util

import android.content.Context
import android.graphics.Bitmap
import android.os.RemoteException
import android.widget.Toast
import com.sunmi.peripheral.printer.InnerLcdCallback
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.InnerResultCallback
import com.sunmi.peripheral.printer.SunmiPrinterService
import com.sunmi.peripheral.printer.WoyouConsts

class SunmiPrintHelper private constructor() {
    private val innerPrinterCallback: InnerPrinterCallback = object : InnerPrinterCallback() {
        /* access modifiers changed from: protected */
        public override fun onConnected(service: SunmiPrinterService) {
            sunmiPrinterService = service
            val unused = sunmiPrinterService
            checkSunmiPrinterService(service)
        }

        /* access modifiers changed from: protected */
        public override fun onDisconnected() {
            sunmiPrinterService = null
            val unused = sunmiPrinterService
            sunmiPrinter = LostSunmiPrinter
        }
    }
    var sunmiPrinter = CheckSunmiPrinter

    /* access modifiers changed from: private */
    var sunmiPrinterService: SunmiPrinterService? = null
    fun initSunmiPrinterService(context: Context?) {
        try {
            if (!InnerPrinterManager.getInstance().bindService(context, innerPrinterCallback)) {
                sunmiPrinter = NoSunmiPrinter
            }
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    fun deInitSunmiPrinterService(context: Context?) {
        try {
            if (sunmiPrinterService != null) {
                InnerPrinterManager.getInstance().unBindService(context, innerPrinterCallback)
                sunmiPrinterService = null
                sunmiPrinter = LostSunmiPrinter
            }
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
    }

    /* access modifiers changed from: private */
    fun checkSunmiPrinterService(service: SunmiPrinterService?) {
        var ret = false
        try {
            ret = InnerPrinterManager.getInstance().hasPrinter(service)
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
        }
        sunmiPrinter = if (ret) FoundSunmiPrinter else NoSunmiPrinter
    }

    private fun handleRemoteException(e: RemoteException) {}
    fun sendRawData(data: ByteArray?) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.sendRAWData(data, null as InnerResultCallback?)
            } catch (e: RemoteException) {
                handleRemoteException(e)
            }
        }
    }

    fun cutpaper() {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.cutPaper(null as InnerResultCallback?)
            } catch (e: RemoteException) {
                handleRemoteException(e)
            }
        }
    }

    fun initPrinter() {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.printerInit(null as InnerResultCallback?)
            } catch (e: RemoteException) {
                handleRemoteException(e)
            }
        }
    }

    fun print3Line() {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.lineWrap(3, null as InnerResultCallback?)
            } catch (e: RemoteException) {
                handleRemoteException(e)
            }
        }
    }

    val printerSerialNo: String
        get() {
            val sunmiPrinterService2 = sunmiPrinterService ?: return ""
            return try {
                sunmiPrinterService2.printerSerialNo
            } catch (e: RemoteException) {
                handleRemoteException(e)
                ""
            }
        }
    val deviceModel: String
        get() {
            val sunmiPrinterService2 = sunmiPrinterService ?: return ""
            return try {
                sunmiPrinterService2.printerModal
            } catch (e: RemoteException) {
                handleRemoteException(e)
                ""
            }
        }
    val printerVersion: String
        get() {
            val sunmiPrinterService2 = sunmiPrinterService ?: return ""
            return try {
                sunmiPrinterService2.printerVersion
            } catch (e: RemoteException) {
                handleRemoteException(e)
                ""
            }
        }
    val printerPaper: String
        get() {
            val sunmiPrinterService2 = sunmiPrinterService ?: return ""
            return try {
                if (sunmiPrinterService2.printerPaper == 1) "58mm" else "80mm"
            } catch (e: RemoteException) {
                handleRemoteException(e)
                ""
            }
        }

    fun getPrinterHead(callbcak: InnerResultCallback?) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.getPrinterFactory(callbcak)
            } catch (e: RemoteException) {
                handleRemoteException(e)
            }
        }
    }

    fun getPrinterDistance(callback: InnerResultCallback?) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.getPrintedLength(callback)
            } catch (e: RemoteException) {
                handleRemoteException(e)
            }
        }
    }

    fun setAlign(align: Int) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.setAlignment(align, null as InnerResultCallback?)
            } catch (e: RemoteException) {
                handleRemoteException(e)
            }
        }
    }

    fun feedPaper() {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.autoOutPaper(null as InnerResultCallback?)
            } catch (e: RemoteException) {
                print3Line()
            }
        }
    }

    @Throws(RemoteException::class)
    fun printText(content: String?, size: Float, isBold: Boolean, isUnderLine: Boolean) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            var i = 1
            try {
                sunmiPrinterService2.setPrinterStyle(1002, if (isBold) 1 else 2)
            } catch (e: RemoteException) {
                if (isBold) {
                    try {
                        sunmiPrinterService!!.sendRAWData(ESCUtil.boldOn(), null as InnerResultCallback?)
                    } catch (e2: RemoteException) {
                        e2.printStackTrace()
                        return
                    }
                } else {
                    sunmiPrinterService!!.sendRAWData(ESCUtil.boldOff(), null as InnerResultCallback?)
                }
            }
            try {
                val sunmiPrinterService3 = sunmiPrinterService
                if (!isUnderLine) {
                    i = 2
                }
                sunmiPrinterService3!!.setPrinterStyle(1003, i)
            } catch (e3: RemoteException) {
                if (isUnderLine) {
                    sunmiPrinterService!!.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null as InnerResultCallback?)
                } else {
                    sunmiPrinterService!!.sendRAWData(ESCUtil.underlineOff(), null as InnerResultCallback?)
                }
            }
            sunmiPrinterService!!.printTextWithFont(content, null as String?, size, null as InnerResultCallback?)
        }
    }

    fun printBarCode(data: String?, symbology: Int, height: Int, width: Int, textposition: Int) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.printBarCode(data, symbology, height, width, textposition, null as InnerResultCallback?)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    fun printQr(data: String?, modulesize: Int, errorlevel: Int) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.printQRCode(data, modulesize, errorlevel, null as InnerResultCallback?)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    fun printTable(txts: Array<String?>?, width: IntArray?, align: IntArray?) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.printColumnsString(txts, width, align, null as InnerResultCallback?)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(RemoteException::class)
    fun printBitmap(bitmap: Bitmap?, orientation: Int) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            if (orientation == 0) {
                try {
                    sunmiPrinterService2.printBitmap(bitmap, null as InnerResultCallback?)
                    sunmiPrinterService!!.printText("横向排列\n", null as InnerResultCallback?)
                    sunmiPrinterService!!.printBitmap(bitmap, null as InnerResultCallback?)
                    sunmiPrinterService!!.printText("横向排列\n", null as InnerResultCallback?)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            } else {
                sunmiPrinterService2.printBitmap(bitmap, null as InnerResultCallback?)
                sunmiPrinterService!!.printText("\n纵向排列\n", null as InnerResultCallback?)
                sunmiPrinterService!!.printBitmap(bitmap, null as InnerResultCallback?)
                sunmiPrinterService!!.printText("\n纵向排列\n", null as InnerResultCallback?)
            }
        }
    }

    val isBlackLabelMode: Boolean
        get() {
            val sunmiPrinterService2 = sunmiPrinterService ?: return false
            return try {
                if (sunmiPrinterService2.printerMode == 1) {
                    true
                } else false
            } catch (e: RemoteException) {
                false
            }
        }
    val isLabelMode: Boolean
        get() {
            val sunmiPrinterService2 = sunmiPrinterService ?: return false
            return try {
                if (sunmiPrinterService2.printerMode == 2) {
                    true
                } else false
            } catch (e: RemoteException) {
                false
            }
        }

    fun printTrans(context: Context?, callbcak: InnerResultCallback?) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.enterPrinterBuffer(true)
                printExample(context)
                sunmiPrinterService!!.exitPrinterBufferWithCallback(true, callbcak)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    fun openCashBox() {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.openDrawer(null as InnerResultCallback?)
            } catch (e: RemoteException) {
                handleRemoteException(e)
            }
        }
    }

    fun controlLcd(flag: Int) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.sendLCDCommand(flag)
            } catch (e: RemoteException) {
                handleRemoteException(e)
            }
        }
    }

    fun sendTextToLcd() {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.sendLCDFillString("SUNMI", 16, true, object : InnerLcdCallback() {
                    @Throws(RemoteException::class)
                    override fun onRunResult(show: Boolean) {
                    }
                })
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    fun sendTextsToLcd() {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.sendLCDMultiString(arrayOf("SUNMI", null, "SUNMI"), intArrayOf(2, 1, 2), object : InnerLcdCallback() {
                    @Throws(RemoteException::class)
                    override fun onRunResult(show: Boolean) {
                    }
                })
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    fun sendPicToLcd(pic: Bitmap?) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.sendLCDBitmap(pic, object : InnerLcdCallback() {
                    @Throws(RemoteException::class)
                    override fun onRunResult(show: Boolean) {
                    }
                })
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    fun printExample(context: Context?) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                val paper = sunmiPrinterService2.printerPaper
                sunmiPrinterService!!.printerInit(null as InnerResultCallback?)
                sunmiPrinterService!!.setAlignment(1, null as InnerResultCallback?)
                sunmiPrinterService!!.printText("测试样张\n", null as InnerResultCallback?)
                //                this.sunmiPrinterService.printBitmap(BitmapFactory.decodeResource(context.getResources(), .drawable.f4sunmi), (InnerResultCallback) null);
                sunmiPrinterService!!.lineWrap(1, null as InnerResultCallback?)
                sunmiPrinterService!!.setAlignment(0, null as InnerResultCallback?)
                try {
                    sunmiPrinterService!!.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0)
                } catch (e: RemoteException) {
                    sunmiPrinterService!!.sendRAWData(byteArrayOf(ESCUtil.ESC, 51, 0), null as InnerResultCallback?)
                }
                sunmiPrinterService!!.printTextWithFont("说明：这是一个自定义的小票样式例子,开发者可以仿照此进行自己的构建\n", null as String?, 12.0f, null as InnerResultCallback?)
                if (paper == 1) {
                    sunmiPrinterService!!.printText("--------------------------------\n", null as InnerResultCallback?)
                } else {
                    sunmiPrinterService!!.printText("------------------------------------------------\n", null as InnerResultCallback?)
                }
                try {
                    sunmiPrinterService!!.setPrinterStyle(1002, 1)
                } catch (e2: RemoteException) {
                    sunmiPrinterService!!.sendRAWData(ESCUtil.boldOn(), null as InnerResultCallback?)
                }
                val txts = arrayOf("商品", "价格")
                val width = intArrayOf(1, 1)
                val align = intArrayOf(0, 2)
                sunmiPrinterService!!.printColumnsString(txts, width, align, null as InnerResultCallback?)
                try {
                    sunmiPrinterService!!.setPrinterStyle(1002, 2)
                } catch (e3: RemoteException) {
                    sunmiPrinterService!!.sendRAWData(ESCUtil.boldOff(), null as InnerResultCallback?)
                }
                if (paper == 1) {
                    sunmiPrinterService!!.printText("--------------------------------\n", null as InnerResultCallback?)
                } else {
                    sunmiPrinterService!!.printText("------------------------------------------------\n", null as InnerResultCallback?)
                }
                txts[0] = "汉堡"
                txts[1] = "17¥"
                sunmiPrinterService!!.printColumnsString(txts, width, align, null as InnerResultCallback?)
                txts[0] = "可乐"
                txts[1] = "10¥"
                sunmiPrinterService!!.printColumnsString(txts, width, align, null as InnerResultCallback?)
                txts[0] = "薯条"
                txts[1] = "11¥"
                sunmiPrinterService!!.printColumnsString(txts, width, align, null as InnerResultCallback?)
                txts[0] = "炸鸡"
                txts[1] = "11¥"
                sunmiPrinterService!!.printColumnsString(txts, width, align, null as InnerResultCallback?)
                txts[0] = "圣代"
                txts[1] = "10¥"
                sunmiPrinterService!!.printColumnsString(txts, width, align, null as InnerResultCallback?)
                if (paper == 1) {
                    sunmiPrinterService!!.printText("--------------------------------\n", null as InnerResultCallback?)
                } else {
                    sunmiPrinterService!!.printText("------------------------------------------------\n", null as InnerResultCallback?)
                }
                sunmiPrinterService!!.printTextWithFont("总计:          59¥\b", null as String?, 40.0f, null as InnerResultCallback?)
                sunmiPrinterService!!.setAlignment(1, null as InnerResultCallback?)
                sunmiPrinterService!!.printQRCode("谢谢惠顾", 10, 0, null as InnerResultCallback?)
                sunmiPrinterService!!.setFontSize(36.0f, null as InnerResultCallback?)
                sunmiPrinterService!!.printText("谢谢惠顾", null as InnerResultCallback?)
                sunmiPrinterService!!.autoOutPaper(null as InnerResultCallback?)
            } catch (e4: RemoteException) {
                e4.printStackTrace()
            }
        }
    }

    fun showPrinterStatus(context: Context?) {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            var result = "Interface is too low to implement interface"
            try {
                when (sunmiPrinterService2.updatePrinterState()) {
                    1 -> result = "printer is running"
                    2 -> result = "printer found but still initializing"
                    3 -> result = "printer hardware interface is abnormal and needs to be reprinted"
                    4 -> result = "printer is out of paper"
                    5 -> result = "printer is overheating"
                    6 -> result = "printer's cover is not closed"
                    7 -> result = "printer's cutter is abnormal"
                    8 -> result = "printer's cutter is normal"
                    9 -> result = "not found black mark paper"
                    505 -> result = "printer does not exist"
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            Toast.makeText(context, result, Toast.LENGTH_LONG).show()
        }
    }

    fun printOneLabel() {
        val sunmiPrinterService2 = sunmiPrinterService
        if (sunmiPrinterService2 != null) {
            try {
                sunmiPrinterService2.labelLocate()
                printLabelContent()
                sunmiPrinterService!!.labelOutput()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(RemoteException::class)
    fun printMultiLabel(num: Int) {
        if (sunmiPrinterService != null) {
            var i = 0
            while (i < num) {
                try {
                    sunmiPrinterService!!.labelLocate()
                    printLabelContent()
                    i++
                } catch (e: RemoteException) {
                    e.printStackTrace()
                    return
                }
            }
            sunmiPrinterService!!.labelOutput()
        }
    }

    @Throws(RemoteException::class)
    private fun printLabelContent() {
        sunmiPrinterService!!.setPrinterStyle(1002, 1)
        sunmiPrinterService!!.lineWrap(1, null as InnerResultCallback?)
        sunmiPrinterService!!.setAlignment(0, null as InnerResultCallback?)
        sunmiPrinterService!!.printText("商品         豆浆\n", null as InnerResultCallback?)
        sunmiPrinterService!!.printText("到期时间         12-13  14时\n", null as InnerResultCallback?)
        sunmiPrinterService!!.printBarCode("{C1234567890123456", 8, 90, 2, 2, null as InnerResultCallback?)
        sunmiPrinterService!!.lineWrap(1, null as InnerResultCallback?)
    }

    companion object {
        var CheckSunmiPrinter = 1
        var FoundSunmiPrinter = 2
        var LostSunmiPrinter = 3
        var NoSunmiPrinter = 0
        val instance = SunmiPrintHelper()
    }
}

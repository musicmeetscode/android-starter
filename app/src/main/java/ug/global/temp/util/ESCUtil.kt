package ug.global.parkingticketing.util

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object ESCUtil {
    const val CAN: Byte = 24
    const val CR: Byte = 13
    const val DLE: Byte = 16
    const val ENQ: Byte = 5
    const val EOT: Byte = 4
    const val ESC: Byte = 27
    const val FF: Byte = 12
    const val FS: Byte = 28
    const val GS: Byte = 29
    const val HT: Byte = 9
    const val LF: Byte = 10
    const val SP: Byte = 32
    fun init_printer(): ByteArray {
        return byteArrayOf(ESC, 64)
    }

    fun setPrinterDarkness(value: Int): ByteArray {
        return byteArrayOf(GS, 40, 69, 4, 0, 5, 5, (value shr 8).toByte(), value.toByte())
    }

    fun getPrintQRCode(code: String, modulesize: Int, errorlevel: Int): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(setQRCodeSize(modulesize))
            buffer.write(setQRCodeErrorLevel(errorlevel))
            buffer.write(getQCodeBytes(code))
            buffer.write(getBytesForPrintQRCode(true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }

    fun getPrintDoubleQRCode(code1: String, code2: String, modulesize: Int, errorlevel: Int): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(setQRCodeSize(modulesize))
            buffer.write(setQRCodeErrorLevel(errorlevel))
            buffer.write(getQCodeBytes(code1))
            buffer.write(getBytesForPrintQRCode(false))
            buffer.write(getQCodeBytes(code2))
            buffer.write(byteArrayOf(ESC, 92, CAN, 0))
            buffer.write(getBytesForPrintQRCode(true))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }

    fun getPrintQRCode2(data: String?, size: Int): ByteArray? {
        return BytesUtil.byteMerger(byteArrayOf(GS, 118, 48, 0), BytesUtil.getZXingQRCode(data, size))
    }

    fun getPrintBarCode(data: String, symbology: Int, height: Int, width: Int, textposition: Int): ByteArray {
        val height2: Int
        val barcode: ByteArray?
        var width2 = width
        var textposition2 = textposition
        if (symbology < 0) {
            val str = data
        } else if (symbology > 10) {
            val str2 = data
        } else {
            if (width2 < 2 || width2 > 6) {
                width2 = 2
            }
            if (textposition2 < 0 || textposition2 > 3) {
                textposition2 = 0
            }
            height2 = if (height < 1 || height > 255) {
                162
            } else {
                height
            }
            val buffer = ByteArrayOutputStream()
            try {
                buffer.write(byteArrayOf(GS, 102, 1, GS, 72, textposition2.toByte(), GS, 119, width2.toByte(), GS, 104, height2.toByte(), 10))
                if (symbology == 10) {
                    barcode = BytesUtil.getBytesFromDecString(data)
                    val str3 = data
                } else {
                    try {
                        barcode = data.toByteArray(charset("GB18030"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return buffer.toByteArray()
                    }
                }
                if (symbology > 7) {
                    buffer.write(byteArrayOf(GS, 107, 73, (barcode!!.size + 2).toByte(), 123, (symbology + 65 - 8).toByte()))
                } else {
                    buffer.write(byteArrayOf(GS, 107, (symbology + 65).toByte(), barcode!!.size.toByte()))
                }
                buffer.write(barcode)
            } catch (e2: Exception) {
                val str4 = data
                e2.printStackTrace()
                return buffer.toByteArray()
            }
            return buffer.toByteArray()
        }
        return byteArrayOf(10)
    }

    fun printBitmap(bitmap: Bitmap): ByteArray? {
        return BytesUtil.byteMerger(byteArrayOf(GS, 118, 48, 0), BytesUtil.getBytesFromBitMap(bitmap))
    }

    fun printBitmap(bitmap: Bitmap, mode: Int): ByteArray? {
        return BytesUtil.byteMerger(byteArrayOf(GS, 118, 48, mode.toByte()), BytesUtil.getBytesFromBitMap(bitmap))
    }

    fun printBitmap(bytes: ByteArray?): ByteArray? {
        return BytesUtil.byteMerger(byteArrayOf(GS, 118, 48, 0), bytes)
    }

    fun selectBitmap(bitmap: Bitmap, mode: Int): ByteArray? {
        return BytesUtil.byteMerger(BytesUtil.byteMerger(byteArrayOf(ESC, 51, 0), BytesUtil.getBytesFromBitMap(bitmap, mode)), byteArrayOf(10, ESC, 50))
    }

    fun nextLine(lineNum: Int): ByteArray {
        val result = ByteArray(lineNum)
        for (i in 0 until lineNum) {
            result[i] = 10
        }
        return result
    }

    fun setDefaultLineSpace(): ByteArray {
        return byteArrayOf(ESC, 50)
    }

    fun setLineSpace(height: Int): ByteArray {
        return byteArrayOf(ESC, 51, height.toByte())
    }

    fun underlineWithOneDotWidthOn(): ByteArray {
        return byteArrayOf(ESC, 45, 1)
    }

    fun underlineWithTwoDotWidthOn(): ByteArray {
        return byteArrayOf(ESC, 45, 2)
    }

    fun underlineOff(): ByteArray {
        return byteArrayOf(ESC, 45, 0)
    }

    fun boldOn(): ByteArray {
        return byteArrayOf(ESC, 69, 15)
    }

    fun boldOff(): ByteArray {
        return byteArrayOf(ESC, 69, 0)
    }

    fun singleByte(): ByteArray {
        return byteArrayOf(FS, 46)
    }

    fun singleByteOff(): ByteArray {
        return byteArrayOf(FS, 38)
    }

    fun setCodeSystemSingle(charset: Byte): ByteArray {
        return byteArrayOf(ESC, 116, charset)
    }

    fun setCodeSystem(charset: Byte): ByteArray {
        return byteArrayOf(FS, 67, charset)
    }

    fun alignLeft(): ByteArray {
        return byteArrayOf(ESC, 97, 0)
    }

    fun alignCenter(): ByteArray {
        return byteArrayOf(ESC, 97, 1)
    }

    fun alignRight(): ByteArray {
        return byteArrayOf(ESC, 97, 2)
    }

    fun cutter(): ByteArray {
        return byteArrayOf(GS, 86, 1)
    }

    fun gogogo(): ByteArray {
        return byteArrayOf(FS, 40, 76, 2, 0, 66, 49)
    }

    private fun setQRCodeSize(modulesize: Int): ByteArray {
        return byteArrayOf(GS, 40, 107, 3, 0, 49, 67, modulesize.toByte())
    }

    private fun setQRCodeErrorLevel(errorlevel: Int): ByteArray {
        return byteArrayOf(GS, 40, 107, 3, 0, 49, 69, (errorlevel + 48).toByte())
    }

    private fun getBytesForPrintQRCode(single: Boolean): ByteArray {
        val dtmp: ByteArray
        if (single) {
            dtmp = ByteArray(9)
            dtmp[8] = 10
        } else {
            dtmp = ByteArray(8)
        }
        dtmp[0] = GS
        dtmp[1] = 40
        dtmp[2] = 107
        dtmp[3] = 3
        dtmp[4] = 0
        dtmp[5] = 49
        dtmp[6] = 81
        dtmp[7] = 48
        return dtmp
    }

    private fun getQCodeBytes(code: String): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            val d = code.toByteArray(charset("GB18030"))
            var len = d.size + 3
            if (len > 7092) {
                len = 7092
            }
            buffer.write(29)
            buffer.write(40)
            buffer.write(107)
            buffer.write(len.toByte().toInt())
            buffer.write((len shr 8).toByte().toInt())
            buffer.write(49)
            buffer.write(80)
            buffer.write(48)
            var i = 0
            while (i < d.size && i < len) {
                buffer.write(d[i].toInt())
                i++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }
}

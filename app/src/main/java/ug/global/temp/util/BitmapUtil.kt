package ug.global.temp.util

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object BitmapUtil {
    fun generateBitmap(content: String?, format: Int, width: Int, height: Int): Bitmap? {
        var height = height
        val barcodeFormat: BarcodeFormat
        if (content == null || content == "") {
            return null
        }
        when (format) {
            0 -> barcodeFormat = BarcodeFormat.UPC_A
            1 -> barcodeFormat = BarcodeFormat.UPC_E
            2 -> barcodeFormat = BarcodeFormat.EAN_13
            3 -> barcodeFormat = BarcodeFormat.EAN_8
            4 -> barcodeFormat = BarcodeFormat.CODE_39
            5 -> barcodeFormat = BarcodeFormat.ITF
            6 -> barcodeFormat = BarcodeFormat.CODABAR
            7 -> barcodeFormat = BarcodeFormat.CODE_93
            8 -> barcodeFormat = BarcodeFormat.CODE_128
            9 -> barcodeFormat = BarcodeFormat.QR_CODE
            else -> {
                barcodeFormat = BarcodeFormat.QR_CODE
                height = width
            }
        }
        val qrCodeWriter = MultiFormatWriter()
        val hints: MutableMap<EncodeHintType, Any?> = HashMap()
        hints[EncodeHintType.CHARACTER_SET] = "GBK"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        return try {
            val encode = qrCodeWriter.encode(content, barcodeFormat, width, height, hints)
            val pixels = IntArray(width * height)
            for (i in 0 until height) {
                for (j in 0 until width) {
                    if (encode[j, i]) {
                        pixels[i * width + j] = 0
                    } else {
                        pixels[i * width + j] = -1
                    }
                }
            }
            Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565)
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        } catch (e2: IllegalArgumentException) {
            e2.printStackTrace()
            null
        }
    }
}

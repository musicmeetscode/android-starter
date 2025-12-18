package ug.global.temp.util

import android.graphics.Bitmap
import androidx.core.view.MotionEventCompat
import androidx.core.view.PointerIconCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.lang.Byte.MIN_VALUE
import java.lang.Double
import java.util.Hashtable
import java.util.Locale

object BytesUtil {
    fun getHexStringFromBytes(data: ByteArray?): String? {
        if (data == null || data.isEmpty()) {
            return null
        }
        val sb = StringBuilder(data.size * 2)
        for (i in data.indices) {
            sb.append("0123456789ABCDEF"[data[i].toInt() and 240 shr 4])
            sb.append("0123456789ABCDEF"[data[i].toInt() and 15 shr 0])
        }
        return sb.toString()
    }

    private fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }

    fun getBytesFromHexString(hexstring: String?): ByteArray? {
        if (hexstring == null || hexstring == "") {
            return null
        }
        val hexstring2 = hexstring.replace(" ", "").uppercase(Locale.getDefault())
        val size = hexstring2.length / 2
        val hexarray = hexstring2.toCharArray()
        val rv = ByteArray(size)
        for (i in 0 until size) {
            val pos = i * 2
            rv[i] = (charToByte(hexarray[pos]).toInt() shl 4 or charToByte(hexarray[pos + 1]).toInt()).toByte()
        }
        return rv
    }

    fun getBytesFromDecString(decstring: String?): ByteArray? {
        if (decstring == null || decstring == "") {
            return null
        }
        val decstring2 = decstring.replace(" ", "")
        val size = decstring2.length / 2
        val decarray = decstring2.toCharArray()
        val rv = ByteArray(size)
        for (i in 0 until size) {
            val pos = i * 2
            rv[i] = (charToByte(decarray[pos]) * 10 + charToByte(decarray[pos + 1])).toByte()
        }
        return rv
    }

    fun byteMerger(byte_1: ByteArray?, byte_2: ByteArray?): ByteArray {
        val byte_3 = ByteArray(byte_1!!.size + byte_2!!.size)
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.size)
        System.arraycopy(byte_2, 0, byte_3, byte_1.size, byte_2.size)
        return byte_3
    }

    fun byteMerger(byteList: Array<ByteArray>): ByteArray {
        var length = 0
        for (length2 in byteList) {
            length += length2.size
        }
        val result = ByteArray(length)
        var index = 0
        for (i in byteList.indices) {
            val nowByte = byteList[i]
            for (k in byteList[i].indices) {
                result[index] = nowByte[k]
                index++
            }
        }
        for (i2 in 0 until index) {
        }
        return result
    }

    fun initTable(h: Int, w: Int): ByteArray {
        val hh = h * 32
        val ww = w * 4
        val data = ByteArray(hh * ww + 5)
        data[0] = ww.toByte()
        data[1] = (ww shr 8).toByte()
        data[2] = hh.toByte()
        data[3] = (hh shr 8).toByte()
        var k = 4
        var m = 31
        for (i in 0 until h) {
            for (j in 0 until w) {
                val k2 = k + 1
                data[k] = -1
                val k3 = k2 + 1
                data[k2] = -1
                val k4 = k3 + 1
                data[k3] = -1
                k = k4 + 1
                data[k4] = -1
            }
            if (i == h - 1) {
                m = 30
            }
            for (t in 0 until m) {
                for (j2 in 0 until w - 1) {
                    val k5 = k + 1
                    data[k] = MIN_VALUE
                    val k6 = k5 + 1
                    data[k5] = 0
                    val k7 = k6 + 1
                    data[k6] = 0
                    k = k7 + 1
                    data[k7] = 0
                }
                val k8 = k + 1
                data[k] = MIN_VALUE
                val k9 = k8 + 1
                data[k8] = 0
                val k10 = k9 + 1
                data[k9] = 0
                k = k10 + 1
                data[k10] = 1
            }
        }
        for (j3 in 0 until w) {
            val k11 = k + 1
            data[k] = -1
            val k12 = k11 + 1
            data[k11] = -1
            val k13 = k12 + 1
            data[k12] = -1
            k = k13 + 1
            data[k13] = -1
        }
        val j4 = k + 1
        data[k] = 10
        return data
    }

    fun getZXingQRCode(data: String?, size: Int): ByteArray? {
        return try {
            val hints = Hashtable<EncodeHintType, String?>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            getBytesFromBitMatrix(QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size, hints))
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    fun getBytesFromBitMatrix(bits: BitMatrix?): ByteArray? {
        if (bits == null) {
            return null
        }
        val h = bits.height
        val w = (bits.width + 7) / 8
        val rv = ByteArray(h * w + 4)
        rv[0] = w.toByte()
        rv[1] = (w shr 8).toByte()
        rv[2] = h.toByte()
        rv[3] = (h shr 8).toByte()
        var k = 4
        for (i in 0 until h) {
            for (j in 0 until w) {
                for (n in 0..7) {
                    rv[k] = (rv[k] + rv[k] + getBitMatrixColor(bits, j * 8 + n, i)).toByte()
                }
                k++
            }
        }
        return rv
    }

    private fun getBitMatrixColor(bits: BitMatrix, x: Int, y: Int): Byte {
        val width = bits.width
        val height = bits.height
        return if (x >= width || y >= height || x < 0 || y < 0 || !bits[x, y]) {
            0
        } else 1
    }

    fun getBytesFromBitMap(bitmap: Bitmap): ByteArray {
        val width = bitmap.width
        val height = bitmap.height
        val bw = (width - 1) / 8 + 1
        val rv = ByteArray(height * bw + 4)
        rv[0] = bw.toByte()
        rv[1] = (bw shr 8).toByte()
        rv[2] = height.toByte()
        rv[3] = (height shr 8).toByte()
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in 0 until height) {
            for (j in 0 until width) {
                val clr = pixels[width * i + j]
                rv[bw * i + j / 8 + 4] = (rv[bw * i + j / 8 + 4].toInt() or (RGB2Gray(16711680 and clr shr 16, 65280 and clr shr 8, clr and 255).toInt() shl 7 - j % 8)).toByte()
            }
        }
        return rv
    }

    fun getBytesFromBitMap(bitmap: Bitmap, mode: Int): ByteArray {
        val i: Int
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        var b: Byte = 42
        if (mode == 0) {
            i = 8
        } else if (mode == 1) {
            i = 8
        } else if (mode == 32 || mode == 33) {
            val res = ByteArray(width * height / 8 + height * 5 / 24)
            var i3 = 3
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            var i4 = 0
            while (i4 < height / 24) {
                res[(width * 3 + 5) * i4 + 0] = ESCUtil.ESC
                res[(width * 3 + 5) * i4 + 1] = b
                res[(width * 3 + 5) * i4 + 2] = mode.toByte()
                res[(width * 3 + 5) * i4 + i3] = (width % 256).toByte()
                res[(width * 3 + 5) * i4 + 4] = (width / 256).toByte()
                var j = 0
                while (j < width) {
                    var n = 0
                    while (n < i3) {
                        var gray: Byte = 0
                        for (m in 0..7) {
                            val clr = pixels[(i4 * 24 + m + n * 8) * width + j]
                            gray = (RGB2Gray(clr and 16711680 shr 16, clr and MotionEventCompat.ACTION_POINTER_INDEX_MASK shr 8, clr and 255).toInt() shl 7 - m or gray.toInt()).toByte()
                        }
                        res[j * 3 + 5 + (width * 3 + 5) * i4 + n] = gray
                        n++
                        i3 = 3
                    }
                    j++
                    i3 = 3
                }
                i4++
                i3 = 3
                b = 42
            }
            return res
        } else {
            return byteArrayOf(10)
        }
        val res2 = ByteArray(width * height / i + height * 5 / i)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i5 in 0 until height / 8) {
            res2[(width + 5) * i5 + 0] = ESCUtil.ESC
            res2[(width + 5) * i5 + 1] = 42
            res2[(width + 5) * i5 + 2] = mode.toByte()
            res2[(width + 5) * i5 + 3] = (width % 256).toByte()
            res2[(width + 5) * i5 + 4] = (width / 256).toByte()
            for (j2 in 0 until width) {
                var gray2: Byte = 0
                for (m2 in 0 until i) {
                    val clr2 = pixels[(i5 * 8 + m2) * width + j2]
                    gray2 = (RGB2Gray(clr2 and 16711680 shr 16, clr2 and MotionEventCompat.ACTION_POINTER_INDEX_MASK shr 8, clr2 and 255).toInt() shl 7 - m2 or gray2.toInt()).toByte()
                }
                res2[j2 + 5 + (width + 5) * i5] = gray2
            }
        }
        return res2
    }

    private fun RGB2Gray(r: Int, g: Int, b: Int): Byte {
        val d = r.toDouble()
        Double.isNaN(d)
        val d2 = g.toDouble()
        Double.isNaN(d2)
        val d3 = d * 0.299 + d2 * 0.587
        val d4 = b.toDouble()
        Double.isNaN(d4)
        return if ((d3 + d4 * 0.114).toInt() < 200) 1.toByte() else 0
    }

    fun initBlackBlock(w: Int): ByteArray {
        var k: Int
        val ww = (w + 7) / 8
        val n = (ww + 11) / 12
        val hh = n * 24
        val data = ByteArray(hh * ww + 5)
        data[0] = ww.toByte()
        data[1] = (ww shr 8).toByte()
        data[2] = hh.toByte()
        data[3] = (hh shr 8).toByte()
        var k2 = 4
        for (i in 0 until n) {
            for (j in 0..23) {
                for (m in 0 until ww) {
                    if (m / 12 == i) {
                        k = k2 + 1
                        data[k2] = -1
                    } else {
                        k = k2 + 1
                        data[k2] = 0
                    }
                    k2 = k
                }
            }
        }
        val i2 = k2 + 1
        data[k2] = 10
        return data
    }

    fun initBlackBlock(h: Int, w: Int): ByteArray {
        val ww = (w - 1) / 8 + 1
        val data = ByteArray(h * ww + 6)
        data[0] = ww.toByte()
        data[1] = (ww shr 8).toByte()
        data[2] = h.toByte()
        data[3] = (h shr 8).toByte()
        var k = 4
        for (i in 0 until h) {
            var j = 0
            while (j < ww) {
                data[k] = -1
                j++
                k++
            }
        }
        val i2 = k + 1
        data[k] = 0
        val i3 = i2 + 1
        data[i2] = 0
        return data
    }

    val baiduTestBytes: ByteArray
        get() = byteArrayOf(
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            17,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.ESC,
            69,
            1,
            ESCUtil.ESC,
            71,
            1,
            -79,
            -66,
            -75,
            -22,
            -63,
            -12,
            -76,
            -26,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            17,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.ESC,
            69,
            1,
            ESCUtil.ESC,
            71,
            1,
            ESCUtil.ESC,
            97,
            1,
            35,
            49,
            53,
            ESCUtil.SP,
            -80,
            -39,
            -74,
            -56,
            -51,
            -30,
            -62,
            -12,
            10,
            91,
            -69,
            -11,
            -75,
            -67,
            -72,
            -74,
            -65,
            -18,
            93,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            1,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            -58,
            -38,
            -51,
            -5,
            -53,
            -51,
            -76,
            -17,
            -54,
            -79,
            -68,
            -28,
            -93,
            -70,
            -63,
            -94,
            -68,
            -76,
            -59,
            -28,
            -53,
            -51,
            10,
            -74,
            -87,
            -75,
            -91,
            -79,
            -72,
            -41,
            -94,
            -93,
            -70,
            -57,
            -21,
            -53,
            -51,
            -75,
            -67,
            -65,
            -4,
            -65,
            -58,
            -50,
            -9,
            -61,
            -59,
            44,
            -78,
            -69,
            -46,
            -86,
            -64,
            -79,
            10,
            -73,
            -94,
            -58,
            -79,
            -48,
            -59,
            -49,
            -94,
            -93,
            -70,
            -80,
            -39,
            -74,
            -56,
            -51,
            -30,
            -62,
            -12,
            -73,
            -94,
            -58,
            -79,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            -74,
            -87,
            -75,
            -91,
            -79,
            -32,
            -70,
            -59,
            -93,
            -70,
            49,
            52,
            49,
            56,
            55,
            49,
            56,
            54,
            57,
            49,
            49,
            54,
            56,
            57,
            10,
            -49,
            -62,
            -75,
            -91,
            -54,
            -79,
            -68,
            -28,
            -93,
            -70,
            50,
            48,
            49,
            52,
            45,
            49,
            50,
            45,
            49,
            54,
            ESCUtil.SP,
            49,
            54,
            58,
            51,
            49,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            1,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            -78,
            -53,
            -58,
            -73,
            -61,
            -5,
            -77,
            -58,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            -54,
            -3,
            -63,
            -65,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            -67,
            -16,
            -74,
            -18,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            1,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            -49,
            -29,
            -64,
            -79,
            -61,
            -26,
            -52,
            -41,
            -78,
            -51,
            ESCUtil.ESC,
            36,
            -14,
            0,
            49,
            ESCUtil.ESC,
            36,
            37,
            1,
            -93,
            -92,
            52,
            48,
            46,
            48,
            48,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            1,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            -53,
            -40,
            -54,
            -77,
            -52,
            -20,
            -49,
            -62,
            -70,
            -70,
            -79,
            -92,
            ESCUtil.ESC,
            36,
            -14,
            0,
            49,
            ESCUtil.ESC,
            36,
            37,
            1,
            -93,
            -92,
            51,
            56,
            46,
            48,
            48,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            1,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            -48,
            -43,
            -61,
            -5,
            -93,
            -70,
            -80,
            -39,
            -74,
            -56,
            -78,
            -30,
            -54,
            -44,
            10,
            -75,
            -40,
            -42,
            -73,
            -93,
            -70,
            -65,
            -4,
            -65,
            -58,
            -65,
            -58,
            -68,
            -68,
            -76,
            -13,
            -49,
            -61,
            10,
            -75,
            -25,
            -69,
            -80,
            -93,
            -70,
            49,
            56,
            55,
            48,
            48,
            48,
            48,
            48,
            48,
            48,
            48,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            -80,
            -39,
            -74,
            -56,
            -78,
            -30,
            -54,
            -44,
            -55,
            -52,
            -69,
            -89,
            10,
            49,
            56,
            55,
            48,
            48,
            48,
            48,
            48,
            48,
            48,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            ESCUtil.ESC,
            77,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            71,
            0,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.ESC,
            97,
            1,
            35,
            49,
            53,
            ESCUtil.SP,
            -80,
            -39,
            -74,
            -56,
            -51,
            -30,
            -62,
            -12,
            ESCUtil.SP,
            ESCUtil.SP,
            49,
            49,
            -44,
            -62,
            48,
            57,
            -56,
            -43,
            ESCUtil.SP,
            49,
            55,
            58,
            53,
            48,
            58,
            51,
            48,
            10,
            10,
            10,
            10,
            10
        )
    val meituanBill: ByteArray
        get() = byteArrayOf(
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            1,
            ESCUtil.GS,
            33,
            17,
            -93,
            -93,
            49,
            ESCUtil.SP,
            ESCUtil.SP,
            -61,
            -64,
            -51,
            -59,
            -78,
            -30,
            -54,
            -44,
            10,
            10,
            ESCUtil.GS,
            33,
            0,
            -44,
            -63,
            -49,
            -29,
            -72,
            -37,
            -54,
            -67,
            -55,
            -43,
            -64,
            -80,
            40,
            -75,
            -38,
            49,
            -63,
            -86,
            41,
            10,
            ESCUtil.ESC,
            33,
            ESCUtil.DLE,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            42,
            ESCUtil.SP,
            42,
            ESCUtil.SP,
            42,
            ESCUtil.SP,
            42,
            ESCUtil.SP,
            42,
            ESCUtil.SP,
            42,
            ESCUtil.SP,
            ESCUtil.SP,
            -44,
            -92,
            -74,
            -87,
            -75,
            -91,
            ESCUtil.SP,
            ESCUtil.SP,
            42,
            ESCUtil.SP,
            42,
            ESCUtil.SP,
            42,
            ESCUtil.SP,
            42,
            ESCUtil.SP,
            42,
            ESCUtil.SP,
            42,
            10,
            -58,
            -38,
            -51,
            -5,
            -53,
            -51,
            -76,
            -17,
            -54,
            -79,
            -68,
            -28,
            58,
            ESCUtil.SP,
            91,
            49,
            56,
            58,
            48,
            48,
            93,
            10,
            ESCUtil.GS,
            33,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            97,
            0,
            -49,
            -62,
            -75,
            -91,
            -54,
            -79,
            -68,
            -28,
            58,
            48,
            49,
            45,
            48,
            49,
            ESCUtil.SP,
            49,
            50,
            58,
            48,
            48,
            10,
            ESCUtil.ESC,
            33,
            ESCUtil.DLE,
            -79,
            -72,
            -41,
            -94,
            58,
            -79,
            -16,
            -52,
            -85,
            -64,
            -79,
            10,
            ESCUtil.GS,
            33,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            -78,
            -53,
            -61,
            -5,
            9,
            9,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            -54,
            -3,
            -63,
            -65,
            9,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            -48,
            -95,
            -68,
            -58,
            9,
            10,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            33,
            ESCUtil.DLE,
            -70,
            -20,
            -55,
            -43,
            -56,
            -30,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            120,
            49,
            9,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            49,
            50,
            10,
            ESCUtil.GS,
            33,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            -59,
            -28,
            -53,
            -51,
            -73,
            -47,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            53,
            10,
            -78,
            -51,
            -70,
            -48,
            -73,
            -47,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            49,
            10,
            91,
            -77,
            -84,
            -54,
            -79,
            -59,
            -30,
            -72,
            -74,
            93,
            ESCUtil.SP,
            45,
            -49,
            -22,
            -68,
            -5,
            -74,
            -87,
            -75,
            -91,
            10,
            -65,
            -55,
            -65,
            -38,
            -65,
            -55,
            -64,
            -42,
            58,
            120,
            49,
            10,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            33,
            ESCUtil.DLE,
            -70,
            -49,
            -68,
            -58,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            49,
            56,
            -44,
            -86,
            10,
            ESCUtil.ESC,
            64,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.GS,
            33,
            17,
            -43,
            -59,
            42,
            ESCUtil.SP,
            49,
            56,
            51,
            49,
            50,
            51,
            52,
            53,
            54,
            55,
            56,
            10,
            -75,
            -40,
            -42,
            -73,
            -48,
            -59,
            -49,
            -94,
            10,
            ESCUtil.GS,
            33,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            1,
            ESCUtil.GS,
            33,
            17,
            -93,
            -93,
            49,
            ESCUtil.SP,
            ESCUtil.SP,
            -61,
            -64,
            -51,
            -59,
            -78,
            -30,
            -54,
            -44,
            10,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            64,
            10,
            10,
            10,
            ESCUtil.GS,
            86,
            0
        )
    val erlmoData: ByteArray
        get() {
            val rv = ByteArray(PointerIconCompat.TYPE_ZOOM_IN)
            // fill-array-data instruction
            rv[0] = 27
            rv[1] = 64
            rv[2] = 27
            rv[3] = 97
            rv[4] = 0
            rv[5] = 42
            rv[6] = 42
            rv[7] = 42
            rv[8] = 42
            rv[9] = 42
            rv[10] = 42
            rv[11] = 42
            rv[12] = 32
            rv[13] = 29
            rv[14] = 33
            rv[15] = 17
            rv[16] = 35
            rv[17] = 49
            rv[18] = 29
            rv[19] = 33
            rv[20] = 0
            rv[21] = 0
            rv[22] = -74
            rv[23] = -10
            rv[24] = -63
            rv[25] = -53
            rv[26] = -61
            rv[27] = -76
            rv[28] = -51
            rv[29] = -30
            rv[30] = -62
            rv[31] = -12
            rv[32] = -75
            rv[33] = -91
            rv[34] = 32
            rv[35] = 42
            rv[36] = 42
            rv[37] = 42
            rv[38] = 42
            rv[39] = 42
            rv[40] = 42
            rv[41] = 42
            rv[42] = 10
            rv[43] = 10
            rv[44] = 27
            rv[45] = 97
            rv[46] = 1
            rv[47] = -65
            rv[48] = -88
            rv[49] = -56
            rv[50] = -8
            rv[51] = -59
            rv[52] = -5
            rv[53] = -56
            rv[54] = -8
            rv[55] = 10
            rv[56] = 10
            rv[57] = 27
            rv[58] = 97
            rv[59] = 0
            rv[60] = 27
            rv[61] = 97
            rv[62] = 1
            rv[63] = 29
            rv[64] = 33
            rv[65] = 17
            rv[66] = 45
            rv[67] = 45
            rv[68] = -46
            rv[69] = -47
            rv[70] = -42
            rv[71] = -89
            rv[72] = -72
            rv[73] = -74
            rv[74] = 45
            rv[75] = 45
            rv[76] = 29
            rv[77] = 33
            rv[78] = 0
            rv[79] = 0
            rv[80] = 10
            rv[81] = 10
            rv[82] = 27
            rv[83] = 97
            rv[84] = 0
            rv[85] = 27
            rv[86] = 97
            rv[87] = 1
            rv[88] = 29
            rv[89] = 33
            rv[90] = 17
            rv[91] = -44
            rv[92] = -92
            rv[93] = -68
            rv[94] = -58
            rv[95] = 49
            rv[96] = 57
            rv[97] = 58
            rv[98] = 48
            rv[99] = 48
            rv[100] = -53
            rv[101] = -51
            rv[102] = -76
            rv[103] = -17
            rv[104] = 29
            rv[105] = 33
            rv[106] = 0
            rv[107] = 0
            rv[108] = 10
            rv[109] = 10
            rv[110] = 27
            rv[111] = 97
            rv[112] = 0
            rv[113] = 91
            rv[114] = -49
            rv[115] = -62
            rv[116] = -75
            rv[117] = -91
            rv[118] = -54
            rv[119] = -79
            rv[120] = -68
            rv[121] = -28
            rv[122] = 93
            rv[123] = 50
            rv[124] = 48
            rv[125] = 49
            rv[126] = 52
            rv[127] = 45
            rv[128] = 49
            rv[129] = 50
            rv[130] = 45
            rv[131] = 48
            rv[132] = 51
            rv[133] = 32
            rv[134] = 49
            rv[135] = 54
            rv[136] = 58
            rv[137] = 50
            rv[138] = 49
            rv[139] = 10
            rv[140] = 91
            rv[141] = -79
            rv[142] = -72
            rv[143] = -41
            rv[144] = -94
            rv[145] = 93
            rv[146] = 29
            rv[147] = 33
            rv[148] = 1
            rv[149] = -78
            rv[150] = -69
            rv[151] = -77
            rv[152] = -44
            rv[153] = -64
            rv[154] = -79
            rv[155] = 32
            rv[156] = -64
            rv[157] = -79
            rv[158] = -46
            rv[159] = -69
            rv[160] = -75
            rv[161] = -29
            rv[162] = 32
            rv[163] = -74
            rv[164] = -32
            rv[165] = -68
            rv[166] = -45
            rv[167] = -61
            rv[168] = -41
            rv[169] = 32
            rv[170] = -61
            rv[171] = -69
            rv[172] = -63
            rv[173] = -29
            rv[174] = -57
            rv[175] = -82
            rv[176] = 29
            rv[177] = 33
            rv[178] = 0
            rv[179] = 10
            rv[180] = 91
            rv[181] = -73
            rv[182] = -94
            rv[183] = -58
            rv[184] = -79
            rv[185] = 93
            rv[186] = -43
            rv[187] = -30
            rv[188] = -54
            rv[189] = -57
            rv[190] = -46
            rv[191] = -69
            rv[192] = -72
            rv[193] = -10
            rv[194] = -73
            rv[195] = -94
            rv[196] = -58
            rv[197] = -79
            rv[198] = -52
            rv[199] = -89
            rv[200] = -51
            rv[201] = -73
            rv[202] = 10
            rv[203] = 45
            rv[204] = 45
            rv[205] = 45
            rv[206] = 45
            rv[207] = 45
            rv[208] = 45
            rv[209] = 45
            rv[210] = 45
            rv[211] = 45
            rv[212] = 45
            rv[213] = 45
            rv[214] = 45
            rv[215] = 45
            rv[216] = 45
            rv[217] = 45
            rv[218] = 45
            rv[219] = 45
            rv[220] = 45
            rv[221] = 45
            rv[222] = 45
            rv[223] = 45
            rv[224] = 45
            rv[225] = 45
            rv[226] = 45
            rv[227] = 45
            rv[228] = 45
            rv[229] = 45
            rv[230] = 45
            rv[231] = 45
            rv[232] = 45
            rv[233] = 45
            rv[234] = 45
            rv[235] = 10
            rv[236] = -78
            rv[237] = -53
            rv[238] = -61
            rv[239] = -5
            rv[240] = 32
            rv[241] = 32
            rv[242] = 32
            rv[243] = 32
            rv[244] = 32
            rv[245] = 32
            rv[246] = 32
            rv[247] = 32
            rv[248] = 32
            rv[249] = 32
            rv[250] = 32
            rv[251] = 32
            rv[252] = 32
            rv[253] = 32
            rv[254] = 32
            rv[255] = 32
            rv[256] = 32
            rv[257] = 32
            rv[258] = -54
            rv[259] = -3
            rv[260] = -63
            rv[261] = -65
            rv[262] = 32
            rv[263] = 32
            rv[264] = -48
            rv[265] = -95
            rv[266] = -68
            rv[267] = -58
            rv[268] = 10
            rv[269] = 45
            rv[270] = 45
            rv[271] = 45
            rv[272] = 45
            rv[273] = 45
            rv[274] = 45
            rv[275] = 45
            rv[276] = 45
            rv[277] = 45
            rv[278] = 45
            rv[279] = 45
            rv[280] = 32
            rv[281] = 49
            rv[282] = -70
            rv[283] = -59
            rv[284] = -64
            rv[285] = -70
            rv[286] = -41
            rv[287] = -45
            rv[288] = 32
            rv[289] = 45
            rv[290] = 45
            rv[291] = 45
            rv[292] = 45
            rv[293] = 45
            rv[294] = 45
            rv[295] = 45
            rv[296] = 45
            rv[297] = 45
            rv[298] = 45
            rv[299] = 45
            rv[300] = 10
            rv[301] = 29
            rv[302] = 33
            rv[303] = 1
            rv[304] = -78
            rv[305] = -30
            rv[306] = -54
            rv[307] = -44
            rv[308] = -61
            rv[309] = -64
            rv[310] = -54
            rv[311] = -77
            rv[312] = -46
            rv[313] = -69
            rv[314] = 29
            rv[315] = 33
            rv[316] = 0
            rv[317] = 32
            rv[318] = 32
            rv[319] = 32
            rv[320] = 32
            rv[321] = 32
            rv[322] = 32
            rv[323] = 32
            rv[324] = 32
            rv[325] = 32
            rv[326] = 32
            rv[327] = 32
            rv[328] = 32
            rv[329] = 32
            rv[330] = 29
            rv[331] = 33
            rv[332] = 1
            rv[333] = 32
            rv[334] = 120
            rv[335] = 52
            rv[336] = 29
            rv[337] = 33
            rv[338] = 0
            rv[339] = 29
            rv[340] = 33
            rv[341] = 1
            rv[342] = 29
            rv[343] = 33
            rv[344] = 0
            rv[345] = 32
            rv[346] = 32
            rv[347] = 32
            rv[348] = 32
            rv[349] = 32
            rv[350] = 29
            rv[351] = 33
            rv[352] = 1
            rv[353] = 52
            rv[354] = 29
            rv[355] = 33
            rv[356] = 0
            rv[357] = 10
            rv[358] = 29
            rv[359] = 33
            rv[360] = 1
            rv[361] = -78
            rv[362] = -30
            rv[363] = -54
            rv[364] = -44
            rv[365] = -61
            rv[366] = -64
            rv[367] = -54
            rv[368] = -77
            rv[369] = -74
            rv[370] = -2
            rv[371] = 29
            rv[372] = 33
            rv[373] = 0
            rv[374] = 32
            rv[375] = 32
            rv[376] = 32
            rv[377] = 32
            rv[378] = 32
            rv[379] = 32
            rv[380] = 32
            rv[381] = 32
            rv[382] = 32
            rv[383] = 32
            rv[384] = 32
            rv[385] = 32
            rv[386] = 32
            rv[387] = 29
            rv[388] = 33
            rv[389] = 1
            rv[390] = 32
            rv[391] = 120
            rv[392] = 54
            rv[393] = 29
            rv[394] = 33
            rv[395] = 0
            rv[396] = 29
            rv[397] = 33
            rv[398] = 1
            rv[399] = 29
            rv[400] = 33
            rv[401] = 0
            rv[402] = 32
            rv[403] = 32
            rv[404] = 32
            rv[405] = 32
            rv[406] = 32
            rv[407] = 29
            rv[408] = 33
            rv[409] = 1
            rv[410] = 54
            rv[411] = 29
            rv[412] = 33
            rv[413] = 0
            rv[414] = 10
            rv[415] = 29
            rv[416] = 33
            rv[417] = 1
            rv[418] = -78
            rv[419] = -30
            rv[420] = -54
            rv[421] = -44
            rv[422] = -61
            rv[423] = -64
            rv[424] = -54
            rv[425] = -77
            rv[426] = -56
            rv[427] = -3
            rv[428] = 29
            rv[429] = 33
            rv[430] = 0
            rv[431] = 32
            rv[432] = 32
            rv[433] = 32
            rv[434] = 32
            rv[435] = 32
            rv[436] = 32
            rv[437] = 32
            rv[438] = 32
            rv[439] = 32
            rv[440] = 32
            rv[441] = 32
            rv[442] = 32
            rv[443] = 32
            rv[444] = 29
            rv[445] = 33
            rv[446] = 1
            rv[447] = 32
            rv[448] = 120
            rv[449] = 50
            rv[450] = 29
            rv[451] = 33
            rv[452] = 0
            rv[453] = 29
            rv[454] = 33
            rv[455] = 1
            rv[456] = 29
            rv[457] = 33
            rv[458] = 0
            rv[459] = 32
            rv[460] = 32
            rv[461] = 32
            rv[462] = 32
            rv[463] = 32
            rv[464] = 29
            rv[465] = 33
            rv[466] = 1
            rv[467] = 50
            rv[468] = 29
            rv[469] = 33
            rv[470] = 0
            rv[471] = 10
            rv[472] = 45
            rv[473] = 45
            rv[474] = 45
            rv[475] = 45
            rv[476] = 45
            rv[477] = 45
            rv[478] = 45
            rv[479] = 45
            rv[480] = 45
            rv[481] = 45
            rv[482] = 45
            rv[483] = 32
            rv[484] = 50
            rv[485] = -70
            rv[486] = -59
            rv[487] = -64
            rv[488] = -70
            rv[489] = -41
            rv[490] = -45
            rv[491] = 32
            rv[492] = 45
            rv[493] = 45
            rv[494] = 45
            rv[495] = 45
            rv[496] = 45
            rv[497] = 45
            rv[498] = 45
            rv[499] = 45
            rv[500] = 45
            rv[501] = 45
            rv[502] = 45
            rv[503] = 10
            rv[504] = 29
            rv[505] = 33
            rv[506] = 1
            rv[507] = -78
            rv[508] = -30
            rv[509] = -54
            rv[510] = -44
            rv[511] = 49
            rv[512] = 29
            rv[513] = 33
            rv[514] = 0
            rv[515] = 32
            rv[516] = 32
            rv[517] = 32
            rv[518] = 32
            rv[519] = 32
            rv[520] = 32
            rv[521] = 32
            rv[522] = 32
            rv[523] = 32
            rv[524] = 32
            rv[525] = 32
            rv[526] = 32
            rv[527] = 32
            rv[528] = 32
            rv[529] = 32
            rv[530] = 32
            rv[531] = 32
            rv[532] = 32
            rv[533] = 29
            rv[534] = 33
            rv[535] = 1
            rv[536] = 32
            rv[537] = 120
            rv[538] = 49
            rv[539] = 29
            rv[540] = 33
            rv[541] = 0
            rv[542] = 29
            rv[543] = 33
            rv[544] = 1
            rv[545] = 29
            rv[546] = 33
            rv[547] = 0
            rv[548] = 32
            rv[549] = 32
            rv[550] = 32
            rv[551] = 32
            rv[552] = 32
            rv[553] = 29
            rv[554] = 33
            rv[555] = 1
            rv[556] = 49
            rv[557] = 29
            rv[558] = 33
            rv[559] = 0
            rv[560] = 10
            rv[561] = 29
            rv[562] = 33
            rv[563] = 1
            rv[564] = -78
            rv[565] = -30
            rv[566] = -54
            rv[567] = -44
            rv[568] = 50
            rv[569] = 29
            rv[570] = 33
            rv[571] = 0
            rv[572] = 32
            rv[573] = 32
            rv[574] = 32
            rv[575] = 32
            rv[576] = 32
            rv[577] = 32
            rv[578] = 32
            rv[579] = 32
            rv[580] = 32
            rv[581] = 32
            rv[582] = 32
            rv[583] = 32
            rv[584] = 32
            rv[585] = 32
            rv[586] = 32
            rv[587] = 32
            rv[588] = 32
            rv[589] = 32
            rv[590] = 29
            rv[591] = 33
            rv[592] = 1
            rv[593] = 32
            rv[594] = 120
            rv[595] = 49
            rv[596] = 29
            rv[597] = 33
            rv[598] = 0
            rv[599] = 29
            rv[600] = 33
            rv[601] = 1
            rv[602] = 29
            rv[603] = 33
            rv[604] = 0
            rv[605] = 32
            rv[606] = 32
            rv[607] = 32
            rv[608] = 32
            rv[609] = 32
            rv[610] = 29
            rv[611] = 33
            rv[612] = 1
            rv[613] = 49
            rv[614] = 29
            rv[615] = 33
            rv[616] = 0
            rv[617] = 10
            rv[618] = 29
            rv[619] = 33
            rv[620] = 1
            rv[621] = -78
            rv[622] = -30
            rv[623] = -54
            rv[624] = -44
            rv[625] = 51
            rv[626] = 29
            rv[627] = 33
            rv[628] = 0
            rv[629] = 32
            rv[630] = 32
            rv[631] = 32
            rv[632] = 32
            rv[633] = 32
            rv[634] = 32
            rv[635] = 32
            rv[636] = 32
            rv[637] = 32
            rv[638] = 32
            rv[639] = 32
            rv[640] = 32
            rv[641] = 32
            rv[642] = 32
            rv[643] = 32
            rv[644] = 32
            rv[645] = 32
            rv[646] = 32
            rv[647] = 29
            rv[648] = 33
            rv[649] = 1
            rv[650] = 32
            rv[651] = 120
            rv[652] = 49
            rv[653] = 29
            rv[654] = 33
            rv[655] = 0
            rv[656] = 29
            rv[657] = 33
            rv[658] = 1
            rv[659] = 29
            rv[660] = 33
            rv[661] = 0
            rv[662] = 32
            rv[663] = 32
            rv[664] = 32
            rv[665] = 32
            rv[666] = 29
            rv[667] = 33
            rv[668] = 1
            rv[669] = 50
            rv[670] = 51
            rv[671] = 29
            rv[672] = 33
            rv[673] = 0
            rv[674] = 10
            rv[675] = 29
            rv[676] = 33
            rv[677] = 1
            rv[678] = 40
            rv[679] = 43
            rv[680] = 41
            rv[681] = -78
            rv[682] = -30
            rv[683] = -54
            rv[684] = -44
            rv[685] = -47
            rv[686] = -13
            rv[687] = -58
            rv[688] = -8
            rv[689] = -92
            rv[690] = -50
            rv[691] = -50
            rv[692] = -9
            rv[693] = -54
            rv[694] = -67
            rv[695] = -52
            rv[696] = -16
            rv[697] = -75
            rv[698] = -29
            rv[699] = 29
            rv[700] = 33
            rv[701] = 0
            rv[702] = 32
            rv[703] = 32
            rv[704] = 29
            rv[705] = 33
            rv[706] = 1
            rv[707] = 32
            rv[708] = 120
            rv[709] = 49
            rv[710] = 29
            rv[711] = 33
            rv[712] = 0
            rv[713] = 29
            rv[714] = 33
            rv[715] = 1
            rv[716] = 29
            rv[717] = 33
            rv[718] = 0
            rv[719] = 32
            rv[720] = 32
            rv[721] = 32
            rv[722] = 32
            rv[723] = 32
            rv[724] = 29
            rv[725] = 33
            rv[726] = 1
            rv[727] = 49
            rv[728] = 29
            rv[729] = 33
            rv[730] = 0
            rv[731] = 10
            rv[732] = 29
            rv[733] = 33
            rv[734] = 1
            rv[735] = 40
            rv[736] = 43
            rv[737] = 41
            rv[738] = -78
            rv[739] = -30
            rv[740] = -54
            rv[741] = -44
            rv[742] = -53
            rv[743] = -31
            rv[744] = -64
            rv[745] = -79
            rv[746] = -60
            rv[747] = -66
            rv[748] = -74
            rv[749] = -6
            rv[750] = 29
            rv[751] = 33
            rv[752] = 0
            rv[753] = 32
            rv[754] = 32
            rv[755] = 32
            rv[756] = 32
            rv[757] = 32
            rv[758] = 32
            rv[759] = 32
            rv[760] = 32
            rv[761] = 29
            rv[762] = 33
            rv[763] = 1
            rv[764] = 32
            rv[765] = 120
            rv[766] = 49
            rv[767] = 29
            rv[768] = 33
            rv[769] = 0
            rv[770] = 29
            rv[771] = 33
            rv[772] = 1
            rv[773] = 29
            rv[774] = 33
            rv[775] = 0
            rv[776] = 32
            rv[777] = 32
            rv[778] = 32
            rv[779] = 32
            rv[780] = 32
            rv[781] = 29
            rv[782] = 33
            rv[783] = 1
            rv[784] = 56
            rv[785] = 29
            rv[786] = 33
            rv[787] = 0
            rv[788] = 10
            rv[789] = 45
            rv[790] = 45
            rv[791] = 45
            rv[792] = 45
            rv[793] = 45
            rv[794] = 45
            rv[795] = 45
            rv[796] = 45
            rv[797] = 45
            rv[798] = 45
            rv[799] = 45
            rv[800] = 32
            rv[801] = 51
            rv[802] = -70
            rv[803] = -59
            rv[804] = -64
            rv[805] = -70
            rv[806] = -41
            rv[807] = -45
            rv[808] = 32
            rv[809] = 45
            rv[810] = 45
            rv[811] = 45
            rv[812] = 45
            rv[813] = 45
            rv[814] = 45
            rv[815] = 45
            rv[816] = 45
            rv[817] = 45
            rv[818] = 45
            rv[819] = 45
            rv[820] = 10
            rv[821] = 29
            rv[822] = 33
            rv[823] = 1
            rv[824] = -78
            rv[825] = -30
            rv[826] = -54
            rv[827] = -44
            rv[828] = -78
            rv[829] = -53
            rv[830] = -58
            rv[831] = -73
            rv[832] = -61
            rv[833] = -5
            rv[834] = -41
            rv[835] = -42
            rv[836] = -70
            rv[837] = -36
            rv[838] = -77
            rv[839] = -92
            rv[840] = -70
            rv[841] = -36
            rv[842] = -77
            rv[843] = -92
            rv[844] = -70
            rv[845] = -36
            rv[846] = -77
            rv[847] = -92
            rv[848] = -70
            rv[849] = -36
            rv[850] = -77
            rv[851] = -92
            rv[852] = -70
            rv[853] = -36
            rv[854] = -77
            rv[855] = -92
            rv[856] = 29
            rv[857] = 33
            rv[858] = 0
            rv[859] = 10
            rv[860] = 32
            rv[861] = 32
            rv[862] = 32
            rv[863] = 32
            rv[864] = 32
            rv[865] = 32
            rv[866] = 32
            rv[867] = 32
            rv[868] = 32
            rv[869] = 32
            rv[870] = 32
            rv[871] = 32
            rv[872] = 32
            rv[873] = 32
            rv[874] = 32
            rv[875] = 32
            rv[876] = 32
            rv[877] = 32
            rv[878] = 32
            rv[879] = 32
            rv[880] = 32
            rv[881] = 32
            rv[882] = 32
            rv[883] = 29
            rv[884] = 33
            rv[885] = 1
            rv[886] = 32
            rv[887] = 120
            rv[888] = 49
            rv[889] = 29
            rv[890] = 33
            rv[891] = 0
            rv[892] = 29
            rv[893] = 33
            rv[894] = 1
            rv[895] = 29
            rv[896] = 33
            rv[897] = 0
            rv[898] = 32
            rv[899] = 32
            rv[900] = 32
            rv[901] = 29
            rv[902] = 33
            rv[903] = 1
            rv[904] = 51
            rv[905] = 48
            rv[906] = 48
            rv[907] = 29
            rv[908] = 33
            rv[909] = 0
            rv[910] = 10
            rv[911] = 29
            rv[912] = 33
            rv[913] = 1
            rv[914] = -78
            rv[915] = -30
            rv[916] = -54
            rv[917] = -44
            rv[918] = 29
            rv[919] = 33
            rv[920] = 0
            rv[921] = 32
            rv[922] = 32
            rv[923] = 32
            rv[924] = 32
            rv[925] = 32
            rv[926] = 32
            rv[927] = 32
            rv[928] = 32
            rv[929] = 32
            rv[930] = 32
            rv[931] = 32
            rv[932] = 32
            rv[933] = 32
            rv[934] = 32
            rv[935] = 32
            rv[936] = 32
            rv[937] = 32
            rv[938] = 32
            rv[939] = 32
            rv[940] = 29
            rv[941] = 33
            rv[942] = 1
            rv[943] = 32
            rv[944] = 120
            rv[945] = 49
            rv[946] = 29
            rv[947] = 33
            rv[948] = 0
            rv[949] = 29
            rv[950] = 33
            rv[951] = 1
            rv[952] = 29
            rv[953] = 33
            rv[954] = 0
            rv[955] = 32
            rv[956] = 32
            rv[957] = 32
            rv[958] = 32
            rv[959] = 32
            rv[960] = 29
            rv[961] = 33
            rv[962] = 1
            rv[963] = 49
            rv[964] = 29
            rv[965] = 33
            rv[966] = 0
            rv[967] = 10
            rv[968] = 45
            rv[969] = 45
            rv[970] = 45
            rv[971] = 45
            rv[972] = 45
            rv[973] = 45
            rv[974] = 45
            rv[975] = 45
            rv[976] = 45
            rv[977] = 45
            rv[978] = 45
            rv[979] = 32
            rv[980] = -58
            rv[981] = -28
            rv[982] = -53
            rv[983] = -5
            rv[984] = -73
            rv[985] = -47
            rv[986] = -45
            rv[987] = -61
            rv[988] = 32
            rv[989] = 45
            rv[990] = 45
            rv[991] = 45
            rv[992] = 45
            rv[993] = 45
            rv[994] = 45
            rv[995] = 45
            rv[996] = 45
            rv[997] = 45
            rv[998] = 45
            rv[999] = 45
            rv[1000] = 10
            rv[1001] = 29
            rv[1002] = 33
            rv[1003] = 1
            rv[1004] = -59
            rv[1005] = -28
            rv[1006] = -53
            rv[1007] = -51
            rv[1008] = -73
            rv[1009] = -47
            rv[1010] = 29
            rv[1011] = 33
            rv[1012] = 0
            rv[1013] = 32
            rv[1014] = 32
            rv[1015] = 32
            rv[1016] = 32
            rv[1017] = 32
            return rv
        }
    val koubeiData: ByteArray
        get() = byteArrayOf(
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            1,
            ESCUtil.GS,
            33,
            17,
            35,
            52,
            -65,
            -38,
            -79,
            -82,
            -51,
            -30,
            -62,
            -12,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            1,
            ESCUtil.GS,
            33,
            17,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            1,
            -73,
            -21,
            -68,
            -57,
            -69,
            -58,
            -20,
            -53,
            -68,
            -90,
            -61,
            -41,
            -73,
            -71,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            17,
            49,
            55,
            58,
            50,
            48,
            ESCUtil.SP,
            -66,
            -95,
            -65,
            -20,
            -53,
            -51,
            -76,
            -17,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            17,
            49,
            56,
            54,
            49,
            48,
            56,
            53,
            56,
            51,
            51,
            55,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            1,
            -50,
            -92,
            -48,
            -95,
            -79,
            -90,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            17,
            -76,
            -76,
            -42,
            -57,
            -52,
            -20,
            -75,
            -40,
            -71,
            -29,
            -77,
            -95,
            55,
            -70,
            -59,
            -62,
            -91,
            40,
            54,
            48,
            53,
            -54,
            -46,
            41,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            1,
            -49,
            -62,
            -75,
            -91,
            -93,
            -70,
            49,
            54,
            58,
            51,
            53,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            -78,
            -53,
            -58,
            -73,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.ESC,
            97,
            0,
            -54,
            -3,
            -63,
            -65,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.SP,
            ESCUtil.SP,
            -75,
            -91,
            -68,
            -37,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.SP,
            ESCUtil.SP,
            -67,
            -16,
            -74,
            -18,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            -69,
            -58,
            -20,
            -53,
            -50,
            -27,
            -69,
            -88,
            -56,
            -30,
            -73,
            -71,
            -93,
            -88,
            -76,
            -13,
            -93,
            -87,
            40,
            -78,
            -69,
            -64,
            -79,
            41,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.SP,
            49,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            50,
            53,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            50,
            53,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            -59,
            -28,
            -53,
            -51,
            -73,
            -47,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            50,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            33,
            1,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            ESCUtil.SP,
            -54,
            -75,
            -72,
            -74,
            -67,
            -16,
            -74,
            -18,
            -93,
            -70,
            -93,
            -92,
            50,
            55,
            10,
            ESCUtil.ESC,
            64,
            10,
            ESCUtil.ESC,
            64,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.ESC,
            97,
            1,
            ESCUtil.GS,
            33,
            17,
            -65,
            -38,
            -79,
            -82,
            -51,
            -30,
            -62,
            -12,
            10,
            ESCUtil.ESC,
            64,
            10,
            ESCUtil.ESC,
            64,
            ESCUtil.GS,
            86,
            66,
            10,
            10
        )

    fun customData(): ByteArray {
        return byteArrayOf(
            -76,
            -14,
            -45,
            -95,
            -69,
            -6,
            -41,
            -44,
            -68,
            -20,
            10,
            31,
            ESCUtil.ESC,
            31,
            83,
            ESCUtil.ESC,
            64,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            -72,
            -60,
            -79,
            -28,
            -41,
            -42,
            -68,
            -28,
            -66,
            -32,
            10,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            48,
            46,
            48,
            58,
            -78,
            -30,
            -54,
            -44,
            -78,
            -30,
            -54,
            -44,
            -78,
            -30,
            -54,
            -44,
            10,
            48,
            46,
            53,
            58,
            ESCUtil.ESC,
            ESCUtil.SP,
            ESCUtil.FF,
            -78,
            -30,
            -54,
            -44,
            -78,
            -30,
            -54,
            -44,
            -78,
            -30,
            -54,
            -44,
            10,
            ESCUtil.ESC,
            ESCUtil.SP,
            0,
            49,
            46,
            48,
            58,
            ESCUtil.ESC,
            ESCUtil.SP,
            ESCUtil.CAN,
            -78,
            -30,
            -54,
            -44,
            -78,
            -30,
            -54,
            -44,
            -78,
            -30,
            -54,
            -44,
            10,
            ESCUtil.ESC,
            ESCUtil.SP,
            0,
            50,
            46,
            48,
            58,
            ESCUtil.ESC,
            ESCUtil.SP,
            48,
            -78,
            -30,
            -54,
            -44,
            -78,
            -30,
            -54,
            -44,
            -78,
            -30,
            -54,
            -44,
            10,
            ESCUtil.ESC,
            ESCUtil.SP,
            0,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            10,
            -41,
            -42,
            -52,
            -27,
            -48,
            -89,
            -71,
            -5,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            10,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            10,
            ESCUtil.ESC,
            33,
            8,
            -68,
            -45,
            -76,
            -42,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            10,
            ESCUtil.ESC,
            33,
            0,
            ESCUtil.ESC,
            69,
            1,
            -68,
            -45,
            -76,
            -42,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            10,
            ESCUtil.ESC,
            69,
            0,
            ESCUtil.ESC,
            33,
            ESCUtil.DLE,
            -79,
            -74,
            -72,
            -33,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            10,
            ESCUtil.ESC,
            33,
            ESCUtil.SP,
            -79,
            -74,
            -65,
            -19,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            10,
            ESCUtil.GS,
            33,
            17,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            ESCUtil.GS,
            33,
            34,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            10,
            ESCUtil.GS,
            33,
            0,
            ESCUtil.ESC,
            33,
            MIN_VALUE,
            -49,
            -62,
            -69,
            -82,
            -49,
            -33,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            10,
            ESCUtil.ESC,
            33,
            0,
            ESCUtil.ESC,
            45,
            1,
            -49,
            -62,
            -69,
            -82,
            -49,
            -33,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            10,
            ESCUtil.ESC,
            45,
            0,
            ESCUtil.GS,
            66,
            1,
            ESCUtil.ESC,
            33,
            8,
            -73,
            -76,
            -80,
            -41,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            10,
            ESCUtil.GS,
            66,
            0,
            ESCUtil.ESC,
            33,
            0,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            -59,
            -59,
            -80,
            -26,
            -50,
            -69,
            -42,
            -61,
            10,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            -43,
            -30,
            -66,
            -28,
            -69,
            -80,
            -76,
            -45,
            -48,
            -48,
            -54,
            -41,
            -65,
            -86,
            -54,
            -68,
            10,
            -66,
            -8,
            -74,
            -44,
            -50,
            -69,
            -42,
            -61,
            ESCUtil.ESC,
            36,
            -64,
            0,
            -58,
            -85,
            -46,
            -58,
            56,
            -72,
            -10,
            -41,
            -42,
            10,
            -49,
            -32,
            -74,
            -44,
            -50,
            -69,
            -42,
            -61,
            ESCUtil.ESC,
            92,
            48,
            0,
            -58,
            -85,
            -46,
            -58,
            50,
            -72,
            -10,
            -41,
            -42,
            10,
            -66,
            -45,
            -41,
            -13,
            10,
            ESCUtil.ESC,
            97,
            1,
            -66,
            -45,
            -42,
            -48,
            10,
            ESCUtil.ESC,
            97,
            2,
            -66,
            -45,
            -45,
            -46,
            10,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            76,
            48,
            0,
            -55,
            -24,
            -42,
            -61,
            -41,
            -13,
            -79,
            -33,
            -66,
            -32,
            52,
            56,
            -49,
            -15,
            -53,
            -40,
            10,
            ESCUtil.GS,
            87,
            -16,
            0,
            -72,
            -60,
            -79,
            -28,
            -65,
            -55,
            -76,
            -14,
            -45,
            -95,
            -57,
            -8,
            -45,
            -14,
            -50,
            -86,
            50,
            52,
            48,
            -49,
            -15,
            -53,
            -40,
            10,
            ESCUtil.GS,
            76,
            96,
            0,
            -55,
            -24,
            -42,
            -61,
            -41,
            -13,
            -79,
            -33,
            -66,
            -32,
            57,
            54,
            -49,
            -15,
            -53,
            -40,
            10,
            ESCUtil.GS,
            87,
            120,
            0,
            -72,
            -60,
            -79,
            -28,
            -65,
            -55,
            -76,
            -14,
            -45,
            -95,
            -57,
            -8,
            -45,
            -14,
            -50,
            -86,
            49,
            50,
            48,
            -49,
            -15,
            -53,
            -40,
            10,
            ESCUtil.GS,
            76,
            0,
            0,
            ESCUtil.GS,
            87,
            MIN_VALUE,
            1,
            -60,
            -84,
            9,
            -56,
            -49,
            9,
            -52,
            -8,
            9,
            -72,
            -15,
            9,
            10,
            ESCUtil.ESC,
            68,
            1,
            2,
            4,
            8,
            10,
            0,
            -41,
            -44,
            9,
            -74,
            -88,
            9,
            -46,
            -27,
            9,
            -52,
            -8,
            9,
            -72,
            -15,
            9,
            10,
            ESCUtil.ESC,
            51,
            96,
            -55,
            -24,
            -42,
            -61,
            -48,
            -48,
            -72,
            -33,
            58,
            57,
            54,
            -75,
            -29,
            -48,
            -48,
            10,
            ESCUtil.ESC,
            51,
            64,
            -55,
            -24,
            -42,
            -61,
            -48,
            -48,
            -72,
            -33,
            58,
            54,
            52,
            -75,
            -29,
            -48,
            -48,
            10,
            ESCUtil.ESC,
            51,
            0,
            -55,
            -24,
            -42,
            -61,
            -48,
            -48,
            -72,
            -33,
            58,
            48,
            -75,
            -29,
            -48,
            -48,
            10,
            ESCUtil.ESC,
            50,
            -60,
            -84,
            -56,
            -49,
            -48,
            -48,
            -72,
            -33,
            10,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            -42,
            -82,
            -70,
            -13,
            -67,
            -85,
            -41,
            -33,
            -42,
            -67,
            10,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            74,
            64,
            -41,
            -33,
            -42,
            -67,
            54,
            52,
            -75,
            -29,
            -48,
            -48,
            10,
            ESCUtil.ESC,
            74,
            96,
            -41,
            -33,
            -42,
            -67,
            57,
            54,
            -75,
            -29,
            -48,
            -48,
            10,
            ESCUtil.ESC,
            100,
            10,
            -41,
            -33,
            -42,
            -67,
            49,
            48,
            -48,
            -48,
            10,
            ESCUtil.ESC,
            100,
            1,
            -41,
            -33,
            -42,
            -67,
            49,
            -48,
            -48,
            10,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            10,
            -76,
            -14,
            -45,
            -95,
            -52,
            -11,
            -62,
            -21,
            10,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            10,
            ESCUtil.GS,
            72,
            2,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            104,
            ESCUtil.SP,
            ESCUtil.GS,
            119,
            2,
            ESCUtil.GS,
            107,
            65,
            ESCUtil.FF,
            49,
            50,
            51,
            52,
            53,
            54,
            55,
            56,
            57,
            48,
            49,
            50,
            10,
            ESCUtil.GS,
            107,
            0,
            49,
            50,
            51,
            52,
            53,
            54,
            55,
            56,
            57,
            48,
            49,
            50,
            0,
            10,
            ESCUtil.ESC,
            97,
            1,
            ESCUtil.GS,
            104,
            64,
            ESCUtil.GS,
            119,
            4,
            ESCUtil.GS,
            107,
            66,
            8,
            48,
            49,
            50,
            51,
            52,
            53,
            54,
            53,
            10,
            ESCUtil.GS,
            107,
            1,
            48,
            49,
            50,
            51,
            52,
            53,
            54,
            53,
            0,
            10,
            ESCUtil.ESC,
            97,
            2,
            ESCUtil.GS,
            104,
            96,
            ESCUtil.GS,
            119,
            2,
            ESCUtil.GS,
            107,
            67,
            ESCUtil.CR,
            48,
            49,
            50,
            51,
            52,
            53,
            54,
            55,
            56,
            57,
            48,
            49,
            50,
            10,
            ESCUtil.GS,
            107,
            2,
            48,
            49,
            50,
            51,
            52,
            53,
            54,
            55,
            56,
            57,
            48,
            49,
            50,
            0,
            10,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            104,
            MIN_VALUE,
            ESCUtil.GS,
            119,
            5,
            ESCUtil.GS,
            107,
            68,
            8,
            48,
            49,
            50,
            51,
            52,
            53,
            54,
            53,
            10,
            ESCUtil.GS,
            107,
            3,
            48,
            49,
            50,
            51,
            52,
            53,
            54,
            53,
            0,
            10,
            ESCUtil.ESC,
            97,
            1,
            ESCUtil.GS,
            104,
            -96,
            ESCUtil.GS,
            119,
            2,
            ESCUtil.GS,
            107,
            69,
            10,
            51,
            54,
            57,
            83,
            85,
            78,
            77,
            73,
            37,
            36,
            10,
            ESCUtil.GS,
            107,
            4,
            51,
            54,
            57,
            83,
            85,
            78,
            77,
            73,
            37,
            36,
            0,
            10,
            ESCUtil.ESC,
            97,
            2,
            ESCUtil.GS,
            104,
            -64,
            ESCUtil.GS,
            119,
            3,
            ESCUtil.GS,
            107,
            70,
            ESCUtil.FF,
            48,
            49,
            50,
            51,
            52,
            53,
            54,
            55,
            56,
            57,
            48,
            49,
            10,
            ESCUtil.GS,
            107,
            5,
            48,
            49,
            50,
            51,
            52,
            53,
            54,
            55,
            56,
            57,
            48,
            49,
            0,
            10,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            104,
            -32,
            ESCUtil.GS,
            119,
            3,
            ESCUtil.GS,
            107,
            71,
            10,
            65,
            49,
            50,
            51,
            52,
            53,
            54,
            55,
            56,
            65,
            10,
            ESCUtil.ESC,
            97,
            1,
            ESCUtil.GS,
            104,
            -1,
            ESCUtil.GS,
            119,
            2,
            ESCUtil.GS,
            107,
            72,
            ESCUtil.FF,
            83,
            85,
            78,
            77,
            73,
            49,
            50,
            51,
            52,
            53,
            54,
            55,
            10,
            ESCUtil.ESC,
            97,
            0,
            ESCUtil.GS,
            104,
            -80,
            ESCUtil.GS,
            119,
            2,
            ESCUtil.GS,
            107,
            73,
            10,
            123,
            65,
            83,
            85,
            78,
            77,
            73,
            48,
            49,
            50,
            10,
            ESCUtil.GS,
            107,
            73,
            ESCUtil.FF,
            123,
            66,
            83,
            85,
            78,
            77,
            73,
            115,
            117,
            110,
            109,
            105,
            10,
            ESCUtil.GS,
            107,
            73,
            11,
            123,
            67,
            1,
            ESCUtil.FF,
            23,
            34,
            45,
            56,
            78,
            89,
            90,
            10,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            -76,
            -14,
            -45,
            -95,
            -74,
            -2,
            -50,
            -84,
            -62,
            -21,
            10,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            42,
            10,
            ESCUtil.ESC,
            97,
            1,
            ESCUtil.GS,
            40,
            107,
            3,
            0,
            49,
            67,
            9,
            ESCUtil.GS,
            40,
            107,
            3,
            0,
            49,
            69,
            50,
            ESCUtil.GS,
            40,
            107,
            11,
            0,
            49,
            80,
            48,
            -55,
            -52,
            -61,
            -41,
            -65,
            -58,
            -68,
            -68,
            ESCUtil.GS,
            40,
            107,
            3,
            0,
            49,
            81,
            48,
            10,
            ESCUtil.ESC,
            97,
            0,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            -76,
            -14,
            -45,
            -95,
            -71,
            -30,
            -43,
            -92,
            -51,
            -68,
            -49,
            -15,
            10,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            45,
            10,
            ESCUtil.ESC,
            97,
            1
        )
    }

    fun wordData(): ByteArray {
        return byteArrayOf(
            ESCUtil.ESC,
            97,
            0,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            10,
            -41,
            -42,
            -73,
            -5,
            -68,
            -81,
            -55,
            -24,
            -42,
            -61,
            10,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            61,
            10,
            ESCUtil.FS,
            38,
            ESCUtil.FS,
            67,
            0
        )
    }
}

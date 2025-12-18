package ug.global.temp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woosim.printer.WoosimCmd
import ug.global.temp.MainActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import kotlin.math.ceil
import kotlin.random.Random

/**
 * Helpers - Common Android utility functions
 * 
 * This file contains frequently used helper functions for Android development
 * to reduce code duplication and improve maintainability.
 */
object Helpers {
    private var mPrintService: BluetoothPrintService? = null
    private var title: ByteArray? = null
    private var body: ByteArray? = null
    private var footer: ByteArray? = null
    private var alertDialog: AlertDialog? = null
    private const val TAG = "PRINT_STATUS"

    @SuppressLint("HandlerLeak")
    val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val statusLayout = alertDialog?.findViewById<LinearLayout>(R.id.status_layout)
            val statusText = alertDialog?.findViewById<TextView>(R.id.status_text)
            val statusProgress = alertDialog?.findViewById<ProgressBar>(R.id.status_progress)

            when (msg.what) {
                MainActivity.MESSAGE_STATE_CHANGE -> {
                    Log.e(TAG, "MESSAGE_STATE_CHANGE: ${msg.arg1}")
                    when (msg.arg1) {
                        BluetoothPrintService.STATE_CONNECTED -> {
                            statusText?.text = "Printing..."
                            Log.e(TAG, "State: Connected. Performing print.")
                            performPrint()
                            // Dismiss after a short delay
                            Handler(Looper.getMainLooper()).postDelayed({
                                alertDialog?.dismiss()
                            }, 2000)
                        }

                        BluetoothPrintService.STATE_CONNECTING -> {
                            statusText?.text = "Connecting..."
                            Log.e(TAG, "State: Connecting...")
                            statusProgress?.visibility = View.VISIBLE
                        }

                        else -> {
                            statusProgress?.visibility = View.GONE
                        }
                    }
                }

                MainActivity.MESSAGE_TOAST -> {
                    val toastMessage = msg.data.getString("toast")
                    Log.e(TAG, "MESSAGE_TOAST: $toastMessage")
                    statusText?.text = toastMessage
                    statusProgress?.visibility = View.GONE
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog?.dismiss()
                    }, 2000)
                }
            }
        }
    }



    private fun convertDateString(inputDateString: String): String {
        val parsedDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").parse(inputDateString)
        } else {
            return inputDateString
        }
        return DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm").format(parsedDate)
    }

    fun calculateCouponsForMinutes(context: Context, minutes: Long): Int {
        if (context.packageName == "ug.global.parkingticketing.msk") {
            return (ceil((minutes.toDouble() / 30))).toInt()
        }
        if (context.packageName == "ug.global.parkingticketing.ishk" || context.packageName == "ug.global.parkingticketing.ntgm") {
            return (ceil((minutes.toDouble() / 60))).toInt()
        }
        val coupons = when (minutes) {
            in 0..61 -> 1
            in 62..91 -> 2
            in 92..121 -> 3
            in 122..151 -> 4
            in 152..181 -> 5
            in 182..211 -> 6
            in 212..241 -> 7
            in 242..271 -> 8
            in 272..301 -> 9
            in 301..331 -> 10
            in 332..361 -> 11
            in 362..391 -> 12
            in 392..421 -> 13
            in 422..451 -> 14
            in 452..481 -> 15
            in 482..511 -> 16
            in 512..541 -> 17
            in 542..571 -> 18
            else -> 19
        }
        return coupons
    }

    @SuppressLint("MissingPermission")
    private fun printOverBt(context: Context, title: ByteArray, body: ByteArray, footer: ByteArray) {
        Log.e(TAG, "printOverBt called.")
        // Dismiss any existing dialog to prevent leaks
        if (alertDialog != null && alertDialog!!.isShowing) {
            Log.e(TAG, "Dismissing existing dialog.")
            alertDialog!!.dismiss()
        }

        this.title = title
        this.body = body
        this.footer = footer

        // Initialize the BluetoothPrintService
        if (mPrintService == null) {
            Log.e(TAG, "Initializing BluetoothPrintService")
            mPrintService = BluetoothPrintService(mHandler)
        }
        // Start the service to ensure it's in the correct state
        if (mPrintService?.state == BluetoothPrintService.STATE_NONE) {
            Log.e(TAG, "Starting BluetoothPrintService")
            mPrintService?.start()
        }

        val view = View.inflate(context, R.layout.device_list, null)
        val builder = MaterialAlertDialogBuilder(context).setTitle("Select Printer").setView(view).setOnDismissListener {
            Log.e(TAG, "Dialog dismissed. Cleaning up reference.")
            alertDialog = null // Crucial for resetting state
        }
        alertDialog = builder.create()


        val pairedListView = view.findViewById<ListView>(R.id.paired_devices)
        val statusLayout = view.findViewById<LinearLayout>(R.id.status_layout)

        val pairedDevicesArrayAdapter = ArrayAdapter<String>(context, R.layout.device_name)
        pairedListView.adapter = pairedDevicesArrayAdapter

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices = bluetoothAdapter.bondedDevices
        if (pairedDevices.size > 0) {
            view.findViewById<View>(R.id.title_paired_devices).visibility = View.VISIBLE
            for (device in pairedDevices) {
                pairedDevicesArrayAdapter.add(device.name + "\n" + device.address)
            }
        } else {
            val noDevices = context.resources.getText(R.string.none_paired).toString()
            pairedDevicesArrayAdapter.add(noDevices)
        }

        pairedListView.setOnItemClickListener { _, v, _, _ ->
            val info = (v as TextView).text.toString()
            if (info == context.resources.getText(R.string.none_paired).toString()) {
                Log.e(TAG, "Clicked 'No devices paired', ignoring.")
                return@setOnItemClickListener
            }
            val address = info.substring(info.length - 17)
            val device = bluetoothAdapter.getRemoteDevice(address)

            // Cancel discovery because it's costly and can interfere with connecting.
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
                Log.e(TAG, "Discovery cancelled.")
            }

            Log.e(TAG, "Device selected. Connecting to $address")
            pairedListView.visibility = View.GONE
            statusLayout.visibility = View.VISIBLE

            mPrintService?.connect(device)
        }
        Log.e(TAG, "Attempting to show dialog. alertDialog is null: ${alertDialog == null}")
        alertDialog?.show()
    }

    private fun performPrint() {
        Log.e(TAG, "performPrint: Starting print job.")
        mPrintService?.write(WoosimCmd.initPrinter())
        mPrintService?.write(WoosimCmd.setBold(true))
        mPrintService?.write(title)
        mPrintService?.write(WoosimCmd.setBold(false))
        mPrintService?.write(body)
        mPrintService?.write(footer)
        mPrintService?.write("-------------------".toByteArray())
        mPrintService?.write("\n\n\n".toByteArray())
        mPrintService?.write((WoosimCmd.cutPaper(WoosimCmd.CUT_FULL)))
        Log.e(TAG, "performPrint: Print job sent.")
    }

    fun printDocument(context: Context, invoice: String, dateString: String, numberPlate: String, spaces: String, street: String,
                      served_by_: String?, coupons: String = "", duration: String = "", isReceipt: Boolean = false,
                      isDefaulter: Boolean = false, hasSticker: Boolean = false, view: View? = null, isCouponSale: Boolean = false,
                      amount: Int = 0, isCashSale: Boolean = false, printService: BluetoothPrintService?, printTitle: String = "",
                      isSaleReceipt: Boolean = false, isDailyAdvance: Boolean = false): Pair<String, Boolean> {
        mPrintService = printService
        try {
            if (hasSticker) {
                return Pair("", true)
            }

            val date = convertDateString(dateString)
            val addressObject = Urls.addressMap[context.packageName]
            val chargesObject = Urls.chargesMap[context.packageName]
            val options = BitmapFactory.Options()
            options.inTargetDensity = 160
            options.inDensity = 160
            val bitmap = scaleImage(BitmapFactory.decodeResource(context.resources, Urls.logoMap[context.packageName]!!, options))
            SunmiPrintHelper.instance.initPrinter()
            val printer = BluetoothUtil.connectBlueTooth(context)
            ESCUtil.printBitmap(bitmap)?.let { BluetoothUtil.sendData(it) }


            var head = if (isCashSale) {
                "CASH RECEIPT"
            } else if (isReceipt) {
                "RECEIPT"
            } else if (isCouponSale) {
                "COUPONS"
            } else {
                "INVOICE"
            }
            if (isDefaulter) {
                head = "DEFAULTER"
            }
            var receipt = ""
            if (isReceipt) {
                receipt = "Receipt: RT" + invoice.substring(0, 5) + Random.nextInt(100, 999) + SimpleDateFormat(
                    "ddMHms", Locale.getDefault()).format(
                    Date(
                        System.currentTimeMillis()))
            }
            var footerText = if (isReceipt || isDefaulter || isCouponSale || isSaleReceipt) {
                ""
            } else "$chargesObject"
            if (context.packageName == "ug.global.parkingticketing.ntgm" && (isCouponSale)) {
                footerText = "$chargesObject"
            }
            if (!isReceipt) {
                footerText += Urls.momoMap[context.packageName]
            }

            if (context.packageName != "ug.global.parkingticketing.msk" && !isReceipt && !isDefaulter && !isCouponSale) {
                footerText += "\nNOTE: Vehicle parked at owner's risk"
            }
            val title = "${addressObject?.first}-$head\n${addressObject?.second}${
                if (isCouponSale) {
                    "Receipt"
                } else {
                    "Invoice"
                }
            } No.$invoice\n${receipt}$printTitle\n"

            BluetoothUtil.sendData(ESCUtil.underlineOff())
            BluetoothUtil.sendData(ESCUtil.alignCenter())
            BluetoothUtil.sendData(title.toByteArray())
            BluetoothUtil.sendData(ESCUtil.nextLine(1))
            if (isDefaulter) {
                BluetoothUtil.sendData(ESCUtil.boldOn())
            }

            val depart = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val sticker = if (hasSticker) {
                "Sticker"
            } else {
                ""
            }
            var cash = ""
            if (isCashSale) {
                cash = "(Ugx. ${
                    NumberFormat.getInstance().format(
                        coupons.toInt() * if (context.packageName == "ug.global.parkingticketing.ntgm") {
                            1000
                        } else {
                            500
                        })
                })"
            }
            val footer = if (isReceipt || isDefaulter) {
                "Departure Time: $depart\nParking Duration:$duration\nCoupons:$coupons - $sticker $cash"
            } else {
                ""
            }
            val saleWord = if (isDailyAdvance) {
                "SPACES"
            } else {
                "COUPONS"
            }
            val body =

                "${
                    if (isCouponSale) {
                        "Date:"
                    } else {
                        "Arrival Time:"
                    }
                } $date\nVehicle No. : $numberPlate\n${
                    if (isCouponSale) {
                        ""
                    } else {
                        "Spaces:$spaces\n"
                    }
                }Street Code: $street\n${

                    if (isCouponSale) {
                        "$saleWord : $coupons (${NumberFormat.getInstance().format(amount)}) \n"
                    } else {
                        ""
                    }
                }$footer\nServed By: ${
                    if (served_by_ == "") {
                        "Coupon Collector"
                    } else {
                        served_by_
                    }
                }\n"
            BluetoothUtil.sendData(ESCUtil.alignLeft())
            BluetoothUtil.sendData(body.toByteArray())
            if (context.packageName != "ug.global.parkingticketing.msk") {
                BluetoothUtil.sendData(ESCUtil.boldOn())
            }
            BluetoothUtil.sendData(footerText.toByteArray())
            BluetoothUtil.sendData(ESCUtil.nextLine(3))
            BluetoothUtil.sendData(ESCUtil.gogogo())
            BluetoothUtil.sendData(ESCUtil.cutter())
            if (!printer) {
                printOverBt(
                    context, (title).toByteArray(), ("\n\n" + body).toByteArray(), ("\n\n" + footerText + "\n").toByteArray())
                return Pair("\n" + title + "\n" + body + "\n" + footerText + "\n", false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "printDocument: ", e)
            if (view != null) {
                Snackbar.make(view, e.toString(), Toast.LENGTH_LONG).show()
            }
        }
        return Pair("true", true)

    }


    private fun scaleImage(bitmap1: Bitmap): Bitmap {
        val width = bitmap1.width
        val height = bitmap1.height
        // 设置想要的大小
        val newWidth = (width / 8 + 1) * 8
        val newHeight = (height / 8 + 1) * 8
        // 计算缩放比例
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // 取得想要缩放的matrix参数
        val matrix = Matrix()
        matrix.postScale(scaleWidth, 1F)
        matrix.postScale(scaleHeight, 1f)
        // 得到新的图片
        return Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true)
    }
    
    // ==================== TOAST & SNACKBAR HELPERS ====================
    
    /**
     * Show a short toast message
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Show a long toast message
     */
    fun showLongToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    /**
     * Show a Snackbar with default duration
     */
    fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
    
    /**
     * Show a Snackbar with action button
     */
    fun showSnackbarWithAction(
        view: View,
        message: String,
        actionText: String,
        action: () -> Unit
    ) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(actionText) { action() }
            .show()
    }
    
    
    // ==================== DIALOG HELPERS ====================
    
    /**
     * Show a simple alert dialog
     */
    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        onPositiveClick: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onPositiveClick?.invoke()
                dialog.dismiss()
            }
            .show()
    }
    
    /**
     * Show a confirmation dialog with Yes/No buttons
     */
    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "Yes",
        negativeButtonText: String = "No",
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                onCancel?.invoke()
                dialog.dismiss()
            }
            .show()
    }
    
    
    // ==================== SHARED PREFERENCES HELPERS ====================
    
    /**
     * Get SharedPreferences instance
     */
    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(URLS.AppConfig.PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Save string to SharedPreferences
     */
    fun saveString(context: Context, key: String, value: String) {
        getPreferences(context).edit().putString(key, value).apply()
    }
    
    /**
     * Get string from SharedPreferences
     */
    fun getString(context: Context, key: String, defaultValue: String = ""): String {
        return getPreferences(context).getString(key, defaultValue) ?: defaultValue
    }
    
    /**
     * Save boolean to SharedPreferences
     */
    fun saveBoolean(context: Context, key: String, value: Boolean) {
        getPreferences(context).edit { putBoolean(key, value) }
    }
    
    /**
     * Get boolean from SharedPreferences
     */
    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean {
        return getPreferences(context).getBoolean(key, defaultValue)
    }
    
    /**
     * Save int to SharedPreferences
     */
    fun saveInt(context: Context, key: String, value: Int) {
        getPreferences(context).edit { putInt(key, value) }
    }
    
    /**
     * Get int from SharedPreferences
     */
    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int {
        return getPreferences(context).getInt(key, defaultValue)
    }
    
    /**
     * Clear all SharedPreferences
     */
    fun clearPreferences(context: Context) {
        getPreferences(context).edit { clear() }
    }
    
    /**
     * Remove specific key from SharedPreferences
     */
    fun removeKey(context: Context, key: String) {
        getPreferences(context).edit().remove(key).apply()
    }
    
    
    // ==================== NETWORK HELPERS ====================
    
    /**
     * Check if device has internet connection
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * Check network and show error if not available
     */
    fun checkNetworkAndNotify(context: Context, view: View? = null): Boolean {
        if (!isNetworkAvailable(context)) {
            if (view != null) {
                showSnackbar(view, "No internet connection")
            } else {
                showToast(context, "No internet connection")
            }
            return false
        }
        return true
    }
    
    
    // ==================== KEYBOARD HELPERS ====================
    
    /**
     * Hide keyboard
     */
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus ?: View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    
    /**
     * Show keyboard for specific view
     */
    fun showKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
    
    
    // ==================== VALIDATION HELPERS ====================
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Validate phone number (basic validation)
     */
    fun isValidPhone(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }
    
    /**
     * Check if string is empty or blank
     */
    fun isEmptyOrBlank(text: String?): Boolean {
        return text.isNullOrBlank()
    }
    
    /**
     * Validate password strength (minimum 6 characters)
     */
    fun isValidPassword(password: String, minLength: Int = 6): Boolean {
        return password.length >= minLength
    }
    
    
    // ==================== DATE & TIME HELPERS ====================
    
    /**
     * Get current timestamp in milliseconds
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
    
    /**
     * Format date to string
     */
    fun formatDate(timestamp: Long, pattern: String = "dd MMM yyyy"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format time to string
     */
    fun formatTime(timestamp: Long, pattern: String = "hh:mm a"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    /**
     * Format date and time to string
     */
    fun formatDateTime(timestamp: Long, pattern: String = "dd MMM yyyy, hh:mm a"): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    
    // ==================== STRING HELPERS ====================
    
    /**
     * Capitalize first letter of string
     */
    fun capitalizeFirstLetter(text: String): String {
        return text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
    
    /**
     * Truncate string to specified length with ellipsis
     */
    fun truncateString(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            "${text.substring(0, maxLength)}..."
        } else {
            text
        }
    }
    
    
    // ==================== VIEW HELPERS ====================
    
    /**
     * Show view
     */
    fun showView(view: View) {
        view.visibility = View.VISIBLE
    }
    
    /**
     * Hide view
     */
    fun hideView(view: View) {
        view.visibility = View.GONE
    }
    
    /**
     * Make view invisible
     */
    fun makeInvisible(view: View) {
        view.visibility = View.INVISIBLE
    }
    
    /**
     * Toggle view visibility
     */
    fun toggleVisibility(view: View) {
        view.visibility = if (view.isVisible) View.GONE else View.VISIBLE
    }
    
    
    // ==================== AUTH HELPERS ====================
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(context: Context): Boolean {
        return getBoolean(context, URLS.AppConfig.KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Save login state
     */
    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        saveBoolean(context, URLS.AppConfig.KEY_IS_LOGGED_IN, isLoggedIn)
    }
    
    /**
     * Get auth token
     */
    fun getAuthToken(context: Context): String {
        return getString(context, URLS.AppConfig.KEY_AUTH_TOKEN, "")
    }
    
    /**
     * Save auth token
     */
    fun saveAuthToken(context: Context, token: String) {
        saveString(context, URLS.AppConfig.KEY_AUTH_TOKEN, token)
    }
    
    /**
     * Clear auth data and logout
     */
    fun logout(context: Context) {
        removeKey(context, URLS.AppConfig.KEY_AUTH_TOKEN)
        removeKey(context, URLS.AppConfig.KEY_USER_ID)
        removeKey(context, URLS.AppConfig.KEY_USER_EMAIL)
        saveLoginState(context, false)
    }
    
    
    // ==================== DATABASE HELPERS ====================
    
    /**
     * Get database instance
     */
    fun getDatabase(context: Context): ug.global.temp.db.database.AppDatabase {
        return ug.global.temp.db.database.AppDatabase.getInstance(context)
    }
    
    /**
     * Get UserRepository instance
     */
    fun getUserRepository(context: Context): ug.global.temp.db.repository.UserRepository {
        val db = getDatabase(context)
        return ug.global.temp.db.repository.UserRepository(db.userDao())
    }
}

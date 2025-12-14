package ug.global.temp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import ug.global.temp.databinding.ActivityMainBinding
import ug.global.temp.databinding.BottomSheetLoginBinding
import ug.global.temp.util.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private var loginBottomSheet: BottomSheetDialog? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize Volley
        VolleyHelper.init(this)
        
        // Check if user is already logged in
        if (Helpers.isLoggedIn(this)) {
            showLoggedInView()
        } else {
            showLoginBottomSheet()
        }
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.btnAction1.setOnClickListener {
            if (Helpers.checkNetworkAndNotify(this, binding.root)) {
                performAction1()
            }
        }
        
        binding.btnAction2.setOnClickListener {
            if (Helpers.checkNetworkAndNotify(this, binding.root)) {
                performAction2()
            }
        }
        
        binding.btnLogout.setOnClickListener {
            logout()
        }
        
        binding.fabLogin.setOnClickListener {
            showLoginBottomSheet()
        }
    }
    
    private fun showLoginBottomSheet() {
        // Create bottom sheet dialog
        loginBottomSheet = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetLoginBinding.inflate(layoutInflater)
        loginBottomSheet?.setContentView(bottomSheetBinding.root)
        
        // Setup login form
        with(bottomSheetBinding) {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                
                if (validateLoginForm(email, password)) {
                    performLogin(email, password, bottomSheetBinding)
                }
            }
            
            tvForgotPassword.setOnClickListener {
                Helpers.showToast(this@MainActivity, "Forgot password clicked")
                // TODO: Implement forgot password functionality
            }
            
            tvRegister.setOnClickListener {
                Helpers.showToast(this@MainActivity, "Register clicked")
                // TODO: Navigate to registration screen
            }
        }
        
        loginBottomSheet?.show()
    }
    
    private fun validateLoginForm(email: String, password: String): Boolean {
        if (Helpers.isEmptyOrBlank(email)) {
            Helpers.showToast(this, "Email is required")
            return false
        }
        
        if (!Helpers.isValidEmail(email)) {
            Helpers.showToast(this, "Invalid email format")
            return false
        }
        
        if (Helpers.isEmptyOrBlank(password)) {
            Helpers.showToast(this, "Password is required")
            return false
        }
        
        if (!Helpers.isValidPassword(password)) {
            Helpers.showToast(this, "Password must be at least 6 characters")
            return false
        }
        
        return true
    }
    
    private fun performLogin(email: String, password: String, bottomSheetBinding: BottomSheetLoginBinding) {
        // Check network
        if (!Helpers.checkNetworkAndNotify(this, binding.root)) {
            return
        }
        
        // Show loading
        showLoginLoading(bottomSheetBinding, true)
        
        // Prepare request data
        val data = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        
        // Make login request
        VolleyHelper.post(
            context = this,
            url = URLS.LOGIN,
            data = data,
            onSuccess = { response ->
                showLoginLoading(bottomSheetBinding, false)
                handleLoginSuccess(response)
            },
            onError = { error ->
                showLoginLoading(bottomSheetBinding, false)
                handleLoginError(error)
            }
        )
    }
    
    private fun showLoginLoading(bottomSheetBinding: BottomSheetLoginBinding, isLoading: Boolean) {
        with(bottomSheetBinding) {
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                btnLogin.isEnabled = false
                etEmail.isEnabled = false
                etPassword.isEnabled = false
            } else {
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
                etEmail.isEnabled = true
                etPassword.isEnabled = true
            }
        }
    }
    
    private fun handleLoginSuccess(response: JSONObject) {
        try {
            // Extract data from response
            val token = response.optString("token", "")
            val userId = response.optString("user_id", "")
            val userEmail = response.optString("email", "")
            
            // Save login data
            Helpers.saveAuthToken(this, token)
            Helpers.saveString(this, URLS.AppConfig.KEY_USER_ID, userId)
            Helpers.saveString(this, URLS.AppConfig.KEY_USER_EMAIL, userEmail)
            Helpers.saveLoginState(this, true)
            
            // Show success message
            Helpers.showSnackbar(binding.root, "Login successful!")
            
            // Close bottom sheet
            loginBottomSheet?.dismiss()
            
            // Update UI
            showLoggedInView()
            
        } catch (e: Exception) {
            Helpers.showToast(this, "Error processing login response")
        }
    }
    
    private fun handleLoginError(error: String) {
        Helpers.showSnackbar(binding.root, "Login failed: $error")
    }
    
    private fun showLoggedInView() {
        binding.layoutLoggedOut.visibility = View.GONE
        binding.layoutLoggedIn.visibility = View.VISIBLE
        binding.fabLogin.visibility = View.GONE
        
        val userEmail = Helpers.getString(this, URLS.AppConfig.KEY_USER_EMAIL, "User")
        binding.tvWelcome.text = "Welcome, $userEmail!"
    }
    
    private fun showLoggedOutView() {
        binding.layoutLoggedOut.visibility = View.VISIBLE
        binding.layoutLoggedIn.visibility = View.GONE
        binding.fabLogin.visibility = View.VISIBLE
    }
    
    private fun performAction1() {
        // Example action using GET request
        VolleyHelper.get(
            context = this,
            url = URLS.GET_DATA,
            onSuccess = { response ->
                Helpers.showSnackbar(binding.root, "Action 1 successful!")
                // Process response data here
            },
            onError = { error ->
                Helpers.showSnackbar(binding.root, "Action 1 failed: $error")
            }
        )
    }
    
    private fun performAction2() {
        // Example action using POST request
        val data = JSONObject().apply {
            put("action", "action2")
            put("timestamp", Helpers.getCurrentTimestamp())
        }
        
        VolleyHelper.post(
            context = this,
            url = URLS.POST_DATA,
            data = data,
            onSuccess = { response ->
                Helpers.showSnackbar(binding.root, "Action 2 successful!")
                // Process response data here
            },
            onError = { error ->
                Helpers.showSnackbar(binding.root, "Action 2 failed: $error")
            }
        )
    }
    
    private fun logout() {
        Helpers.showConfirmationDialog(
            context = this,
            title = "Logout",
            message = "Are you sure you want to logout?",
            onConfirm = {
                // Clear user data
                Helpers.logout(this)
                
                // Update UI
                showLoggedOutView()
                
                // Show message
                Helpers.showToast(this, "Logged out successfully")
                
                // Show login bottom sheet
                showLoginBottomSheet()
            }
        )
    }
    
    override fun onDestroy() {
        super.onDestroy()
        loginBottomSheet?.dismiss()
    }
}

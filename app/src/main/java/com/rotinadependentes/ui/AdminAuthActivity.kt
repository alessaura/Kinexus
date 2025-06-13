package com.rotinadependentes.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rotinadependentes.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class AdminAuthActivity : AppCompatActivity() {

    // Views principais
    private lateinit var logoView: ConstraintLayout
    private lateinit var logoText: TextView
    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView

    // Campos de input
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var passwordToggle: ImageView

    // Botões
    private lateinit var loginButton: MaterialButton
    private lateinit var biometricButton: MaterialButton
    private lateinit var cancelButton: MaterialButton

    // Navbar
    private lateinit var homeNavButton: ConstraintLayout
    private lateinit var adminNavButton: ConstraintLayout
    private lateinit var helpNavButton: ConstraintLayout

    // Loading e erros
    private lateinit var progressBar: ProgressBar
    private lateinit var emailErrorText: TextView
    private lateinit var passwordErrorText: TextView

    // Biometria
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var isBiometricAvailable = false
    private var biometricType = ""

    // Bottom Sheet
    private var bottomSheetDialog: BottomSheetDialog? = null

    // Estado
    private var loading = false
    private var showPassword = false

    // Credenciais admin
    companion object {
        private const val ADMIN_EMAIL = "admin@admin.com"
        private const val ADMIN_PASSWORD = "12345678@kinexus"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_auth)

        // Inicializar views
        initViews()

        // Configurar listeners
        setupClickListeners()

        // Configurar biometria
        setupBiometricAuthentication()

        // Verificar disponibilidade de biometria
        checkBiometricAvailability()
    }

    private fun initViews() {
        logoView = findViewById(R.id.logoView)
        logoText = findViewById(R.id.logoText)
        titleText = findViewById(R.id.titleText)
        subtitleText = findViewById(R.id.subtitleText)

        emailInputLayout = findViewById(R.id.emailInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        passwordEditText = findViewById(R.id.passwordEditText)
        passwordToggle = findViewById(R.id.passwordToggle)

        loginButton = findViewById(R.id.loginButton)
        biometricButton = findViewById(R.id.biometricButton)
        cancelButton = findViewById(R.id.cancelButton)

        homeNavButton = findViewById(R.id.homeNavButton)
        adminNavButton = findViewById(R.id.adminNavButton)
        helpNavButton = findViewById(R.id.helpNavButton)

        progressBar = findViewById(R.id.progressBar)
        emailErrorText = findViewById(R.id.emailErrorText)
        passwordErrorText = findViewById(R.id.passwordErrorText)
    }

    private fun setupClickListeners() {
        // Botão login
        loginButton.setOnClickListener {
            if (!loading) handleEmailLogin()
        }

        // Botão biométrico
        biometricButton.setOnClickListener {
            if (!loading) openBiometricBottomSheet()
        }

        // Botão cancelar
        cancelButton.setOnClickListener {
            if (!loading) finish()
        }

        // Toggle senha
        passwordToggle.setOnClickListener {
            togglePasswordVisibility()
        }

        // Navbar
        homeNavButton.setOnClickListener {
            // TODO: Navegar para home
            Toast.makeText(this, "Navegação para Home", Toast.LENGTH_SHORT).show()
        }

        helpNavButton.setOnClickListener {
            // TODO: Navegar para ajuda
            Toast.makeText(this, "Navegação para Ajuda", Toast.LENGTH_SHORT).show()
        }

        // Validação em tempo real
        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateEmail(emailEditText.text.toString())
        }

        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePassword(passwordEditText.text.toString())
        }

        // Enter key no campo password
        passwordEditText.setOnEditorActionListener { _, _, _ ->
            if (!loading) handleEmailLogin()
            true
        }
    }

    private fun setupBiometricAuthentication() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        Toast.makeText(applicationContext, "Erro de autenticação: $errString", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    bottomSheetDialog?.dismiss()
                    navigateToAdminDashboard()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Autenticação biométrica falhou", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticação Biométrica")
            .setSubtitle("Use sua biometria para acessar a área administrativa")
            .setNegativeButtonText("Cancelar")
            .build()
    }

    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                isBiometricAvailable = true
                biometricType = when {
                    // Detectar tipo de biometria (simplificado)
                    android.os.Build.MODEL.contains("iPhone", ignoreCase = true) -> "Face ID"
                    else -> "Impressão Digital"
                }
                updateBiometricButtonText()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                isBiometricAvailable = false
                biometricType = "Biometria"
                updateBiometricButtonText()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                isBiometricAvailable = false
                biometricType = "Biometria"
                updateBiometricButtonText()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                isBiometricAvailable = false
                biometricType = "Biometria"
                updateBiometricButtonText()
            }
        }
    }

    private fun updateBiometricButtonText() {
        val buttonText = if (isBiometricAvailable) {
            "Entrar com $biometricType"
        } else {
            "Entrar com Biometria"
        }
        biometricButton.text = buttonText
    }

    private fun handleEmailLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        // Validação
        val isEmailValid = validateEmail(email)
        val isPasswordValid = validatePassword(password)

        if (!isEmailValid || !isPasswordValid) {
            return
        }

        // Mostrar loading
        setLoading(true)

        // Simular delay de rede
        lifecycleScope.launch {
            delay(1000)

            if (email == ADMIN_EMAIL && password == ADMIN_PASSWORD) {
                // Login bem-sucedido
                navigateToAdminDashboard()
            } else {
                // Credenciais inválidas
                setLoading(false)
                Toast.makeText(this@AdminAuthActivity, "Credenciais administrativas inválidas", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openBiometricBottomSheet() {
        if (!isBiometricAvailable) {
            Toast.makeText(this, "Biometria não disponível neste dispositivo. Use email e senha.", Toast.LENGTH_LONG).show()
            return
        }

        // Criar Bottom Sheet
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_biometric_auth, null)

        // Configurar views do bottom sheet
        setupBottomSheetViews(bottomSheetView)

        bottomSheetDialog?.setContentView(bottomSheetView)
        bottomSheetDialog?.show()
    }

    private fun setupBottomSheetViews(view: View) {
        val titleText = view.findViewById<TextView>(R.id.bottomSheetTitle)
        val logoView = view.findViewById<ConstraintLayout>(R.id.bottomSheetLogo)
        val logoText = view.findViewById<TextView>(R.id.bottomSheetLogoText)
        val biometricIcon = view.findViewById<ImageView>(R.id.biometricIcon)
        val descriptionText = view.findViewById<TextView>(R.id.bottomSheetDescription)
        val authButton = view.findViewById<MaterialButton>(R.id.biometricAuthButton)
        val cancelButton = view.findViewById<MaterialButton>(R.id.bottomSheetCancelButton)

        // Configurar textos
        titleText.text = "Autenticação Biométrica"
        descriptionText.text = "Use sua $biometricType para acessar a área administrativa"
        authButton.text = "Autenticar com $biometricType"

        // Configurar ícone baseado no tipo de biometria
        val iconRes = when (biometricType) {
            "Face ID" -> R.drawable.ic_face
            "Impressão Digital" -> R.drawable.ic_fingerprint
            else -> R.drawable.ic_shield
        }
        biometricIcon.setImageResource(iconRes)

        // Listeners
        authButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        cancelButton.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
    }

    private fun validateEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                emailInputLayout.error = "Email é obrigatório"
                emailErrorText.text = "Email é obrigatório"
                emailErrorText.visibility = View.VISIBLE
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailInputLayout.error = "Email inválido"
                emailErrorText.text = "Email inválido"
                emailErrorText.visibility = View.VISIBLE
                false
            }
            else -> {
                emailInputLayout.error = null
                emailErrorText.visibility = View.GONE
                true
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        return when {
            password.isEmpty() -> {
                passwordInputLayout.error = "Senha é obrigatória"
                passwordErrorText.text = "Senha é obrigatória"
                passwordErrorText.visibility = View.VISIBLE
                false
            }
            password.length < 8 -> {
                passwordInputLayout.error = "Senha deve ter pelo menos 8 caracteres"
                passwordErrorText.text = "Senha deve ter pelo menos 8 caracteres"
                passwordErrorText.visibility = View.VISIBLE
                false
            }
            else -> {
                passwordInputLayout.error = null
                passwordErrorText.visibility = View.GONE
                true
            }
        }
    }

    private fun togglePasswordVisibility() {
        showPassword = !showPassword

        if (showPassword) {
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            passwordToggle.setImageResource(R.drawable.ic_visibility_off)
        } else {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            passwordToggle.setImageResource(R.drawable.ic_visibility)
        }

        // Manter cursor no final
        passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
    }

    private fun setLoading(isLoading: Boolean) {
        loading = isLoading

        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            loginButton.isEnabled = false
            biometricButton.isEnabled = false
            cancelButton.isEnabled = false
            emailEditText.isEnabled = false
            passwordEditText.isEnabled = false

            // Esconder texto do botão login
            loginButton.text = ""
        } else {
            progressBar.visibility = View.GONE
            loginButton.isEnabled = true
            biometricButton.isEnabled = true
            cancelButton.isEnabled = true
            emailEditText.isEnabled = true
            passwordEditText.isEnabled = true

            // Restaurar texto
            loginButton.text = "Entrar"
        }
    }

    private fun navigateToAdminDashboard() {
        Toast.makeText(this, "Login administrativo realizado com sucesso!", Toast.LENGTH_LONG).show()

        // TODO: Navegar para AdminDashboardActivity quando criar
        val intent = Intent(this, MainActivity::class.java) // Temporário
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomSheetDialog?.dismiss()
    }
}
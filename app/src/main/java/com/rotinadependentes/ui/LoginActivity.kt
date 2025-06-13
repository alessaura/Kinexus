package com.rotinadependentes.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rotinadependentes.R

class LoginActivity : AppCompatActivity() {

    // Views principais
    private lateinit var scrollView: ScrollView
    private lateinit var cardView: CardView
    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView

    // Botões
    private lateinit var googleButton: MaterialButton
    private lateinit var loginButton: MaterialButton
    private lateinit var adminButton: MaterialButton

    // Campos de input
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText

    // Controles adicionais
    private lateinit var passwordToggle: ImageView
    private lateinit var rememberMeCheckbox: CheckBox
    private lateinit var rememberMeText: TextView
    private lateinit var forgotPasswordText: TextView
    private lateinit var signUpText: TextView
    private lateinit var signUpLink: TextView

    // Loading
    private lateinit var progressBar: ProgressBar
    // Divisores comentados temporariamente
    // private lateinit var dividerLine1: View
    // private lateinit var dividerLine2: View
    // private lateinit var dividerText: TextView

    // Firebase e Google
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    // SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    // Estado
    private var loading = false
    private var showPassword = false

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val PREF_NAME = "LoginPrefs"
        private const val KEY_REMEMBERED_EMAIL = "remembered_email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Configurar Google Sign In
        setupGoogleSignIn()

        // Inicializar views
        initViews()

        // Configurar listeners
        setupClickListeners()

        // Carregar email salvo
        loadSavedEmail()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initViews() {
        scrollView = findViewById(R.id.scrollView)
        cardView = findViewById(R.id.cardView)
        titleText = findViewById(R.id.titleText)
        subtitleText = findViewById(R.id.subtitleText)

        googleButton = findViewById(R.id.googleButton)
        loginButton = findViewById(R.id.loginButton)
        adminButton = findViewById(R.id.adminButton)

        emailInputLayout = findViewById(R.id.emailInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        passwordEditText = findViewById(R.id.passwordEditText)

        passwordToggle = findViewById(R.id.passwordToggle)
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox)
        rememberMeText = findViewById(R.id.rememberMeText)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)
        signUpText = findViewById(R.id.signUpText)
        signUpLink = findViewById(R.id.signUpLink)

        progressBar = findViewById(R.id.progressBar)

        // Divisores comentados temporariamente
        // dividerLine1 = findViewById(R.id.dividerLine1)
        // dividerLine2 = findViewById(R.id.dividerLine2)
        // dividerText = findViewById(R.id.dividerText)
    }

    private fun setupClickListeners() {
        // Botão Google
        googleButton.setOnClickListener {
            if (!loading) handleGoogleLogin()
        }

        // Botão Login
        loginButton.setOnClickListener {
            if (!loading) handleEmailLogin()
        }

        // Toggle de senha
        passwordToggle.setOnClickListener {
            togglePasswordVisibility()
        }

        // Remember me (checkbox + texto)
        rememberMeCheckbox.setOnClickListener {
            // Checkbox gerencia seu próprio estado
        }

        rememberMeText.setOnClickListener {
            rememberMeCheckbox.isChecked = !rememberMeCheckbox.isChecked
        }

        // Forgot password
        forgotPasswordText.setOnClickListener {
            if (!loading) navigateToForgotPassword()
        }

        // Sign up
        signUpLink.setOnClickListener {
            if (!loading) navigateToSignUp()
        }

        // Admin access
        adminButton.setOnClickListener {
            if (!loading) navigateToAdminAuth()
        }

        // Enter key no campo password
        passwordEditText.setOnEditorActionListener { _, _, _ ->
            if (!loading) handleEmailLogin()
            true
        }
    }

    private fun loadSavedEmail() {
        val savedEmail = sharedPreferences.getString(KEY_REMEMBERED_EMAIL, null)
        if (!savedEmail.isNullOrEmpty()) {
            emailEditText.setText(savedEmail)
            rememberMeCheckbox.isChecked = true
        }
    }

    private fun handleEmailLogin() {
        // Esconder teclado
        hideKeyboard()

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        // Validação
        if (!validateForm(email, password)) {
            return
        }

        // Mostrar loading
        setLoading(true)

        // Fazer login
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                setLoading(false)

                if (task.isSuccessful) {
                    // Sucesso
                    handleRememberMe(email)
                    showSuccessToast("Login realizado com sucesso!")
                    navigateToDashboard()
                } else {
                    // Erro
                    handleLoginError(task.exception)
                }
            }
    }

    private fun handleGoogleLogin() {
        setLoading(true)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            setLoading(false)
            showErrorToast("Não foi possível fazer login com Google")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                setLoading(false)

                if (task.isSuccessful) {
                    showSuccessToast("Login com Google realizado com sucesso!")
                    navigateToDashboard()
                } else {
                    handleLoginError(task.exception)
                }
            }
    }

    private fun validateForm(email: String, password: String): Boolean {
        var isValid = true

        // Validar email
        if (email.isEmpty()) {
            emailInputLayout.error = "O e-mail é obrigatório"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Digite um e-mail válido"
            isValid = false
        } else {
            emailInputLayout.error = null
        }

        // Validar senha
        if (password.isEmpty()) {
            passwordInputLayout.error = "A senha é obrigatória"
            isValid = false
        } else {
            passwordInputLayout.error = null
        }

        return isValid
    }

    private fun handleRememberMe(email: String) {
        val editor = sharedPreferences.edit()

        if (rememberMeCheckbox.isChecked) {
            editor.putString(KEY_REMEMBERED_EMAIL, email)
        } else {
            editor.remove(KEY_REMEMBERED_EMAIL)
        }

        editor.apply()
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
            googleButton.isEnabled = false
            loginButton.isEnabled = false
            adminButton.isEnabled = false
            emailEditText.isEnabled = false
            passwordEditText.isEnabled = false
            rememberMeCheckbox.isEnabled = false

            // Esconder textos dos botões
            googleButton.text = ""
            loginButton.text = ""
        } else {
            progressBar.visibility = View.GONE
            googleButton.isEnabled = true
            loginButton.isEnabled = true
            adminButton.isEnabled = true
            emailEditText.isEnabled = true
            passwordEditText.isEnabled = true
            rememberMeCheckbox.isEnabled = true

            // Restaurar textos
            googleButton.text = "Entrar com Google"
            loginButton.text = "Entrar"
        }
    }

    private fun handleLoginError(exception: Exception?) {
        val errorMessage = when (exception?.message?.contains("user-not-found")) {
            true -> "E-mail não encontrado. Verifique ou crie uma conta."
            else -> when (exception?.message?.contains("wrong-password")) {
                true -> "Senha incorreta. Tente novamente."
                else -> when (exception?.message?.contains("too-many-requests")) {
                    true -> "Muitas tentativas. Tente novamente mais tarde."
                    else -> "Erro ao fazer login. Verifique suas credenciais."
                }
            }
        }

        showErrorToast(errorMessage)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun showSuccessToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Navegação
    private fun navigateToDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToForgotPassword() {
        // Implementar quando criar ForgotPasswordActivity
        showErrorToast("Tela de recuperação de senha será implementada")
    }

    private fun navigateToSignUp() {
        // Implementar quando criar SignUpActivity
        showErrorToast("Tela de cadastro será implementada")
    }

    private fun navigateToAdminAuth() {
        val intent = Intent(this, AdminAuthActivity::class.java)
        startActivity(intent)
    }
}
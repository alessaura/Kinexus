package com.rotinadependentes.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.rotinadependentes.R

class ForgotPasswordActivity : AppCompatActivity() {

    // Views
    private lateinit var containerLayout: ConstraintLayout
    private lateinit var cardView: CardView
    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var resetButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var successMessageLayout: ConstraintLayout
    private lateinit var successText: TextView
    private lateinit var backButton: MaterialButton
    private lateinit var backToLoginText: TextView

    // Firebase
    private lateinit var auth: FirebaseAuth

    // Estado
    private var emailSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()

        // Inicializar views
        initViews()

        // Configurar listeners
        setupClickListeners()
    }

    private fun initViews() {
        containerLayout = findViewById(R.id.containerLayout)
        cardView = findViewById(R.id.cardView)
        titleText = findViewById(R.id.titleText)
        subtitleText = findViewById(R.id.subtitleText)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        resetButton = findViewById(R.id.resetButton)
        progressBar = findViewById(R.id.progressBar)
        successMessageLayout = findViewById(R.id.successMessageLayout)
        successText = findViewById(R.id.successText)
        backButton = findViewById(R.id.backButton)
        backToLoginText = findViewById(R.id.backToLoginText)
    }

    private fun setupClickListeners() {
        resetButton.setOnClickListener {
            handleResetPassword()
        }

        backButton.setOnClickListener {
            navigateToLogin()
        }

        backToLoginText.setOnClickListener {
            navigateToLogin()
        }

        // Enter key no campo de email
        emailEditText.setOnEditorActionListener { _, _, _ ->
            handleResetPassword()
            true
        }
    }

    private fun handleResetPassword() {
        val email = emailEditText.text.toString().trim()

        // Validação de email
        if (!validateEmail(email)) {
            return
        }

        // Mostrar loading
        showLoading(true)

        // Enviar email de recuperação
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    // Sucesso
                    showSuccessState(email)
                    Toast.makeText(this, "E-mail de recuperação enviado com sucesso!", Toast.LENGTH_LONG).show()
                } else {
                    // Erro
                    handleResetError(task.exception)
                }
            }
    }

    private fun validateEmail(email: String): Boolean {
        // Verificar se email está vazio
        if (email.isEmpty()) {
            emailInputLayout.error = "Digite seu e-mail para recuperar a senha"
            emailEditText.requestFocus()
            return false
        }

        // Verificar formato do email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Digite um e-mail válido"
            emailEditText.requestFocus()
            return false
        }

        // Limpar erro se válido
        emailInputLayout.error = null
        return true
    }

    private fun handleResetError(exception: Exception?) {
        val errorMessage = when (exception?.message?.contains("user-not-found")) {
            true -> "Não existe conta com este e-mail"
            else -> "Não foi possível enviar o e-mail de recuperação. Tente novamente"
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showSuccessState(email: String) {
        emailSent = true

        // Esconder formulário de email
        emailInputLayout.visibility = View.GONE
        resetButton.visibility = View.GONE
        backToLoginText.visibility = View.GONE

        // Mostrar mensagem de sucesso
        successMessageLayout.visibility = View.VISIBLE
        successText.text = "E-mail de recuperação enviado para $email. " +
                "Verifique sua caixa de entrada e siga as instruções para redefinir sua senha."

        // Mostrar botão de voltar
        backButton.visibility = View.VISIBLE
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
            resetButton.isEnabled = false
            resetButton.text = ""
        } else {
            progressBar.visibility = View.GONE
            resetButton.isEnabled = true
            resetButton.text = "Enviar Link de Recuperação"
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToLogin()
    }
}
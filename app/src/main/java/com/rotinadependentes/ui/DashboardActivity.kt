package com.rotinadependentes.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rotinadependentes.R

class DashboardActivity : AppCompatActivity() {

    private lateinit var btnLogout: ImageButton
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Inicializar views
        initializeViews()

        // Configurar listeners
        setupListeners()

        // Atualizando o título dinâmico
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = "Bem-vindo(a), Administrador!"
    }

    private fun initializeViews() {
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupListeners() {
        // Listener do botão de logout
        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // Exemplo de ação rápida existentes
        val btnManageOrganizations = findViewById<Button>(R.id.btnManageOrganizations)
        btnManageOrganizations.setOnClickListener {
            Toast.makeText(this, "Gerenciar Organizações", Toast.LENGTH_SHORT).show()
        }

        val btnViewUsers = findViewById<Button>(R.id.btnViewUsers)
        btnViewUsers.setOnClickListener {
            Toast.makeText(this, "Ver Todos os Usuários", Toast.LENGTH_SHORT).show()
        }

        // Você pode adicionar aqui os outros cliques dos botões
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Logout")
            .setMessage("Tem certeza que deseja sair do sistema?")
            .setPositiveButton("Sim") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performLogout() {
        // Limpar dados de autenticação
        clearUserData()

        // Redirecionar para LoginActivity
        navigateToLogin()

        // Finalizar esta activity
        finish()
    }

    private fun clearUserData() {
        val editor = sharedPreferences.edit()

        // Remover dados de autenticação
        editor.remove("auth_token")
        editor.remove("user_id")
        editor.remove("user_email")
        editor.remove("user_name")
        editor.remove("is_logged_in")

        // Aplicar mudanças
        editor.apply()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)

        // Limpar stack de activities (impede voltar com botão back)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
    }
}
package com.rotinadependentes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rotinadependentes.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = "Bem-vindo(a), Administrador!"

        // ✅ Agora ativado!
        val btnManageOrganizations = findViewById<Button>(R.id.btnManageOrganizations)
        btnManageOrganizations.setOnClickListener {
            val intent = Intent(this, OrganizationListActivity::class.java)
            startActivity(intent)
        }

        val btnViewUsers = findViewById<Button>(R.id.btnViewUsers)
        btnViewUsers.setOnClickListener {
            val intent = Intent(this, MemberListActivity::class.java)
            startActivity(intent)
        }

    }
}

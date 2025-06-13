package com.rotinadependentes.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.R

class RoutineCreateActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etTime: EditText
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar

    private var dependentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_routine)

        // Recupera o dependentId vindo via Intent
        dependentId = intent.getStringExtra("dependentId")

        etTitle = findViewById(R.id.etTitle)
        etTime = findViewById(R.id.etTime)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressBar = findViewById(R.id.progressBar)

        btnSubmit.setOnClickListener {
            createRoutine()
        }
    }

    private fun createRoutine() {
        val title = etTitle.text.toString().trim()
        val time = etTime.text.toString().trim()

        if (title.isEmpty()) {
            etTitle.error = "Digite o título da rotina"
            return
        }

        if (time.isEmpty()) {
            etTime.error = "Digite o horário da rotina"
            return
        }

        if (dependentId.isNullOrEmpty()) {
            Toast.makeText(this, "Erro: Dependente não informado", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = ProgressBar.VISIBLE
        btnSubmit.isEnabled = false

        val db = FirebaseFirestore.getInstance()
        val routineData = hashMapOf(
            "title" to title,
            "time" to time,
            "dependentId" to dependentId,
            "createdAt" to Timestamp.now()
        )

        db.collection("routine")
            .add(routineData)
            .addOnSuccessListener {
                Toast.makeText(this, "Rotina criada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao criar rotina", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
                progressBar.visibility = ProgressBar.GONE
                btnSubmit.isEnabled = true
            }
    }
}

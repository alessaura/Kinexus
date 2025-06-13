package com.rotinadependentes.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.R
import java.util.Calendar
import java.util.Date

class RoutineCreateActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var btnPickTime: Button
    private lateinit var tvSelectedTime: TextView
    private lateinit var btnSaveRoutine: Button

    private var selectedHour = -1
    private var selectedMinute = -1
    private lateinit var dependentId: String  // Deve ser passado via intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_routine)

        etTitle = findViewById(R.id.etTitle)
        btnPickTime = findViewById(R.id.btnPickTime)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        btnSaveRoutine = findViewById(R.id.btnSaveRoutine)

        dependentId = intent.getStringExtra("DEPENDENT_ID") ?: ""

        btnPickTime.setOnClickListener { openTimePicker() }
        btnSaveRoutine.setOnClickListener { saveRoutine() }
    }

    private fun openTimePicker() {
        val cal = Calendar.getInstance()
        val timePicker = TimePickerDialog(this, { _, hour, minute ->
            selectedHour = hour
            selectedMinute = minute
            val formattedTime = String.format("%02d:%02d", hour, minute)
            tvSelectedTime.text = "Horário selecionado: $formattedTime"
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
        timePicker.show()
    }

    private fun saveRoutine() {
        val title = etTitle.text.toString().trim()
        if (title.isEmpty() || selectedHour == -1 || selectedMinute == -1) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

        val routine = hashMapOf(
            "title" to title,
            "time" to formattedTime,
            "dependentId" to dependentId,
            "createdAt" to Date()
        )

        FirebaseFirestore.getInstance()
            .collection("routines")
            .add(routine)
            .addOnSuccessListener {
                Toast.makeText(this, "Rotina salva!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}

package com.rotinadependentes.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.R
import com.rotinadependentes.adapter.RoutineAdapter
import com.rotinadependentes.model.Routine

class DependentDetailsActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvRelationship: TextView
    private lateinit var rvRoutines: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAddRoutine: Button

    private lateinit var routineAdapter: RoutineAdapter
    private val routines = mutableListOf<Routine>()
    private lateinit var dependentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dependent_details)

        tvName = findViewById(R.id.tvDependentName)
        tvRelationship = findViewById(R.id.tvDependentRelationship)
        rvRoutines = findViewById(R.id.rvRoutines)
        progressBar = findViewById(R.id.progressBar)
        btnAddRoutine = findViewById(R.id.btnAddRoutine)

        dependentId = intent.getStringExtra("dependentId") ?: ""
        val name = intent.getStringExtra("dependentName") ?: "Sem nome"
        val relationship = intent.getStringExtra("dependentRelationship") ?: "Sem relação"

        tvName.text = "Nome: $name"
        tvRelationship.text = "Relacionamento: $relationship"

        routineAdapter = RoutineAdapter(routines)
        rvRoutines.layoutManager = LinearLayoutManager(this)
        rvRoutines.adapter = routineAdapter

        btnAddRoutine.setOnClickListener {
            val intent = Intent(this, RoutineCreateActivity::class.java)
            intent.putExtra("dependentId", dependentId)
            startActivity(intent)
        }

        loadRoutines()
    }

    override fun onResume() {
        super.onResume()
        loadRoutines() // recarrega ao voltar da criação
    }

    private fun loadRoutines() {
        progressBar.visibility = View.VISIBLE
        FirebaseFirestore.getInstance()
            .collection("routines")
            .whereEqualTo("dependentId", dependentId)
            .get()
            .addOnSuccessListener { result ->
                routines.clear()
                for (doc in result) {
                    val routine = doc.toObject(Routine::class.java)
                    routines.add(routine)
                }
                routineAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar rotinas", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progressBar.visibility = View.GONE
            }
    }
}

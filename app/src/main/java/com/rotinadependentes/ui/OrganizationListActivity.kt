package com.rotinadependentes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.R
import com.rotinadependentes.adapter.OrganizationAdapter
import com.rotinadependentes.model.Organization

class OrganizationListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAdd: FloatingActionButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization_list)

        recyclerView = findViewById(R.id.recyclerViewOrganizations)
        progressBar = findViewById(R.id.progressBar)
        btnAdd = findViewById(R.id.btnAddOrganization)

        recyclerView.layoutManager = LinearLayoutManager(this)

        progressBar.visibility = View.VISIBLE
        val db = FirebaseFirestore.getInstance()

        db.collection("organization")
            .get()
            .addOnSuccessListener { result ->
                val organizations = mutableListOf<Organization>()
                for (document in result) {
                    val org = Organization(
                        id = document.id,
                        name = document.getString("name") ?: "Sem nome",
                        type = document.getString("type") ?: "Indefinido",
                        description = document.getString("description"),
                        logoUrl = document.getString("logoUrl")
                    )
                    organizations.add(org)
                }

                recyclerView.adapter = OrganizationAdapter(organizations) { organization ->
                    // Abre a tela de detalhes da organização
                    val intent = Intent(this, OrganizationDetailsActivity::class.java)
                    intent.putExtra("organization", organization)
                    startActivity(intent)
                }

                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                exception.printStackTrace()
            }

        btnAdd.setOnClickListener {
            val intent = Intent(this, OrganizationCreateActivity::class.java)
            startActivity(intent)
        }
    }
}

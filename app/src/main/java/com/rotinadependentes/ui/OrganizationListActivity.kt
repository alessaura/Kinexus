package com.rotinadependentes.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.R
import com.rotinadependentes.adapter.OrganizationAdapter
import com.rotinadependentes.model.Organization

class OrganizationListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var emptyStateText: TextView
    private lateinit var btnAdd: ExtendedFloatingActionButton
    private lateinit var adapter: OrganizationAdapter
    private val organizations = mutableListOf<Organization>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization_list)

        recyclerView = findViewById(R.id.recyclerViewOrganizations)
        progressBar = findViewById(R.id.progressBar)
        searchView = findViewById(R.id.searchView)
        emptyStateText = findViewById(R.id.emptyStateText)
        btnAdd = findViewById(R.id.btnAddOrganization)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = OrganizationAdapter(organizations) { organization ->
            val intent = Intent(this, OrganizationDetailsActivity::class.java)
            intent.putExtra("organization", organization)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        progressBar.visibility = View.VISIBLE
        fetchOrganizations()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText.orEmpty())
                toggleEmptyState()
                return true
            }
        })

        btnAdd.setOnClickListener {
            val intent = Intent(this, OrganizationCreateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchOrganizations() {
        val db = FirebaseFirestore.getInstance()
        db.collection("organization")
            .get()
            .addOnSuccessListener { result ->
                organizations.clear()
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
                adapter.updateList(organizations)
                progressBar.visibility = View.GONE
                toggleEmptyState()
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                exception.printStackTrace()
                toggleEmptyState()
            }
    }

    private fun toggleEmptyState() {
        if (adapter.itemCount == 0) {
            emptyStateText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateText.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}

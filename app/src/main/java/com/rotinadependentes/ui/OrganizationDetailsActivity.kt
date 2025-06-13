package com.rotinadependentes.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.R
import com.rotinadependentes.adapter.MemberAdapter
import com.rotinadependentes.model.Member
import com.rotinadependentes.model.Organization

class OrganizationDetailsActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDescription: TextView
    private lateinit var rvMembers: RecyclerView
    private lateinit var progressBar: ProgressBar

    private val db = FirebaseFirestore.getInstance()
    private lateinit var organizationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization_details)

        tvName = findViewById(R.id.tvOrganizationName)
        tvType = findViewById(R.id.tvOrganizationType)
        tvDescription = findViewById(R.id.tvOrganizationDescription)
        rvMembers = findViewById(R.id.rvOrganizationMembers)
        progressBar = findViewById(R.id.progressBar)

        val organization = intent.getSerializableExtra("organization") as? Organization

        if (organization != null) {
            organizationId = organization.id
            tvName.text = organization.name
            tvType.text = organization.type
            tvDescription.text = organization.description ?: "Sem descrição"
            loadMembers()
        } else {
            finish()
        }
    }

    private fun loadMembers() {
        progressBar.visibility = View.VISIBLE
        db.collection("members")
            .whereEqualTo("organizationId", organizationId)
            .get()
            .addOnSuccessListener { result ->
                val members = result.map { doc ->
                    Member(
                        id = doc.id,
                        fullName = doc.getString("fullName") ?: "",
                        email = doc.getString("email") ?: "",
                        phone = doc.getString("phone") ?: "",
                        status = doc.getString("status") ?: "",
                        organizationId = doc.getString("organizationId") ?: "",
                        photoUrl = doc.getString("photoUrl"),
                        photoBase64 = doc.getString("photoBase64"),
                        dependents = doc.get("dependents") as? List<Map<String, Any>> ?: emptyList()
                    )
                }.toMutableList()

                rvMembers.layoutManager = LinearLayoutManager(this)
                rvMembers.adapter = MemberAdapter(members) { /* clique opcional */ }

                progressBar.visibility = View.GONE
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                it.printStackTrace()
            }
    }
}

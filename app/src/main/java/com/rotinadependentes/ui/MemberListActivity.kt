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
import com.rotinadependentes.adapter.MemberAdapter
import com.rotinadependentes.model.Member
import com.rotinadependentes.ui.member.MemberCreateActivity

class MemberListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var adapter: MemberAdapter
    private val members = mutableListOf<Member>()

    companion object {
        const val CREATE_MEMBER_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)

        recyclerView = findViewById(R.id.recyclerViewMembers)
        progressBar = findViewById(R.id.progressBar)
        btnAdd = findViewById(R.id.btnAddMember)

        adapter = MemberAdapter(members) { member ->
            val intent = Intent(this, MemberEditActivity::class.java)
            intent.putExtra("memberId", member.id)
            startActivity(intent)
        }


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            val intent = Intent(this, MemberCreateActivity::class.java)
            startActivityForResult(intent, CREATE_MEMBER_REQUEST)
        }

        loadMembers()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMembers() {
        progressBar.visibility = View.VISIBLE

        FirebaseFirestore.getInstance().collection("members")
            .get()
            .addOnSuccessListener { result ->
                members.clear()
                for (document in result) {
                    val member = document.toObject(Member::class.java).copy(id = document.id)
                    members.add(member)
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener {
                it.printStackTrace()
                progressBar.visibility = View.GONE
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_MEMBER_REQUEST) {
            loadMembers() // Recarrega membros quando volta da criação
        }
    }
}

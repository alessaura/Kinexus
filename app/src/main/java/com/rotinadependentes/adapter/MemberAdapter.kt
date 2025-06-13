package com.rotinadependentes.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.rotinadependentes.R
import com.rotinadependentes.model.Member
import com.rotinadependentes.ui.MemberEditActivity

class MemberAdapter(
    private val members: MutableList<Member>, // Agora é uma lista mutável
    private val onClick: (Member) -> Unit
) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val fullName: TextView = view.findViewById(R.id.tvFullName)
        private val email: TextView = view.findViewById(R.id.tvEmail)
        private val phone: TextView = view.findViewById(R.id.tvPhone)
        private val status: TextView = view.findViewById(R.id.tvStatus)
        private val btnView: Button = view.findViewById(R.id.btnView)
        private val btnEdit: Button = view.findViewById(R.id.btnEdit)
        private val btnDelete: Button = view.findViewById(R.id.btnDelete)

        fun bind(member: Member) {
            fullName.text = member.fullName
            email.text = member.email
            phone.text = member.phone
            status.text = when (member.status) {
                "active" -> "Ativo"
                "inactive" -> "Inativo"
                "pending" -> "Pendente"
                else -> "Desconhecido"
            }

            btnView.setOnClickListener {
                Toast.makeText(itemView.context, "Expandindo ${member.fullName}", Toast.LENGTH_SHORT).show()
            }

            btnEdit.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, MemberEditActivity::class.java)
                intent.putExtra("memberId", member.id)
                context.startActivity(intent)
            }

            btnDelete.setOnClickListener {
                FirebaseFirestore.getInstance().collection("members")
                    .document(member.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(itemView.context, "Membro excluído", Toast.LENGTH_SHORT).show()
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            members.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(itemView.context, "Erro ao excluir membro", Toast.LENGTH_SHORT).show()
                    }
            }

            itemView.setOnClickListener { onClick(member) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = members.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(members[position])
    }
}

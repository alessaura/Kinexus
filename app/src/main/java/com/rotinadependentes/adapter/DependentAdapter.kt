package com.rotinadependentes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rotinadependentes.R

class DependentAdapter(
    private val dependents: MutableList<Map<String, Any>>,
    private val onViewClick: (Map<String, Any>) -> Unit,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<DependentAdapter.DependentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DependentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dependent, parent, false)
        return DependentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DependentViewHolder, position: Int) {
        val dependent = dependents[position]
        holder.bind(dependent, position)
    }

    override fun getItemCount(): Int = dependents.size

    fun removeAt(position: Int) {
        dependents.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class DependentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvDependentName)
        private val tvRelationship: TextView = itemView.findViewById(R.id.tvDependentRelationship)
        private val btnView: ImageButton = itemView.findViewById(R.id.btnViewDependent)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemoveDependent)

        fun bind(dependent: Map<String, Any>, position: Int) {
            val name = dependent["fullName"] as? String ?: dependent["name"] as? String ?: "Sem nome"
            val relationship = dependent["relationship"] as? String ?: "-"

            tvName.text = name
            tvRelationship.text = relationship

            btnView.setOnClickListener {
                onViewClick(dependent)
            }

            btnRemove.setOnClickListener {
                onRemoveClick(position)
            }
        }
    }
}

package com.rotinadependentes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rotinadependentes.R
import com.rotinadependentes.model.Organization

class OrganizationAdapter(
    private val items: List<Organization>,
    private val onItemClick: (Organization) -> Unit
) : RecyclerView.Adapter<OrganizationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.tvOrganizationName)
        private val type: TextView = view.findViewById(R.id.tvOrganizationType)
        private val btnManage: Button = view.findViewById(R.id.btnManage)

        fun bind(organization: Organization) {
            name.text = organization.name
            type.text = organization.type

            // Clique no botão Gerenciar
            btnManage.setOnClickListener {
                onItemClick(organization)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_organization, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }



    override fun getItemCount() = items.size
}


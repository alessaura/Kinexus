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
    private var items: List<Organization>,
    private val onItemClick: (Organization) -> Unit
) : RecyclerView.Adapter<OrganizationAdapter.ViewHolder>() {

    private var fullList: List<Organization> = ArrayList(items)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.tvOrganizationName)
        private val type: TextView = view.findViewById(R.id.tvOrganizationType)
        private val btnManage: Button = view.findViewById(R.id.btnManage)

        fun bind(organization: Organization) {
            name.text = organization.name
            type.text = organization.type

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

    override fun getItemCount(): Int = items.size

    fun filter(query: String) {
        items = if (query.isEmpty()) {
            fullList
        } else {
            fullList.filter { organization ->
                organization.name.contains(query, ignoreCase = true) ||
                        organization.type.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: List<Organization>) {
        items = newList
        fullList = ArrayList(newList)
        notifyDataSetChanged()
    }
}

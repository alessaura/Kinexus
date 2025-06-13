package com.rotinadependentes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rotinadependentes.R
import com.rotinadependentes.model.Routine

class RoutineAdapter(private val routines: List<Routine>) :
    RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routine, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.tvTitle.text = routine.title
        holder.tvTime.text = "Horário: ${routine.time}"
    }

    override fun getItemCount(): Int = routines.size

    class RoutineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvRoutineTitle)
        val tvTime: TextView = itemView.findViewById(R.id.tvRoutineTime)
    }
}

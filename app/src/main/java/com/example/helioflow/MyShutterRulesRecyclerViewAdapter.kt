package com.example.helioflow

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.helioflow.databinding.RuleItemBinding
import com.example.helioflow.placeholder.ShutterRule

class MyShutterRulesRecyclerViewAdapter(
    private val values: MutableList<ShutterRule>,
    private val onEditClick: (ShutterRule, Int) -> Unit,
    private val onDeleteClick: (ShutterRule, Int) -> Unit
) : RecyclerView.Adapter<MyShutterRulesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RuleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.getDisplayContent()
        holder.detailsView.text = item.getDisplayDetails()
        holder.editButton.setOnClickListener { onEditClick(item, position) }
        holder.deleteButton.setOnClickListener { onDeleteClick(item, position) }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: RuleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val detailsView: TextView = binding.details
        val editButton: ImageButton = binding.editButton
        val deleteButton: ImageButton = binding.deleteButton

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

    fun addItem(item: ShutterRule) {
        values.add(item)
        notifyItemInserted(values.size - 1)
    }
}

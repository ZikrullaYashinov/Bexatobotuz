package com.apextech.bexatobotuz.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.databinding.ItemHistoryBinding

class HistoryAdapter : ListAdapter<HistoryEntity, HistoryAdapter.VhHistory>(MyDiffUtil()) {

    lateinit var itemClick: (HistoryEntity) -> Unit

    inner class VhHistory(private val itemHistoryBinding: ItemHistoryBinding) :
        ViewHolder(itemHistoryBinding.root) {
        fun bind(historyEntity: HistoryEntity) {
            itemHistoryBinding.apply {
                tvFirst.text = historyEntity.cyrill
                tvSecond.text = historyEntity.latin
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<HistoryEntity>() {
        override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
            return newItem == oldItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhHistory {
        return VhHistory(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VhHistory, position: Int) {
        val history = getItem(position)
        holder.itemView.setOnClickListener {
            itemClick.invoke(history)
        }
        holder.bind(history)
    }
}
package com.example.evi.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.evi.diffcallback.FeatureDiffCallback
import com.example.evi.databinding.ItemListBinding
import com.example.evi.repository.Features

class ListFeatureAdapter : RecyclerView.Adapter<ListFeatureAdapter.ViewHolder>() {
    private var data = listOf<Features>()

    fun updateItems(newItems: List<Features>) {
        val diffResult = DiffUtil.calculateDiff(
            FeatureDiffCallback(
                data,
                newItems
            )
        )
        data = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewDataBinding = ItemListBinding.inflate(inflater, parent, false)
        return ViewHolder(viewDataBinding)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }


    class ViewHolder(
        private val binding: ItemListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(features: Features) {
            binding.feature = features
            binding.executePendingBindings()
        }

    }
}
package com.example.evi.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.evi.FilterClickListener
import com.example.evi.R
import com.example.evi.databinding.ItemFilterBinding
import com.example.evi.diffcallback.FilterDiffCallback
import com.example.evi.repository.Features
import com.jakewharton.rxbinding2.view.clicks
import java.util.concurrent.TimeUnit

class ListFilterAdapter(
    private val listener: FilterClickListener
) : RecyclerView.Adapter<ListFilterAdapter.ViewHolder>() {
    private var data = listOf<Features>()
    private var selectedFilter: String? = ""

    fun updateItems(newItems: List<Features>, filterProvince: String?) {
        val diffResult = DiffUtil.calculateDiff(
            FilterDiffCallback(
                data,
                newItems
            )
        )
        data = newItems
        selectedFilter = filterProvince
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewDataBinding = ItemFilterBinding.inflate(inflater, parent, false)
        return ViewHolder(viewDataBinding, listener, selectedFilter)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(
        private val binding: ItemFilterBinding,
        private val listener: FilterClickListener,
        private val selectedFilter: String?
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("CheckResult")
        fun bind(
            features: Features
        ) {
            binding.feature = features
            binding.executePendingBindings()

            if (features.attributes!!.Provinsi == selectedFilter) {
                binding.textProvinceFilter.setBackgroundResource(R.drawable.background_outline_red)
            } else {
                binding.textProvinceFilter.setBackgroundResource(R.drawable.background_outline_gray)
            }

            binding.root.clicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (selectedFilter == features.attributes.Provinsi) {
                        binding.textProvinceFilter.setBackgroundResource(R.drawable.background_outline_gray)
                        listener.onSelectFilter("")
                    } else {
                        binding.textProvinceFilter.setBackgroundResource(R.drawable.background_outline_red)
                        listener.onSelectFilter(features.attributes.Provinsi)
                    }
                }
        }

    }
}
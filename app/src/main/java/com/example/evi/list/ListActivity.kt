package com.example.evi.list

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evi.FilterClickListener
import com.example.evi.R
import com.example.evi.databinding.ActivityListBinding
import com.example.evi.databinding.BottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var viewModel: ListViewModel
    private lateinit var listAdapter: ListFeatureAdapter
    private lateinit var filterAdapter: ListFilterAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list)
        binding.lifecycleOwner = this
        binding.viewModelList = viewModel

        initRecyclerView()
        viewModel.loadData()
        observer()
        listener()
    }

    private fun initRecyclerView() {
        listAdapter = ListFeatureAdapter()
        binding.recyclerviewList.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            adapter = listAdapter
            listAdapter.notifyDataSetChanged()
        }
    }

    private fun observer() {
        viewModel.dataList.observe(this, Observer {
            val list = viewModel.sortByProvinceList()
            listAdapter.updateItems(list)
        })

        var isFirstFilter = true
        viewModel.filter.observe(this, Observer {filter ->
            if (isFirstFilter){
                isFirstFilter = false
                return@Observer
            }
            val list = if (filter.isNullOrEmpty()){
                viewModel.sortByProvinceList()
            }else{
                viewModel.getFilterList()
            }
            listAdapter.updateItems(list)
        })
//        viewModel.keyword.observe(this, Observer {
//
//        })
    }

    @SuppressLint("CheckResult")
    private fun listener() {
        RxTextView.textChanges(binding.textSearch)
            .skipInitialValue()
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val list = viewModel.getSearchList()
                listAdapter.updateItems(list)
            }

        binding.buttonFilter.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                showFilterBottomSheet()
            }
    }

    private fun showFilterBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)

        val bindingFilterBottomSheet: BottomSheetFilterBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.bottom_sheet_filter, null, false
        )

        bindingFilterBottomSheet.lifecycleOwner = this
        bottomSheet.setContentView(bindingFilterBottomSheet.root)
        bindingFilterBottomSheet.viewModelFilter = viewModel

        filterAdapter =
            ListFilterAdapter(object : FilterClickListener {
                override fun onSelectFilter(filter: String) {
                    viewModel.filter.value = filter
                    bottomSheet.dismiss()
                }
            })
        bindingFilterBottomSheet.recycler.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            adapter = filterAdapter
            listAdapter.notifyDataSetChanged()
        }

        filterAdapter.updateItems(viewModel.sortByProvinceList(), viewModel.filter.value)

        bindingFilterBottomSheet.buttonClose.setOnClickListener {
            bottomSheet.dismiss()
        }
        bottomSheet.show()
    }
}

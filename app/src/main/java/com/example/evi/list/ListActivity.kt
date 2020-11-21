package com.example.evi.list

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evi.FilterClickListener
import com.example.evi.R
import com.example.evi.databinding.ActivityListBinding
import com.example.evi.databinding.BottomSheetFilterBinding
import com.example.evi.databinding.BottomSheetSortingBinding
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
            refreshList()
        })

        var isFirsSort = true
        viewModel.isSortByProvince.observe(this, Observer {
            if (isFirsSort) {
                isFirsSort = false
                return@Observer
            }
            refreshList()
        })

        var isFirstFilter = true
        viewModel.filter.observe(this, Observer { filter ->
            if (isFirstFilter) {
                isFirstFilter = false
                return@Observer
            }
            val list = if (filter.isNullOrEmpty()) {
                viewModel.sortList()
            } else {
                viewModel.getFilterList()
            }
            listAdapter.updateItems(list)
        })
    }

    private fun refreshList() {
        val list = viewModel.sortList()
        listAdapter.updateItems(list)
        binding.recyclerviewList.scrollToPosition(0)
    }

    @SuppressLint("CheckResult")
    private fun listener() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.keyword.value = ""
            viewModel.filter.value = ""
            binding.swipeRefresh.isRefreshing = false
            Handler().postDelayed({
                viewModel.loadData()
                binding.swipeRefresh.isRefreshing = false
            }, 800)
        }
        binding.swipeRefresh.setColorSchemeResources(
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

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

        binding.buttonSort.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                showSortingBottomSheet()
            }
    }

    private fun showSortingBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)

        val bindingSortBottomSheet: BottomSheetSortingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.bottom_sheet_sorting, null, false
        )

        bindingSortBottomSheet.lifecycleOwner = this
        bottomSheet.setContentView(bindingSortBottomSheet.root)
        bindingSortBottomSheet.viewModelSorting = viewModel

        if (viewModel.isSortByProvince.value!!) {
            bindingSortBottomSheet.textProvinceFilter.setBackgroundResource(R.drawable.background_outline_red)

        } else {
            bindingSortBottomSheet.textConfirmedFilter.setBackgroundResource(R.drawable.background_outline_red)
        }

        bindingSortBottomSheet.buttonClose.setOnClickListener {
            bottomSheet.dismiss()
        }

        bindingSortBottomSheet.textProvinceFilter.setOnClickListener {
            viewModel.isSortByProvince.value = true
            bottomSheet.dismiss()
        }

        bindingSortBottomSheet.textConfirmedFilter.setOnClickListener {
            viewModel.isSortByProvince.value = false
            bottomSheet.dismiss()
        }

        bottomSheet.show()

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

        filterAdapter.updateItems(viewModel.sortList(), viewModel.filter.value)

        bindingFilterBottomSheet.buttonClose.setOnClickListener {
            bottomSheet.dismiss()
        }
        bindingFilterBottomSheet.buttonClear.setOnClickListener {
            viewModel.filter.value = ""
            bottomSheet.dismiss()
        }
        bottomSheet.show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_UP) {
            val view = currentFocus
            if (view is EditText) {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}

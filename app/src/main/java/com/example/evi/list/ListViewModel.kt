package com.example.evi.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.evi.repository.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ListViewModel: ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    var dataList = MutableLiveData<ArrayList<Features>>()
    var keyword = MutableLiveData<String>()
    var filter = MutableLiveData<String>("")
    var textFilter = Transformations.map(filter){
        if (it == "") "Filter" else it
    }
    var isSortByProvince = MutableLiveData<Boolean>(true)
    fun loadData(){
        val requestApi = ApiClient.requestInstance()
            .create(ApiInterface::class.java)

        compositeDisposable.add(
            requestApi.getData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe{
                    dataList.value = it.features as ArrayList<Features>?
                }
        )
    }

    fun sortList(): List<Features>{
        return if (isSortByProvince.value!!) {
            dataList.value!!.sortedBy { features ->
                selector(features)
            }
        } else {
            dataList.value!!.sortedByDescending {features ->
                sortingConfirmed(features)
            }
        }
    }

    private fun selector(features: Features): String = features.attributes!!.Provinsi
    private fun sortingConfirmed(features: Features): Int = features.attributes!!.Kasus_Posi

    fun getSearchList(): List<Features> {
        return sortList().filter { features ->
            features.attributes!!.Provinsi.toLowerCase().contains(keyword.value!!)
        }
    }

    fun getFilterList(): List<Features> {
        return sortList().filter { features ->
            features.attributes!!.Provinsi == (filter.value!!)
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
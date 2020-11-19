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
    val showFilter = Transformations.map(filter){
        it.isNotEmpty()
    }
    fun loadData(){
        val requestApi = ApiClient.requestInstance()
            .create(ApiInterface::class.java)

        compositeDisposable.add(
            requestApi.getData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe{
//                    val list = it.features!!.sortedWith(compareBy({feature ->
//                        feature.attributes!!.Kasus_Posi
//                    }, {feature ->
//                        feature.attributes!!.Provinsi
//                    }))

                    dataList.value = it.features as ArrayList<Features>?
                }
        )
    }

    fun sortByProvinceList(): List<Features>{
        return dataList.value!!.sortedBy { features ->
            selector(features)
        }
    }

    private fun selector(features: Features): String = features.attributes!!.Provinsi

    fun getSearchList(): List<Features> {
        return sortByProvinceList().filter { features ->
            features.attributes!!.Provinsi.toLowerCase().contains(keyword.value!!)
        }
    }

    fun getFilterList(): List<Features> {
        return sortByProvinceList().filter { features ->
            features.attributes!!.Provinsi == (filter.value!!)
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
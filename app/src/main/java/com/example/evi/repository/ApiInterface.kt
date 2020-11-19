package com.example.evi.repository

import io.reactivex.Observable
import retrofit2.http.GET

interface ApiInterface {
    @GET("FeatureServer/0/query?where=1%3D1&outFields=*&outSR=4326&f=json")
    fun getData(): Observable<CovidDTO>
}
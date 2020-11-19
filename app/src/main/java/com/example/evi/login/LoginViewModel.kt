package com.example.evi.login

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {
    var userId = MutableLiveData<String>()
    var userPassword = MutableLiveData<String>()

    @SuppressLint("DefaultLocale")
    fun isUserAdmin(): Boolean {
        val id = userId.value ?: ""
        val password = userPassword.value ?: ""
        return id.toLowerCase() == "admin" && password.toLowerCase() == "admin"
    }

    fun isNotAdmin(): Boolean {
        return userId.value.isNullOrBlank() || userPassword.value.isNullOrBlank() || !isUserAdmin()
    }
}
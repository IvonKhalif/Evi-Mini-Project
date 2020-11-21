package com.example.evi.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.evi.R
import com.example.evi.databinding.ActivityLoginBinding
import com.example.evi.list.ListActivity
import com.jakewharton.rxbinding2.view.clicks
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.viewModelLogin = viewModel

        observer()
        listener()
    }

    private fun observer() {
        viewModel.userId.observe(this, Observer {text ->
            if (text.isNullOrEmpty()){
                binding.textIdCaption.setTextColor(
                    ContextCompat.getColorStateList(
                        this,
                        R.color.black_484848
                    )
                )
            } else {
                binding.textIdCaption.setTextColor(
                    ContextCompat.getColorStateList(
                        this,
                        R.color.gray_ADABAB
                    )
                )
            }
        })

        viewModel.userPassword.observe(this, Observer {text ->
            if (text.isNullOrEmpty()){
                binding.textPasswordCaption.setTextColor(
                    ContextCompat.getColorStateList(
                        this,
                        R.color.black_484848
                    )
                )
            } else {
                binding.textPasswordCaption.setTextColor(
                    ContextCompat.getColorStateList(
                        this,
                        R.color.gray_ADABAB
                    )
                )
            }
        })
    }

    @SuppressLint("CheckResult")
    private fun listener() {
        binding.buttonLogin.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                if (viewModel.isUserAdmin()) {
                    goToHomeActivity()
                } else if (viewModel.isNotAdmin()) {
                    Toast.makeText(this, "ID atau Kata Sandi tidak sesuai", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun goToHomeActivity() {
        startActivity(Intent(this, ListActivity::class.java))
        finish()
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

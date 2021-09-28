package com.fdhasna21.nydrobionics.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.databinding.ActivityCreateProfileBinding
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel

class CreateProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateProfileBinding
    lateinit var viewModel : CreateProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
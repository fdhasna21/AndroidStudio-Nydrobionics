package com.fdhasna21.nydrobionics.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityResetPasswordBinding
import com.fdhasna21.nydrobionics.viewmodel.ResetPasswordViewModel

class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener, TextWatcher{
    private lateinit var binding : ActivityResetPasswordBinding
    private lateinit var viewModel : ResetPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ResetPasswordViewModel::class.java)

        binding.apply {
            resetSubmit.setOnClickListener(this@ResetPasswordActivity)
            checkEmpty()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.resetSubmit -> {
                //todo : reset password
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty() {
        viewModel.apply {

        }
    }
}
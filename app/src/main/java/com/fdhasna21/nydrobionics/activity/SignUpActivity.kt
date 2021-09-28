package com.fdhasna21.nydrobionics.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.databinding.ActivitySignUpBinding
import com.fdhasna21.nydrobionics.viewmodel.SignUpViewModel
import com.google.android.material.textfield.TextInputEditText

class SignUpActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel : SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        supportActionBar?.title = null
        supportActionBar?.elevation= 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            signupSubmit.setOnClickListener(this@SignUpActivity)
            signupSignIn.setOnClickListener(this@SignUpActivity)

            signupEmail.addTextChangedListener(this@SignUpActivity)
            signupPassword.addTextChangedListener(this@SignUpActivity)
            signupConfirmPassword.addTextChangedListener(this@SignUpActivity)
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
        when(v){
            binding.signupSubmit -> {
//                if(viewModel.isNotEmpties.value == true){
//                    isLoading = true
//                    viewModel.signUp(binding.signupEmail.text.toString(), binding.signupPassword.text.toString())
//                    viewModel.isUserSignUp.observe(this@SignUpActivity, {
//                        if(it){
//                            isLoading = false
                            startActivity(Intent(this@SignUpActivity, CreateProfileActivity::class.java))
                            finish()
//                        } else {
//                            isLoading = false
//                            viewModel.signUpError.observe(this, {
//                                if(it.isNotEmpty()){
//                                    Toast.makeText(this@SignUpActivity, it, Toast.LENGTH_SHORT).show()
//                                    Log.i("signInActivity", it)
//                                    viewModel.signUpError.value = ""
//                                }
//                            })
//                        }
//                    })
//                }

            }
            binding.signupSignIn -> onBackPressed()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty() {
        val passwordMatch = binding.signupPassword.text.toString() == binding.signupConfirmPassword.text.toString()

        viewModel.setEmailEmpty(binding.signupEmail.text.toString().count() > 0)
        viewModel.setPasswordEmpty(binding.signupPassword.text.toString().count() > 0)
        viewModel.setConfirmPasswordEmpty(binding.signupConfirmPassword.text.toString().count() > 0 && passwordMatch)
        binding.signupConfirmPasswordLayout.error = if(passwordMatch){
            null
        } else {
            "Confirm password should be same"
        }

        viewModel.isNotEmpties.observe(this@SignUpActivity, {
            binding.signupSubmit.isEnabled = it
        })
    }

    private var isLoading : Boolean = false
        set(value) {
            val editableEditText : ArrayList<TextInputEditText> = arrayListOf(
                binding.signupEmail,
                binding.signupPassword,
                binding.signupConfirmPassword
            )
            editableEditText.forEach {
                it.isCursorVisible = !value
                it.isFocusable = !value
                it.isFocusableInTouchMode = !value
            }
            binding.signupSignIn.isEnabled = !value
            binding.signupSubmit.apply {
                if(value){
                    startAnimation()
                } else {
                    revertAnimation()
                }
            }
            supportActionBar?.setDisplayHomeAsUpEnabled(value)

            field = value
        }
}
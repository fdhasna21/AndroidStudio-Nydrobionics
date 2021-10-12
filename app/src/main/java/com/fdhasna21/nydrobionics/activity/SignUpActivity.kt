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
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.SignUpViewModel
import com.google.android.material.textfield.TextInputEditText

class SignUpActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel : SignUpViewModel
    private lateinit var utility : ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var viewsAsButton : ArrayList<View>

    companion object{
        const val TAG = "signUp"
    }

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
            viewsAsButton = arrayListOf(signupSignIn, signupSubmit)
            editTexts = arrayListOf(signupEmail, signupPassword, signupConfirmPassword)
            utility = ViewUtility(
                context = this@SignUpActivity,
                circularProgressButton = signupSubmit,
                textInputEditTexts = editTexts,
                viewsAsButton = viewsAsButton,
                actionBar = supportActionBar
            )

            viewsAsButton.forEach { it.setOnClickListener(this@SignUpActivity) }
            editTexts.forEach { it.addTextChangedListener(this@SignUpActivity) }
            checkEmpty()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
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
                    utility.isLoading = true
                    viewModel.signUp(binding.signupEmail.text.toString(), binding.signupPassword.text.toString())
                    viewModel.isUserSignUp.observe(this@SignUpActivity, {
                        if(it){
                            utility.isLoading = false
                            startActivity(Intent(this, CreateProfileActivity::class.java))
                            finish()
                        } else {
                            utility.isLoading = false
                            viewModel.signUpError.observe(this, {
                                it?.let {
                                    if (it.isNotEmpty()) {
                                        Toast.makeText(this@SignUpActivity, it, Toast.LENGTH_SHORT)
                                            .show()
                                        Log.i(TAG, it)
                                        viewModel.signUpError.value = ""
                                    }
                                }
                            })
                        }
                    })
            }
            binding.signupSignIn -> super.onBackPressed()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty() {
        val passwordMatch = binding.signupPassword.text.toString() == binding.signupConfirmPassword.text.toString()
        binding.signupConfirmPasswordLayout.error = if(passwordMatch){
            null
        } else {
            "Confirm password should be same"
        }

        viewModel.apply {
            checkNotEmpty(utility.isEmpties(editTexts) && passwordMatch).observe(this@SignUpActivity, {
                binding.signupSubmit.isEnabled = it
            })
        }
    }
}
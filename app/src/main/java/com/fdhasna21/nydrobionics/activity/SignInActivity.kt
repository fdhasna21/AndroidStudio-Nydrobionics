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
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.databinding.ActivitySignInBinding
import com.fdhasna21.nydrobionics.viewmodel.SignInViewModel
import com.google.android.material.textfield.TextInputEditText

class SignInActivity : AppCompatActivity(), View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var viewModel : SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        binding.apply {
            signinSubmit.setOnClickListener(this@SignInActivity)
            signinSignUp.setOnClickListener(this@SignInActivity)
            signinRemember.setOnCheckedChangeListener(this@SignInActivity)
            signinForgetPassword.setOnClickListener(this@SignInActivity)

            signinEmail.addTextChangedListener(this@SignInActivity)
            signinPassword.addTextChangedListener(this@SignInActivity)
            checkEmpty()
        }

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
            binding.signinSubmit -> {
                if(viewModel.isNotEmpties.value == true){
//                    isLoading = true
//                    viewModel.signIn(binding.signinEmail.text.toString(), binding.signinPassword.text.toString())
//                    viewModel.isUserSignIn.observe(this@SignInActivity, {
//                        if(it){
//                            isLoading = false
                            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                            finish()
//                        } else {
//                            isLoading = false
//                            viewModel.signInError.observe(this, {
//                                if(it.isNotEmpty()){
//                                    Toast.makeText(this@SignInActivity, it, Toast.LENGTH_SHORT).show()
//                                    Log.i("signInActivity", it)
//                                    viewModel.signInError.value = ""
//                                }
//                            })
//                        }
//                    })
                }
            }
            binding.signinSignUp -> startActivity(Intent(this, SignUpActivity::class.java))
            binding.signinForgetPassword -> startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty() {
        viewModel.apply {
            setEmailEmpty(binding.signinEmail.text.toString().count() > 0)
            setPasswordEmpty(binding.signinPassword.text.toString().count() > 0)
            isNotEmpties.observe(this@SignInActivity, {
                binding.signinSubmit.isEnabled = it
            })
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        viewModel.setRememberChecked(true)
    }

    private var isLoading : Boolean = false
    set(value) {
        val editableEditText : ArrayList<TextInputEditText> = arrayListOf(
            binding.signinEmail,
            binding.signinPassword
        )
        editableEditText.forEach {
            it.isCursorVisible = !value
            it.isFocusable = !value
            it.isFocusableInTouchMode = !value
        }
        binding.signinForgetPassword.isEnabled = !value
        binding.signinSignUp.isEnabled = !value
        binding.signinRemember.isEnabled = !value
        binding.signinSubmit.apply {
            if(value){
                startAnimation()
            } else {
                revertAnimation()
            }
        }

        field = value
    }
}
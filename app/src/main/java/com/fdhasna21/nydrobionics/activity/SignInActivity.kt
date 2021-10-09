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
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.SignInViewModel
import com.google.android.material.textfield.TextInputEditText

class SignInActivity : AppCompatActivity(), View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var viewModel : SignInViewModel
    private lateinit var utility : ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var viewsAsButton : ArrayList<View>

    companion object{
        const val TAG = "signIn"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        binding.apply {
            editTexts = arrayListOf(signinEmail, signinPassword)
            viewsAsButton = arrayListOf(signinSubmit,
                signinSignUp,
                signinForgetPassword)
            utility = ViewUtility(
                context = this@SignInActivity,
                circularProgressButton = signinSubmit,
                textInputEditTexts = editTexts,
                viewsAsButton = viewsAsButton,
                actionBar = supportActionBar)

            signinRemember.setOnCheckedChangeListener(this@SignInActivity)
            viewsAsButton.forEach { it.setOnClickListener(this@SignInActivity) }
            editTexts.forEach { it.addTextChangedListener(this@SignInActivity) }
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
                utility.isLoading = true
                viewModel.signIn(binding.signinEmail.text.toString(), binding.signinPassword.text.toString())
                viewModel.isUserSignIn.observe(this@SignInActivity, {
                    if(it){
                        utility.isLoading = false
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                    } else {
                        utility.isLoading = false
                        viewModel.signInError.observe(this, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this@SignInActivity, it, Toast.LENGTH_SHORT).show()
                                Log.i(TAG, it)
                                viewModel.signInError.value = ""
                            }
                        })
                    }
                })
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
        viewModel.checkNotEmpty(utility.isEmpties(editTexts)).observe(this, {
            binding.signinSubmit.isEnabled = it
        })
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        viewModel.setRememberChecked(true)
    }
}
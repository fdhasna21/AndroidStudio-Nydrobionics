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
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityForgetPasswordBinding
import com.fdhasna21.nydrobionics.viewmodel.ForgetPasswordViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ActionCodeResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.util.ArrayList

class ForgetPasswordActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var binding : ActivityForgetPasswordBinding
    private lateinit var viewModel : ForgetPasswordViewModel
    private var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ForgetPasswordViewModel::class.java)
        supportActionBar?.title = null
        supportActionBar?.elevation= 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            forgetSubmit.setOnClickListener(this@ForgetPasswordActivity)
            forgetResend.setOnClickListener(this@ForgetPasswordActivity)
            forgetEmail.addTextChangedListener(this@ForgetPasswordActivity)
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
            binding.forgetSubmit, binding.forgetResend -> {
//                isLoading = true
//                viewModel.sendEmailReset(binding.forgetEmail.text.toString())
//                viewModel.isUserForgetPassword.observe(this, {
//                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
//                    if(it){
//                        isLoading = false
//                        binding.forgetSuccess.visibility = View.VISIBLE
//                        //todo : handle deeplink
////                        handleDynamicLink()
                        startActivity(Intent(this, ResetPasswordActivity::class.java))
                        finish()
//                    } else {
//                        isLoading = false
//                        viewModel.forgetPasswordError.observe(this, {
//                            if(it.isNotEmpty()){
//                                Toast.makeText(this@ForgetPasswordActivity, it, Toast.LENGTH_SHORT).show()
//                                Log.i("forgetPasswordActivity", it)
//                                viewModel.forgetPasswordError.value = ""
//                            }
//                        })
//                    }
//                    binding.forgetSubmit.revertAnimation()
//                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
//                })
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty() {
        viewModel.checkEmailEmpty(binding.forgetEmail.text.toString().count() > 0).observe(this, {
            binding.forgetSubmit.isEnabled = it
        })
    }

    private var isLoading : Boolean = false
        set(value) {
            val editableEditText : ArrayList<TextInputEditText> = arrayListOf(
                binding.forgetEmail
            )
            editableEditText.forEach {
                it.isCursorVisible = !value
                it.isFocusable = !value
                it.isFocusableInTouchMode = !value
            }
            binding.forgetSubmit.apply {
                if(value){
                    startAnimation()
                } else {
                    revertAnimation()
                }
            }
            if(value){
                binding.forgetSuccess.visibility = View.GONE
            }
            supportActionBar?.setDisplayHomeAsUpEnabled(value)

            field = value
        }

//    fun handleDynamicLink() {
//        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
//            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData ->
//                Log.i("forgetPasswordActivity", "Dynamic link detected")
//                val oobCode =
//                    pendingDynamicLinkData.link!!.getQueryParameter("oobCode")
//                if (oobCode != null) {
//                    auth.checkActionCode(oobCode).addOnSuccessListener { result ->
//                        when (result.getOperation()) {
//                            ActionCodeResult.VERIFY_EMAIL -> {
//                                auth.applyActionCode(oobCode)
//                                    .addOnSuccessListener { resultCode ->
//                                        Log.i("forgetPasswordActivity", "Verified email")
//                                        finish()
//                                    }.addOnFailureListener { resultCode ->
//                                        Log.w(
//                                            "forgetPasswordActivity",
//                                            "Failed to Verified Email",
//                                            resultCode
//                                        )
//                                    }
//                            }
//                            ActionCodeResult.PASSWORD_RESET -> {
//                                val passWordResetInetemnt =
//                                    Intent(this, ResetPasswordActivity::class.java)
//                                passWordResetInetemnt.putExtra("oobCode", oobCode)
//                                startActivity(passWordResetInetemnt)
//                                finish()
//                            }
//                        }
//                    }.addOnFailureListener { result ->
//                        Log.w("forgetPasswordActivity", "Invalid code sent")
//                        finish()
//                    }
//                }
//            }.addOnFailureListener { result: Exception? ->
//                Log.w("forgetPasswordActivity", "Failed to get dynamic link")
//                finish()
//            }
//    }
}
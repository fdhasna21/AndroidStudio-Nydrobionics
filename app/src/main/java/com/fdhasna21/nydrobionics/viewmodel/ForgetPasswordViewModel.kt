package com.fdhasna21.nydrobionics.viewmodel

import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.BuildConfig
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgetPasswordViewModel : ViewModel() {
    private var auth = Firebase.auth
    private var isEmailNotEmpty : MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserForgetPassword : MutableLiveData<Boolean> = MutableLiveData(false)
    var forgetPasswordError : MutableLiveData<String> = MutableLiveData("")

    fun checkEmailEmpty(boolean: Boolean) : MutableLiveData<Boolean>  {
        isEmailNotEmpty.value = boolean
        return isEmailNotEmpty
    }

    fun getEmailEmpty() : MutableLiveData<Boolean> {
        return isEmailNotEmpty
    }

    fun sendEmailReset(email:String) {
        if(isEmailNotEmpty.value == true){
            forgetPasswordError.value = ""
            val actionCode = ActionCodeSettings.newBuilder()
                .setUrl("https://nydrobionics21.page.link/RstPwd")
                .setHandleCodeInApp(true)
                .setAndroidPackageName(BuildConfig.APPLICATION_ID, true, "26")
                .build()
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if(it.isSuccessful){
                    isUserForgetPassword.value = true
                    //todo : tambahin ini ke yg lain
                } else {
                    forgetPasswordError.value = it.exception.toString()
                    isUserForgetPassword.value = false
                }
            }

        }
    }
}
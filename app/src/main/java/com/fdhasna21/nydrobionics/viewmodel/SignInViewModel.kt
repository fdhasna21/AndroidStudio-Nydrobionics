package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInViewModel : ViewModel() {
    private var auth = Firebase.auth
    private var isEmailNotEmpty : MutableLiveData<Boolean> = MutableLiveData(false)
    private var isPasswordNotEmpty : MutableLiveData<Boolean> = MutableLiveData(false)
    private var isRememberChecked : MutableLiveData<Boolean> = MutableLiveData(false)
    var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserSignIn : MutableLiveData<Boolean> = MutableLiveData(false)
    var signInError : MutableLiveData<String> = MutableLiveData("")

    fun setEmailEmpty(boolean: Boolean){
        isEmailNotEmpty.value = boolean
        isNotEmpties.value = isPasswordNotEmpty.value!! && boolean
    }

    fun setPasswordEmpty(boolean: Boolean){
        isPasswordNotEmpty.value = boolean
        isNotEmpties.value = isPasswordNotEmpty.value!! && boolean
    }

    fun setRememberChecked(boolean: Boolean){
        isRememberChecked.value = boolean
        //todo : mekanisme nyimpen
    }

    fun signIn(email:String, password:String){
        signInError.value = ""
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                isUserSignIn.value = true
            } else {
                signInError.value = it.exception.toString()
                isUserSignIn.value = false
            }
        }
    }
}
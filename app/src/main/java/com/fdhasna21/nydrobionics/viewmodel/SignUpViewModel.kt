package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpViewModel : ViewModel() {
    private var auth = Firebase.auth
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserSignUp : MutableLiveData<Boolean> = MutableLiveData(false)
    var signUpError : MutableLiveData<String> = MutableLiveData("")

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    fun signUp(email:String, password:String){
        signUpError.value = ""
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                isUserSignUp.value = true
            } else {
                signUpError.value = it.exception.toString()
                isUserSignUp.value = false
            }
        }
    }
}
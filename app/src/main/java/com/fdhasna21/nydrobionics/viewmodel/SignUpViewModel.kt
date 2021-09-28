package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpViewModel : ViewModel() {
    private var auth = Firebase.auth
     var isEmailNotEmpty : MutableLiveData<Boolean> = MutableLiveData(false)
    var isPasswordNotEmpty : MutableLiveData<Boolean> = MutableLiveData(false)
     var isConfirmPasswordNotEmpty : MutableLiveData<Boolean> = MutableLiveData(false)
    var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserSignUp : MutableLiveData<Boolean> = MutableLiveData(false)
    var signUpError : MutableLiveData<String> = MutableLiveData("")

    fun setEmailEmpty(boolean: Boolean){
        isEmailNotEmpty.value = boolean
        isNotEmpties.value = boolean && isPasswordNotEmpty.value!! && isConfirmPasswordNotEmpty.value!!
    }

    fun setPasswordEmpty(boolean: Boolean){
        isPasswordNotEmpty.value = boolean
        isNotEmpties.value = isEmailNotEmpty.value!! && boolean && isConfirmPasswordNotEmpty.value!!
    }

    fun setConfirmPasswordEmpty(boolean: Boolean){
        isConfirmPasswordNotEmpty.value = boolean
        isNotEmpties.value =  isEmailNotEmpty.value!! && isPasswordNotEmpty.value!! && boolean
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
package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResetPasswordViewModel : ViewModel() {
    private var isPasswordNotEmpty : MutableLiveData<Boolean> = MutableLiveData(false)
    private var isConfirmPasswordNotEmpty : MutableLiveData<Boolean> = MutableLiveData(false)
    var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)

    fun setPasswordEmpty(boolean: Boolean){
        isPasswordNotEmpty.value = boolean
        isNotEmpties.value = boolean && isConfirmPasswordNotEmpty.value!!
    }

    fun setConfirmPasswordEmpty(boolean: Boolean){
        isConfirmPasswordNotEmpty.value = boolean
        isNotEmpties.value =  boolean && isPasswordNotEmpty.value!!
    }

    fun sendNewPassword(password:String){
        //todo : mekanisme reset password
    }
}

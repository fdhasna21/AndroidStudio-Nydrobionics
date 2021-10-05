package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResetPasswordViewModel : ViewModel() {
    var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isPasswordChange : MutableLiveData<Boolean> = MutableLiveData(false)
    var resetPaswordError : MutableLiveData<String> = MutableLiveData("")

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    fun sendNewPassword(password:String){
        //todo : mekanisme reset password
    }
}

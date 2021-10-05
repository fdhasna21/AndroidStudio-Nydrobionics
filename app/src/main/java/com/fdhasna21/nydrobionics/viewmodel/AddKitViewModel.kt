package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddKitViewModel : ViewModel() {
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isKitAdd : MutableLiveData<Boolean> = MutableLiveData(false)
    var addKitError : MutableLiveData<String> = MutableLiveData("")

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

}

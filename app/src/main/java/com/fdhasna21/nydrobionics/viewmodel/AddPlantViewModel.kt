package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddPlantViewModel : ViewModel() {
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isPlantAdd : MutableLiveData<Boolean> = MutableLiveData(false)
    var addPlantError : MutableLiveData<String> = MutableLiveData("")

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }
}

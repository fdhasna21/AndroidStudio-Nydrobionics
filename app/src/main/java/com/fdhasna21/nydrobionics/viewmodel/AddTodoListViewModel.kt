package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddTodoListViewModel : ViewModel(){
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isTodoAdd: MutableLiveData<Boolean> = MutableLiveData(false)
    var addTodoError : MutableLiveData<String> = MutableLiveData("")

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }
}

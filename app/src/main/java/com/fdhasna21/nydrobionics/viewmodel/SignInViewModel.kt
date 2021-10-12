package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.utility.local.DatabaseHandler
import com.fdhasna21.nydrobionics.utility.local.DatabaseModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInViewModel : ViewModel() {
    private var auth = Firebase.auth
    private var isRememberChecked : MutableLiveData<Boolean> = MutableLiveData(false)
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    private var localDataModel : MutableLiveData<DatabaseModel> = MutableLiveData(DatabaseModel())
    var isUserSignIn : MutableLiveData<Boolean> = MutableLiveData(false)
    var signInError : MutableLiveData<String?> = MutableLiveData(null)

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    fun getDataModel() : DatabaseModel = localDataModel.value!!

    fun setRememberChecked(boolean: Boolean){
        isRememberChecked.value = boolean

    }

    fun signIn(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                localDataModel.value = DatabaseModel(
                    uid = auth.uid,
                    email = email,
                    remember = isRememberChecked.value!!
                )
                isUserSignIn.value = true
            } else {
                signInError.value = it.exception.toString()
                isUserSignIn.value = false
            }
        }
    }
}
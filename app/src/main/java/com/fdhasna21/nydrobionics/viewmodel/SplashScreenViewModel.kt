package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel.Companion.toFarmModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel.Companion.toUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SplashScreenViewModel : ViewModel() {
    private var auth : FirebaseAuth = Firebase.auth
    private var firestore : FirebaseFirestore = Firebase.firestore
    var currentUserModel : MutableLiveData<UserModel?> = MutableLiveData(null)
    var currentFarmModel : MutableLiveData<FarmModel?> = MutableLiveData(null)
    var isCurrentUserExist : MutableLiveData<Boolean?> = MutableLiveData(null)
    var isCurrentFarmExist : MutableLiveData<Boolean?> = MutableLiveData(null)

    companion object {
        const val TAG = "splashScreen"
    }

    fun getCurrentData(){
        try{
            auth.uid?.let {
                firestore.collection("users").document(auth.uid!!)
                    .get().addOnSuccessListener {
                        if(it.exists()){
                            currentUserModel.value = it.toUserModel()
                            isCurrentUserExist.value = true
                            currentUserModel.value?.farmId?.let {
                                firestore.collection("farms").document(it)
                                    .get().addOnSuccessListener {
                                        if(it.exists()){
                                            currentFarmModel.value = it.toFarmModel()
                                            Log.i(
                                                TAG,
                                                "getCurrentData: ${currentUserModel.value}\n" +
                                                        "${currentFarmModel.value}"
                                            )
                                            isCurrentFarmExist.value = true
                                        } else {
                                            isCurrentFarmExist.value = false
                                        }
                                    }
                            } ?: kotlin.run {
                                isCurrentFarmExist.value = false
                            }

                        } else {
                            isCurrentUserExist.value = false
                        }
                    }
            } ?: kotlin.run {
                isCurrentUserExist.value = false
            }
        } catch (e:Exception){
            Log.e(TAG, "getCurrentData ${auth.uid}", e)
        }
    }
}
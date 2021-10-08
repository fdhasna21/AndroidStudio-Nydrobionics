package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel.Companion.toFarmModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel.Companion.toUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainViewModel : ViewModel() {
    private var auth : FirebaseAuth = Firebase.auth
    private var database : FirebaseDatabase = Firebase.database
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var storage : FirebaseStorage = Firebase.storage
    var isUserSignOut : MutableLiveData<Boolean> = MutableLiveData(false)
    var signOutError : MutableLiveData<String> = MutableLiveData("")

    private var userModel : MutableLiveData<UserModel> = MutableLiveData(UserModel())
    private var isUserExist : MutableLiveData<Boolean> = MutableLiveData(false)

    var currentUser : FirebaseUser? = auth.currentUser
    var currentUserModel : MutableLiveData<UserModel> = MutableLiveData(UserModel())
    var isCurrentUserExist : MutableLiveData<Boolean?> = MutableLiveData(null)

    var currentFarmModel : MutableLiveData<FarmModel> = MutableLiveData(FarmModel())
    var isCurrentFarmExist : MutableLiveData<Boolean?> = MutableLiveData(null)

    companion object{
        const val TAG = "mainViewModel"
    }

    fun signOut(){
        signOutError.value = ""
        try {
            auth.signOut()
            isUserSignOut.value = true
        } catch (e:FirebaseAuthException){
            isUserSignOut.value = false
            signOutError.value = e.toString()
        } catch (e:Exception){
            isUserSignOut.value = false
            signOutError.value = e.toString()
        }
    }

    fun setCurrentUser(userModel: UserModel?){
        if(userModel != null){
            currentUserModel.value = userModel
            isCurrentUserExist.value = true
            this.userModel.value = UserModel()
            isUserExist.value = false
        } else {
            getUser(auth.uid!!)
        }
        Log.i(TAG, "currentUser : ${currentUserModel.value}")
    }

    fun getUser(userId : String){
        val db = firestore.collection("users").document(userId)
        db.get().addOnCompleteListener {
            it.result.let {
                if(it.exists()){
                    isUserExist.value = true
                    userModel.value = it.toUserModel()
                    if(userId == currentUser!!.uid){
                        setCurrentUser(userModel.value)
                        getCurrentFarm()
                    } else {
                        Log.i(TAG, "$it")
                    }
                } else{
                    isUserExist.value = false
                    if(userId == currentUser?.uid){
                        isCurrentUserExist.value = false
                    }
                }
            }
        }
    }

    private fun getCurrentFarm(){
        val farmId : String? = currentUserModel.value?.farmId
        if(farmId != null){
            val db = firestore.collection("farms").document(farmId)
            db.get().addOnCompleteListener{
                it.result.let {
                    isCurrentFarmExist.value = true
                    setCurrentFarm(it.toFarmModel())
                }
            }
        } else {
            isCurrentFarmExist.value = false
        }
    }

    fun setCurrentFarm(farmModel: FarmModel?){
        currentFarmModel.value = farmModel
        isCurrentFarmExist.value = true
        Log.i(TAG, "currentFarm : ${currentFarmModel.value}")
    }
}
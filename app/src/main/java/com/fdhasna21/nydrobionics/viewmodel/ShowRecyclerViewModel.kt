package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel.Companion.toPlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ShowRecyclerViewModel : ViewModel() {
    private var auth : FirebaseAuth = Firebase.auth
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var plantModels : MutableLiveData<ArrayList<PlantModel>> = MutableLiveData(null)
    private var userModel : MutableLiveData<UserModel> = MutableLiveData(null)

    fun setCurrentPosts(plantModel: ArrayList<PlantModel>?, userModel : UserModel?) {
        this.userModel.value = userModel
        plantModel?.let {
            val plants : ArrayList<PlantModel> = arrayListOf()
            it.forEach {
                if(it.userId == auth.uid){
                    plants.add(it)
                }
            }
            plantModels.value = plants
            getPlantsUpdate()
        } ?: kotlin.run {
            plantModels.value = arrayListOf()
            getPlantsUpdate()
        }
    }

    fun getCurrentPosts() : MutableLiveData<ArrayList<PlantModel>> = plantModels
    fun getCurrentUser() : MutableLiveData<UserModel> = userModel

    var isPostDeleted : MutableLiveData<Boolean?> = MutableLiveData(null)
    var deletePostError : MutableLiveData<String> = MutableLiveData(null)

    fun getPost(position: Int) : PlantModel?{
        return plantModels.value?.get(position)
    }

    fun deletePost(position: Int){
        isPostDeleted.value = null
        val plantModel = getPost(position)
        val plantId = plantModel?.plantId
        try {
            if(plantModel?.userId == auth.uid) {
                firestore.collection("plants").document(plantId!!)
                    .delete().addOnCompleteListener {
                        if (it.isSuccessful) {
                            isPostDeleted.value = true
                        } else {
                            isPostDeleted.value = false
                            deletePostError.value = it.exception.toString()
                            Log.e(MainViewModel.TAG, "deletePost $plantId\n ${it.exception}")
                        }
                    }
            }
        } catch (e:Exception){
            Log.e(MainViewModel.TAG, "deletePost $plantId failed", e)
        }
    }

    private fun getPlantsUpdate(){
        val db = firestore.collection("plants")
            .whereEqualTo("userId", auth.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        try {
            db.addSnapshotListener { plantsSnapshot, error ->
                plantsSnapshot?.let {
                    val plants : ArrayList<PlantModel> = arrayListOf()
                    for(plantDocument in plantsSnapshot.documents){
                        plantDocument?.let { plant ->
                            plant.toPlantModel()?.let { plantModel ->
                                plants.add(plantModel)
                            }
                        }
                    }
                    plantModels.value = plants
                    Log.i(MainViewModel.TAG, "getPlantsUpdate: ${plantModels.value?.size}")
                }

                error?.let {
                    Log.e(MainViewModel.TAG, "Listen plants failed", it)
                }
            }
        } catch (e:Exception){
            Log.e(MainViewModel.TAG, "real time plant error", e)
        }
    }
}

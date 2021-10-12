package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel.Companion.toPlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel.Companion.toUserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchViewModel : ViewModel(){
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var userModels : MutableLiveData<ArrayList<UserModel>> = MutableLiveData()
    private var plantModels : MutableLiveData<ArrayList<PlantModel>> = MutableLiveData()
    private var searchUsers : MutableLiveData<ArrayList<UserModel>> = MutableLiveData()
    private var searchPlants : MutableLiveData<ArrayList<PlantModel>> = MutableLiveData()

    var isSearchSuccess : MutableLiveData<Boolean> = MutableLiveData(false)
    var searchError : MutableLiveData<String> = MutableLiveData("")

    companion object {
        const val TAG = "searchViewModel"
    }
    fun getAllUsers(lastKey : String? = null){
        val db = firestore.collection("users")
        try {
            db.addSnapshotListener { snapshot, error ->
                snapshot?.let {
                    val users : ArrayList<UserModel> = arrayListOf()
                    for(plant in it.documents){
                        plant.toUserModel()?.let { it1 -> users.add(it1) }
                    }
                    userModels.value = users
                    searchUsers(lastKey)
                }

                error?.let {
                    Log.e(TAG, "Listen data users failed", it)
                    return@addSnapshotListener
                }
            }
        } catch (e:Exception){
            Log.e(TAG, "Error get plants", e)
        }
    }
    fun getAllPlants(lastKey : String? = null){
        val db = firestore.collection("plants")
        try {
            db.addSnapshotListener { snapshot, error ->
                snapshot?.let {
                    val plants : ArrayList<PlantModel> = arrayListOf()
                    for(plant in it.documents){
                        plant.toPlantModel()?.let { it1 -> plants.add(it1) }
                    }
                    plantModels.value = plants
                    searchPlants(lastKey)
                }

                error?.let {
                    Log.e(TAG, "Listen data plants failed", it)
                    return@addSnapshotListener
                }
            }
        } catch (e:Exception){
            Log.e(TAG, "Error get plants", e)
        }
    }

    fun getUsers() :  MutableLiveData<ArrayList<UserModel>> = searchUsers
    fun getPlants() : MutableLiveData<ArrayList<PlantModel>> = searchPlants

    fun getUser(position: Int) : UserModel? {
        return searchUsers.value?.get(position)
    }
    fun getPlant(position:Int) : PlantModel?{
        return searchPlants.value?.get(position)
    }

    fun searchUsers(key:String?) {
        if(key != null){
            val results : ArrayList<UserModel> = arrayListOf()
            for(user in userModels.value!!){
                if(user.name?.contains(key) == true){
                    results.add(user)
                }
            }
            searchUsers.value = results
        }
        else {
            searchUsers.value = userModels.value
        }
    }
    fun searchPlants(key:String?) {
        if(key != null && plantModels.value != null){
            val results : ArrayList<PlantModel> = arrayListOf()
            for(user in plantModels.value!!){
                if(user.name?.contains(key) == true){
                    results.add(user)
                }
            }
            searchPlants.value = results
        }
        else {
            searchPlants.value = plantModels.value
        }
    }
}

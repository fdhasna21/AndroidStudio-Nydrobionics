package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SearchViewModel : ViewModel(){
    private var auth = Firebase.auth
    private var users : MutableLiveData<ArrayList<UserModel>> = MutableLiveData()
    private var plants : MutableLiveData<ArrayList<PlantModel>> = MutableLiveData()

    var isSearchSuccess : MutableLiveData<Boolean> = MutableLiveData(false)
    var searchError : MutableLiveData<String> = MutableLiveData("")

    fun getUsers() :  MutableLiveData<ArrayList<UserModel>> = users

    fun getPlants() : MutableLiveData<ArrayList<PlantModel>> = plants

    fun searchUsers(key:String) {
    }

    fun searchPlants(key:String) {
    }
}

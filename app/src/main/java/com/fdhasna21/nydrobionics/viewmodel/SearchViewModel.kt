package com.fdhasna21.nydrobionics.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.Plant
import com.fdhasna21.nydrobionics.dataclass.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SearchViewModel : ViewModel(){
    private var auth = Firebase.auth
    private var users : MutableLiveData<ArrayList<User>> = MutableLiveData()
    private var plants : MutableLiveData<ArrayList<Plant>> = MutableLiveData()

    var isSearchSuccess : MutableLiveData<Boolean> = MutableLiveData(false)
    var searchError : MutableLiveData<String> = MutableLiveData("")

    fun getUsers() :  MutableLiveData<ArrayList<User>> = users

    fun getPlants() : MutableLiveData<ArrayList<Plant>> = plants

    fun searchUsers(key:String) {
    }

    fun searchPlants(key:String) {
    }
}

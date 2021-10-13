package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.*
import com.fdhasna21.nydrobionics.dataclass.model.CropsModel.Companion.replace
import com.fdhasna21.nydrobionics.dataclass.model.CropsModel.Companion.toCropsModel
import com.fdhasna21.nydrobionics.dataclass.model.DataMonitoringModel.Companion.replace
import com.fdhasna21.nydrobionics.dataclass.model.DataMonitoringModel.Companion.toDataMonitoringModel
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel.Companion.toFarmModel
import com.fdhasna21.nydrobionics.dataclass.model.KitModel.Companion.toKitModel
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel.Companion.toNoteModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel.Companion.toPlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel.Companion.toUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    private var auth : FirebaseAuth = Firebase.auth
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var currentUserModel : MutableLiveData<UserModel?> = MutableLiveData(null)
    private var currentFarmModel : MutableLiveData<FarmModel?> = MutableLiveData(null)
    private var currentKitModels : MutableLiveData<ArrayList<KitModel>?> = MutableLiveData(null)
    private var currentNoteModels : MutableLiveData<ArrayList<NoteModel>?> = MutableLiveData(null)
    private var allUserModels : MutableLiveData<ArrayList<UserModel>?> = MutableLiveData(null)
    private var allPlantModels : MutableLiveData<ArrayList<PlantModel>?> = MutableLiveData(null)

    companion object{
        const val TAG = "mainViewModel"
    }

    fun setCurrentData(userModel: UserModel?, farmModel: FarmModel?){
        getUsersUpdate()
        getPlantsUpdate()
        userModel?.let {
            currentUserModel.value = it
            farmModel?.let {
                currentFarmModel.value = it
            } ?: kotlin.run {
                getFarmsUpdate(currentFarmModel.value?.farmId)
            }
        } ?: kotlin.run {
            getUsersUpdate()
        }
    }

    /** GET DATA **/
    fun getCurrentUser() : MutableLiveData<UserModel?> = currentUserModel
    fun getCurrentFarm() : MutableLiveData<FarmModel?> = currentFarmModel
    fun getCurrentKits() : MutableLiveData<ArrayList<KitModel>?> = currentKitModels
    fun getCurrentNotes() : MutableLiveData<ArrayList<NoteModel>?> = currentNoteModels
    fun getAllUsers() : MutableLiveData<ArrayList<UserModel>?> = allUserModels
    fun getAllPosts() : MutableLiveData<ArrayList<PlantModel>?> = allPlantModels

    /** UPDATE DATABASE LISTENER **/
    private lateinit var usersListener : ListenerRegistration
    private lateinit var farmsListener : ListenerRegistration
    private lateinit var kitsListener : ListenerRegistration
    private var monitoringsListeners : ArrayList<ListenerRegistration> = arrayListOf()
    private var cropsListeners : ArrayList<ListenerRegistration> = arrayListOf()
    private lateinit var plantsListener : ListenerRegistration

    private fun getUsersUpdate(){
        val db = firestore.collection("users")
        try {
            usersListener =
                db.orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { usersSnapshot, error ->
                        usersSnapshot?.let {
                            val users : ArrayList<UserModel> = arrayListOf()
                            for(userDocument in usersSnapshot.documents){
                                userDocument?.let { user ->
                                    user.toUserModel()?.let { userModel ->
                                        users.add(userModel)
                                        val userId = userDocument.id
                                        if(auth.uid == userId) {
                                            currentUserModel.value = userModel
                                            getFarmsUpdate(userModel.farmId)

                                            val userRef = db.document(userId)
                                            userRef.collection("notes")
                                                .orderBy("timestamp", Query.Direction.DESCENDING)
                                                .addSnapshotListener { notesSnapshot, error ->
                                                    notesSnapshot?.let {
                                                        val notes: ArrayList<NoteModel> = arrayListOf()
                                                        for (noteDocument in notesSnapshot.documents) {
                                                            noteDocument?.let { note ->
                                                                note.toNoteModel()?.let { noteModel ->
                                                                    notes.add(noteModel)
                                                                }
                                                            }
                                                        }
                                                        currentNoteModels.value = notes
                                                        Log.i(TAG, "getUsersUpdate ${currentUserModel.value?.uid}" +
                                                                " \tnotes:${currentNoteModels.value?.size}\n")
                                                    }

                                                    error?.let {
                                                        Log.e(TAG, "Listen notes from $userId failed", it)
                                                    }
                                                }
                                        }
                                    }
                                }
                            }
                            allUserModels.value = users
                            Log.i(TAG, "getUsersUpdate: ${allUserModels.value?.size}\n")
                        }

                        error?.let {
                            Log.e(TAG, "Listen users failed", it)
                        }
                    }
        } catch (e:Exception){
            Log.e(TAG, "getUsersUpdate() error", e)
        }
    }

    private fun getFarmsUpdate(farmId: String?){
        if(farmId == null){
            currentFarmModel.value = null
        } else {
            try{
                val db = firestore.collection("farms").document(farmId)
                farmsListener = db.addSnapshotListener { farmSnapshot, error ->
                    farmSnapshot?.let { farm ->
                        farm.toFarmModel()?.let { farmModel ->
                            currentFarmModel.value = farmModel

                            val kitsRef = db.collection("kits")
                            kitsListener = kitsRef.addSnapshotListener{ kitSnapshot, error ->
                                kitSnapshot?.let { kitDocuments ->
                                    val kits : ArrayList<KitModel> = arrayListOf()
                                    kitDocuments.documents.forEachIndexed { index, kitDocument ->
                                        kitDocument?.toKitModel().let { kit ->
                                            kit?.let { kitModel ->
                                                val kitId = kitDocument.id
                                                val kitRef = kitsRef.document(kitId)
                                                kits.add(kitModel)

                                                val monitorListener = kitRef.collection("dataMonitorings")
                                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                                    .addSnapshotListener { monitoringSnapshot, error ->
                                                        monitoringSnapshot?.let { monitoringDocuments ->
                                                            if(monitoringDocuments.documents.size > 0){
                                                                val lastMonitoring : DataMonitoringModel? = monitoringDocuments.documents[0].toDataMonitoringModel()
                                                                updateKitModel(index, monitoringModel = lastMonitoring)
                                                            }
                                                        }

                                                        error?.let {
                                                            Log.e(TAG, "Listen dataMonitoring from kit $kitId failed", it)
                                                        }
                                                    }

                                                val cropsListener = kitRef.collection("crops")
                                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                                    .addSnapshotListener { cropsSnapshot, error ->
                                                        cropsSnapshot?.let { cropsDocuments ->
                                                            if(cropsDocuments.documents.size > 0){
                                                                val lastCropsModel : CropsModel? = cropsDocuments.documents[0].toCropsModel()
                                                                updateKitModel(index, cropsModel = lastCropsModel)
                                                            }
                                                        }

                                                        error?.let {
                                                            Log.e(TAG, "Listen crops from kit $kitId failed", it)
                                                        }
                                                    }

                                                monitoringsListeners.add(monitorListener)
                                                cropsListeners.add(cropsListener)
                                            }
                                        }
                                    }
                                    currentKitModels.value = kits
                                    updateKitModel(0)
                                }

                                error?.let {
                                    Log.e(TAG, "Listen kits from farm $farmId failed", it)
                                }
                            }
                        }

                        error?.let {
                            Log.e(TAG, "Listen farm $farmId failed", it)
                        }
                    }
                }
            } catch (e:Exception){
                Log.e(TAG, "real time farms error", e)
            }
        }

    }

    private fun getPlantsUpdate(){
        val db = firestore.collection("plants").orderBy("timestamp", Query.Direction.DESCENDING)
        try {
            plantsListener = db.addSnapshotListener { plantsSnapshot, error ->
                plantsSnapshot?.let {
                    val plants : ArrayList<PlantModel> = arrayListOf()
                    for(plantDocument in plantsSnapshot.documents){
                        plantDocument?.let { plant ->
                            plant.toPlantModel()?.let { plantModel ->
                                plants.add(plantModel)
                            }
                        }
                    }
                    allPlantModels.value = plants
                    Log.i(TAG, "getPlantsUpdate: ${allPlantModels.value?.size}")
                }

                error?.let {
                    Log.e(TAG, "Listen plants failed", it)
                }
            }
        } catch (e:Exception){
            Log.e(TAG, "real time plant error", e)
        }
    }

    private fun updateKitModel(index:Int, cropsModel: CropsModel?=null, monitoringModel: DataMonitoringModel? = null){
        try {
            currentKitModels.value?.let {
                val kitModels = it
                kitModels.forEachIndexed { kitIndex, kitModel ->
                    if(kitIndex == index){
                        cropsModel?.let {
                            kitModel.lastCrops = it
                        }
                        monitoringModel?.let {
                            kitModel.lastMonitoring = it
                        }
                    }
                }
                currentKitModels.value = kitModels
            }

            currentFarmModel.value?.let {
                val farmModel = it
                farmModel.kitModels = currentKitModels.value
                currentFarmModel.value = farmModel
            }
        }catch (e:Exception) {
            Log.e(TAG, "updateKitModel ${currentKitModels.value!![index]}\n", e)
        }

        Log.i(TAG, "getFarmsUpdate ${currentFarmModel.value?.farmId}" +
                " \tkits:${currentKitModels.value?.size}\n\n" +
                "${currentFarmModel.value}")
    }

    /** NOTES **/
    var isNoteDeleted : MutableLiveData<Boolean?> = MutableLiveData(null)
    var deleteNoteError : MutableLiveData<String> = MutableLiveData(null)

    fun getNote(position:Int) : NoteModel?{
        return currentNoteModels.value?.get(position)
    }

    fun deleteNote(position:Int){
        val noteId = getNote(position)?.noteId
        try {
            if(noteId == auth.uid!!) {
                firestore.collection("users").document(auth.uid!!)
                    .collection("notes").document(noteId!!)
                    .delete().addOnCompleteListener {
                        if (it.isSuccessful) {
                            isNoteDeleted.value = true
                        } else {
                            isNoteDeleted.value = false
                            deleteNoteError.value = it.exception.toString()
                            Log.e(TAG, "deleteNote $noteId\n ${it.exception}")
                        }
                    }
            }
        } catch (e:Exception){
            Log.e(TAG, "deleteNote $noteId failed", e)
            isNoteDeleted.value = false
            deleteNoteError.value = e.toString()
        }
    }

    fun refreshNotes(){
        usersListener.remove()
        currentNoteModels.value?.clear()
        getUsersUpdate()
    }

    /** POSTS **/
    var isPostDeleted : MutableLiveData<Boolean?> = MutableLiveData(null)
    var deletePostError : MutableLiveData<String> = MutableLiveData(null)

    fun getPost(position: Int) : PlantModel?{
        return allPlantModels.value?.get(position)
    }

    fun deletePost(position: Int){
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
                            Log.e(TAG, "deletePost $plantId\n ${it.exception}")
                        }
                    }
            }
        } catch (e:Exception){
            Log.e(TAG, "deletePost $plantId failed", e)
        }
    }

    fun refreshPosts(){
        plantsListener.remove()
        allPlantModels.value?.clear()
        getPlantsUpdate()
    }

    /** FARM DASHBOARD **/
    fun getKit(position: Int) : KitModel? {
        return currentKitModels.value?.get(position)
    }

    fun refreshFarm(){
        farmsListener.remove()
        kitsListener.remove()
        monitoringsListeners.forEach { it.remove() }
        cropsListeners.forEach { it.remove() }

        currentFarmModel.value?.let {
            val farmModel = it
            farmModel.kitModels?.clear()
            currentFarmModel.value = farmModel
        }
        currentKitModels.value?.clear()
        getFarmsUpdate(currentUserModel.value?.farmId)
    }

    /** SIGN OUT **/
    var isUserSignOut : MutableLiveData<Boolean?> = MutableLiveData(null)
    var signOutError : MutableLiveData<String?> = MutableLiveData(null)

    fun signOut(){
        try {
            auth.signOut()
            isUserSignOut.value = true
        } catch (e:Exception){
            isUserSignOut.value = false
            signOutError.value = e.toString()
        }
    }
}
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
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainViewModel : ViewModel() {
    private var auth : FirebaseAuth = Firebase.auth
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var storage : FirebaseStorage = Firebase.storage

    var currentUserModel : MutableLiveData<UserModel> = MutableLiveData(UserModel())
    var currentFarmModel : MutableLiveData<FarmModel> = MutableLiveData(FarmModel())
    private var currentKitModels : MutableLiveData<ArrayList<KitModel>> = MutableLiveData(arrayListOf())
    var currentNoteModels : MutableLiveData<ArrayList<NoteModel>?> = MutableLiveData(null)
    private var allUserModels : MutableLiveData<ArrayList<UserModel>?> = MutableLiveData(null)
    private var allPlantModels : MutableLiveData<ArrayList<PlantModel>?> = MutableLiveData(null)
    var isCurrentUserExist : MutableLiveData<Boolean?> = MutableLiveData(null)
    var isCurrentFarmExist : MutableLiveData<Boolean?> = MutableLiveData(null)
    var isUserSignOut : MutableLiveData<Boolean> = MutableLiveData(false)
    var signOutError : MutableLiveData<String> = MutableLiveData("")

    private lateinit var usersListener : ListenerRegistration

    companion object{
        const val TAG = "mainViewModel"
    }

    fun signOut(){
        signOutError.value = ""
        try {
            auth.signOut()
            isUserSignOut.value = true
        } catch (e:Exception){
            isUserSignOut.value = false
            signOutError.value = e.toString()
        }
    }

    fun setCurrentData(userModel: UserModel?, farmModel: FarmModel?){
        getAllData()
        if(userModel != null){
            currentUserModel.value = userModel
            isCurrentUserExist.value = true
        } else {
            getUsersUpdate()
            Log.i(TAG, "currentUser : ${currentUserModel.value}")
        }

        if(currentUserModel.value != null){
            if(farmModel  != null) {
                currentFarmModel.value = farmModel
                isCurrentFarmExist.value = true
            } else {
                getFarmsUpdate(currentUserModel.value?.farmId)
                Log.i(TAG, "currentFarm : ${currentFarmModel.value}")
            }
        }
    }

    fun getAllData(){
        getUsersUpdate()
        getPlantsUpdate()
    }

    private fun getUsersUpdate(){
        val db = firestore.collection("users")
        try {
            usersListener = db.addSnapshotListener { usersSnapshot, error ->
                usersSnapshot?.let {
                    val users : ArrayList<UserModel> = arrayListOf()
                    for(userDocument in usersSnapshot.documents){
                        userDocument?.let { user ->
                            user.toUserModel()?.let { userModel ->
                                users.add(userModel)
                                val userId = userDocument.id
                                if(auth.uid == userId) {
                                    currentUserModel.value = userModel
                                    isCurrentUserExist.value = true
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
                    if(isCurrentUserExist.value != true) {
                        isCurrentUserExist.value = false
                    }
                } ?: kotlin.run {
                    isCurrentUserExist.value = false
                }

                error?.let {
                    Log.e(TAG, "Listen users failed", it)
                }
            }
        } catch (e:Exception){
            Log.e(TAG, "real time user error", e)
        }
    }

    /** FARM **/
    private fun getFarmsUpdate(farmId: String?){
        if(farmId == null){
            isCurrentFarmExist.value = false
        } else {
            val db = firestore.collection("farms").document(farmId)
            try{
                db.addSnapshotListener { farmSnapshot, error ->
                    farmSnapshot?.let { farm ->
                        farm.toFarmModel()?.let { farmModel ->
                            currentFarmModel.value = farmModel
                            val kitsRef = db.collection("kits")
                            kitsRef.addSnapshotListener{ kitSnapshot, error ->
                                kitSnapshot?.let { kitDocuments ->
                                    kitDocuments.documents.forEachIndexed { index, kitDocument ->
                                        kitDocument?.toKitModel().let { kit ->
                                            kit?.let { kitModel ->
                                                val kitId = kitDocument.id
                                                val kitRef = kitsRef.document(kitId)

                                                val lastMonitoring : DataMonitoringModel = DataMonitoringModel()
                                                kitRef.collection("dataMonitorings")
                                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                                    .addSnapshotListener { dataMonitoringSnapshot, error ->
                                                        dataMonitoringSnapshot?.let { dataMonitoringDocuments ->
                                                            val dataMonitorings : ArrayList<DataMonitoringModel> = arrayListOf()
                                                            dataMonitoringDocuments.documents.let {
                                                                if (it.size > 0) {
                                                                    it.get(0)?.let {
                                                                        lastMonitoring.replace(it.toDataMonitoringModel())
                                                                        updateKitModel(index, monitoringModel = lastMonitoring)
//                                                                        Log.i(TAG, "monitoring: $lastMonitoring")
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        error?.let {
                                                            Log.e(TAG, "Listen data monitoring from kit $kitId failed", it)
                                                        }

                                                    }

                                                val lastCropsModel : CropsModel = CropsModel()
                                                kitRef.collection("crops")
                                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                                    .addSnapshotListener { cropsSnapshot, error ->
                                                        cropsSnapshot?.let { cropsDocuments ->
                                                            val crops : ArrayList<CropsModel> = arrayListOf()
                                                            cropsDocuments.documents.let{
                                                                if(it.size > 0){
                                                                    it.get(0)?.let{
                                                                        lastCropsModel.replace(it.toCropsModel())
                                                                        allPlantModels.value?.forEach {
                                                                            if(it.plantId == lastCropsModel.plantId){
                                                                                lastCropsModel.plantModel = it
                                                                                updateKitModel(index, cropsModel = lastCropsModel)
                                                                            }
//                                                                            Log.i(TAG, "crops: $lastCropsModel")
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        error?.let {
                                                            Log.e(TAG, "Listen crops from kit $kitId failed", it)
                                                        }
                                                    }

                                                if(currentKitModels.value?.size!! > index){
                                                    updateKitModel(index, lastCropsModel, lastMonitoring)
                                                } else {
                                                    kitModel.apply {
                                                        currentKitModels.value?.add(
                                                            KitModel(
                                                                this.kitId,
                                                                this.name,
                                                                this.position,
                                                                this.length,
                                                                this.width,
                                                                this.waterLv,
                                                                this.nutrientLv,
                                                                this.turbidityLv,
                                                                this.isPlanted,
                                                                this.timestamp,
                                                                lastMonitoring,
                                                                lastCrops
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
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

    private fun updateKitModel(index:Int, cropsModel: CropsModel?=null, monitoringModel: DataMonitoringModel? = null){
        if(cropsModel!=null){
            currentKitModels.value!![index].lastCrops = cropsModel
            isCurrentFarmExist.value = true
        }
        if (monitoringModel!=null) {
            currentKitModels.value!![index].lastMonitoring = monitoringModel
        }
        currentFarmModel.value?.kitModels = currentKitModels.value
//        Log.i(TAG, "currentkit ${currentKitModels.value}, currentfarm ${currentFarmModel.value}")
    }

    /** PLANT **/
    fun getPlantsUpdate(){
        val db = firestore.collection("plants")
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
                    allPlantModels.value = plants
                }

                error?.let {
                    Log.e(TAG, "Listen plants failed", it)
                }
            }
        } catch (e:Exception){
            Log.e(TAG, "real time plant error", e)
        }
    }

    /** NOTES **/
    var isNoteDeleted : MutableLiveData<Boolean?> = MutableLiveData(null)
    var deleteNoteError : MutableLiveData<String> = MutableLiveData("")
    fun getNote(position:Int) : NoteModel?{
        return currentNoteModels.value?.get(position)
    }

    fun deleteNote(position:Int){
        val noteId = getNote(position)?.noteId
        try {
            val db = firestore.collection("users").document(auth.uid!!)
                .collection("notes").document(noteId!!)
            db.delete().addOnCompleteListener {
                if(it.isSuccessful){
                    isNoteDeleted.value = true
                } else {
                    isNoteDeleted.value = false
                    deleteNoteError.value = it.exception.toString()
                }
            }
        } catch (e:Exception){
            Log.e(TAG, "deleteNote $noteId: failed", e)
        }
    }

    fun refreshNote(){
        usersListener.remove()
        currentNoteModels.value?.clear()
        getUsersUpdate()
    }
}
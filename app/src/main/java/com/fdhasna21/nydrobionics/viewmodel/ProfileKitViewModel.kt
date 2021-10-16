package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.*
import com.fdhasna21.nydrobionics.dataclass.model.CropsModel.Companion.toCropsModel
import com.fdhasna21.nydrobionics.dataclass.model.DataMonitoringModel.Companion.toDataMonitoringModel
import com.fdhasna21.nydrobionics.dataclass.model.KitModel.Companion.toKitModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel.Companion.toPlantModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileKitViewModel : ViewModel() {
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var currentUserModel : MutableLiveData<UserModel> = MutableLiveData(null)
    private var currentKitModel : MutableLiveData<KitModel> = MutableLiveData(null)
    private var currentFarmModel : MutableLiveData<FarmModel> = MutableLiveData(null)

    private var monitoringArray : MutableLiveData<ArrayList<DataMonitoringModel>> = MutableLiveData(null)
    private var cropsArray : MutableLiveData<ArrayList<CropsModel>> = MutableLiveData(null)
    private lateinit var kitListener : ListenerRegistration
    private lateinit var monitoringListener : ListenerRegistration
    private lateinit var cropsListener : ListenerRegistration

    companion object {
        const val TAG = "profileKitViewModel"
    }

    fun setCurrentKit(userModel: UserModel?, farmModel: FarmModel?, kitModel: KitModel?) {
        userModel?.let { user ->
            currentUserModel.value = user
            farmModel?.let {
                currentFarmModel.value = it
            }
            kitModel?.let { kit ->
                currentKitModel.value = kit
                getKitUpdate()
            }
        }
        Log.i(TAG, "setCurrentKit: $userModel $kitModel")
    }

    private fun getKitUpdate() {
        try{
            val db = firestore.collection("farms").document(currentUserModel.value?.farmId!!)
                .collection("kits").document(currentKitModel.value?.kitId!!)

            kitListener = db.addSnapshotListener { kitSnapshot, error ->
                kitSnapshot?.let {
                    currentKitModel.value = it.toKitModel()
                }

                error?.let {
                    Log.e(TAG, "Listen kits failed", it)
                }
            }

            monitoringListener = db.collection("dataMonitorings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { monitoringSnapshot, error ->
                    monitoringSnapshot?.let {
                        val monitorings : ArrayList<DataMonitoringModel> = arrayListOf()
                        for(each in it.documents){
                            each.toDataMonitoringModel()?.let {
                                monitorings.add(it)
                            }
                        }
                        monitoringArray.value = monitorings
                    }
                    error?.let {
                        Log.e(TAG, "Listen monitoring failed", it)
                    }
            }

            cropsListener = db.collection("crops")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { cropsSnapshot, error ->
                    cropsSnapshot?.let {
                        val crops : ArrayList<CropsModel> = arrayListOf()
                        for(each in it.documents){
                            each.toCropsModel()?.let { cropsModel ->
                                cropsModel.plantId?.let{ plantId ->
                                    firestore.collection("plants").document(plantId)
                                        .get().addOnSuccessListener {
                                            cropsModel.plantModel = it.toPlantModel()
                                        }
                                }
                                crops.add(cropsModel)
                            }
                        }
                        cropsArray.value = crops
                    }

                    error?.let {
                        Log.e(TAG, "Listen crops failed", it)
                    }
            }
        } catch (e:Exception){
            Log.e(TAG, "getKitUpdate: ",e )
        }
    }

    fun getCurrentKit() = currentKitModel
    fun getCurrentMonitoring() = monitoringArray
    fun getCurrentCrops() = cropsArray
    fun getCurrentUser() = currentUserModel
    fun getCurrentFarm() = currentFarmModel
}

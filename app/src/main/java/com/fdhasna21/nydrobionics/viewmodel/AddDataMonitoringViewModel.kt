package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.*
import com.fdhasna21.nydrobionics.dataclass.model.CropsModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.DataMonitoringModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.KitModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.enumclass.NumberPickerType
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddDataMonitoringViewModel : ViewModel() {
    private var auth : FirebaseAuth = Firebase.auth
    private var firestore : FirebaseFirestore = Firebase.firestore
    var currentUserModel : MutableLiveData<UserModel> = MutableLiveData(UserModel())
    var currentFarmModel : MutableLiveData<FarmModel> = MutableLiveData(FarmModel())
    private var currentKitModel : MutableLiveData<KitModel> = MutableLiveData(null)
    private var currentPlantModel : MutableLiveData<PlantModel> = MutableLiveData(null)
    private var currentCropsModel : MutableLiveData<CropsModel> = MutableLiveData(null)
    private var dataMonitoringModel : MutableLiveData<DataMonitoringModel> = MutableLiveData(DataMonitoringModel())
    private var kitSelectors : MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf<String>())
    private var availableKits : MutableLiveData<ArrayList<KitModel>> = MutableLiveData(arrayListOf())

    private var water : MutableLiveData<Float> = MutableLiveData(0f)
    private var nutrient : MutableLiveData<Float> = MutableLiveData(0f)
    private var turb : MutableLiveData<Float> = MutableLiveData(0f)
    private var temp : MutableLiveData<Float> = MutableLiveData(0f)
    private var humid : MutableLiveData<Float> = MutableLiveData(0f)
    private var acid : MutableLiveData<Float> = MutableLiveData(0f)
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isDataMonitoringAdd: MutableLiveData<Boolean> = MutableLiveData(false)
    var addDataMonitoringError : MutableLiveData<String> = MutableLiveData("")
    var isCropsAdd: MutableLiveData<Boolean> = MutableLiveData(false)
    var addCropsError : MutableLiveData<String> = MutableLiveData("")
    private var isMenuAddCrops : MutableLiveData<Boolean> = MutableLiveData(false)

    companion object {
        const val TAG = "addDataMonitoringViewModel"
    }

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    fun setCurrentData(userModel: UserModel?, farmModel: FarmModel?, isAddCrops:Boolean){
        isMenuAddCrops.value = isAddCrops
        userModel?.let {
            this.currentUserModel.value = it
        }
        farmModel?.let {
            this.currentFarmModel.value = it
            val kitSelectors : MutableList<String> = mutableListOf()
            currentFarmModel.value?.kitModels?.let {
                val plantTrue : ArrayList<Int> = arrayListOf()
                it.forEachIndexed { index, kit ->
                    if((isAddCrops && kit.isPlanted != true)) {
                        kitSelectors.add(kit.name.toString())
                        availableKits.value?.add(kit)
                    }
                    if(!isAddCrops){
                        kitSelectors.add(kit.name.toString())
                    }
                }
                this.kitSelectors.value = kitSelectors
            }
            Log.i(TAG, "$farmModel")
        }
    }

    fun setCurrentKit(position:Int){
        currentKitModel.value = if(isMenuAddCrops.value == true){
            availableKits.value?.get(position)
        } else {
            currentFarmModel.value?.kitModels?.get(position)
        }
        val cropsModel = currentFarmModel.value?.kitModels?.get(position)?.lastCrops
        currentCropsModel.value = CropsModel()
        Log.i(TAG, "setCurrentKit: ${currentKitModel.value}")
    }

    fun getKitSelector() : MutableLiveData<MutableList<String>> = kitSelectors

    fun getCurrentKit() : MutableLiveData<KitModel> = currentKitModel

    fun getCurrentCrops() : MutableLiveData<CropsModel> = currentCropsModel

    fun setCurrentPlant(plantModel: PlantModel?) {
        currentPlantModel.value = plantModel
    }

    fun getCurrentPlant() : MutableLiveData<PlantModel> = currentPlantModel

    fun setNumberPickerValue(currentValue : Float, type: NumberPickerType?){
        when(type){
            NumberPickerType.WATER_TANK -> water.value = currentValue
            NumberPickerType.NUTRIENT_TANK -> nutrient.value = currentValue
            NumberPickerType.TURBIDITY -> turb.value = currentValue
            NumberPickerType.TEMPERATURE -> temp.value = currentValue
            NumberPickerType.HUMIDITY -> humid.value = currentValue
            NumberPickerType.ACIDITY -> acid.value = currentValue
            else -> { }
        }
    }

    fun getNumberPickerValue(type: NumberPickerType?) : MutableLiveData<Float>?{
        return when(type){
            NumberPickerType.WATER_TANK -> water
            NumberPickerType.NUTRIENT_TANK -> nutrient
            NumberPickerType.TURBIDITY -> turb
            NumberPickerType.TEMPERATURE -> temp
            NumberPickerType.HUMIDITY -> humid
            NumberPickerType.ACIDITY -> acid
            else -> null
        }
    }

    /** ADD DATA MONITORING **/
    fun addDataMonitoring(){
        val db = firestore.collection("farms").document(currentFarmModel.value?.farmId!!)
            .collection("kits").document(currentKitModel.value?.kitId!!)
            .collection("dataMonitorings")
        val ref : DocumentReference = db.document()

        try {
            dataMonitoringModel.value?.apply {
                this.dataId = ref.id
                this.userId = auth.uid
                this.timestamp = ViewUtility().getCurrentTimestamp()
                this.temperature = temp.value
                this.humidity = humid.value
                this.turbidity = turb.value
                this.waterTank = water.value
                this.nutrientTank = nutrient.value
                this.ph = acid.value
            }

            db.document(ref.id).set(dataMonitoringModel.value!!.toHashMap()).addOnCompleteListener {
                if(it.isSuccessful){
                    isDataMonitoringAdd.value = true
                } else {
                    addDataMonitoringError.value = it.exception.toString()
                    isDataMonitoringAdd.value = false
                }
            }

        } catch (e:Exception){
            Log.e(TAG, "Error submit data monitoring", e)
        }
    }

    /** ADD CROPS **/
    fun addCrops(){
        val db = firestore.collection("farms").document(currentFarmModel.value?.farmId!!)
            .collection("kits").document(currentKitModel.value?.kitId!!)
            .collection("crops")
        val ref : DocumentReference = db.document()

        try{
            currentCropsModel.value.apply {
                currentCropsModel.value = CropsModel(
                    cropsId = ref.id,
                    userId = auth.uid,
                    plantId = currentPlantModel.value?.plantId,
                    tempLv = currentPlantModel.value?.tempLv,
                    humidLv = currentPlantModel.value?.humidLv,
                    phLv = currentPlantModel.value?.phLv
                )

                db.document(ref.id).set(currentCropsModel.value!!.toHashMap()).addOnCompleteListener {
                    if(it.isSuccessful){
                        updateKit(ref.id)
                    } else {
                        addCropsError.value = it.exception.toString()
                        isCropsAdd.value = false
                    }
                }
            }

        } catch (e:Exception){
            Log.e(TAG, "Error add crops to ${currentKitModel.value?.kitId}", e)
        }
    }

    private fun updateKit(cropsId : String){
        val db = firestore.collection("farms").document(currentFarmModel.value?.farmId!!)
            .collection("kits").document(currentKitModel.value?.kitId!!)
        try {
            currentKitModel.value?.isPlanted = true
            db.set(currentKitModel.value!!.toHashMap()).addOnCompleteListener {
                if(it.isSuccessful){
                    isCropsAdd.value = true
                    if(isMenuAddCrops.value == false) {
                        dataMonitoringModel.value?.cropsId = cropsId
                        addDataMonitoring()
                    }
                } else {
                    addCropsError.value = it.exception.toString()
                    isCropsAdd.value = false
                }
            }
        } catch (e:Exception){
            Log.e(TAG, "Error update kit ${currentKitModel.value?.kitId}", e)
        }
    }
}
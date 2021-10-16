package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel
import com.fdhasna21.nydrobionics.dataclass.model.DataMonitoringModel
import com.fdhasna21.nydrobionics.dataclass.model.DataMonitoringModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.model.KitModel
import com.fdhasna21.nydrobionics.dataclass.model.KitModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.enumclass.NumberPickerType
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddKitViewModel : ViewModel() {
    private var firestore : FirebaseFirestore = Firebase.firestore
    var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isKitAdd : MutableLiveData<Boolean> = MutableLiveData(false)
    var addKitError : MutableLiveData<String> = MutableLiveData("")

    private var currentUserModel : MutableLiveData<UserModel> = MutableLiveData(null)
    private var currentFarmModel : MutableLiveData<FarmModel> = MutableLiveData(null)
    private var currentKitModel : MutableLiveData<KitModel> = MutableLiveData(KitModel())

    private var kitWidth : MutableLiveData<Float> = MutableLiveData(0f)
    private var kitLength : MutableLiveData<Float> = MutableLiveData(0f)
    private var waterMin : MutableLiveData<Float> = MutableLiveData(0f)
    private var waterMax : MutableLiveData<Float> = MutableLiveData(0f)
    private var nutrientMin : MutableLiveData<Float> = MutableLiveData(0f)
    private var nutrientMax : MutableLiveData<Float> = MutableLiveData(0f)
    private var turbidityMin : MutableLiveData<Float> = MutableLiveData(0f)
    private var turbidityMax : MutableLiveData<Float> = MutableLiveData(0f)


    companion object {
        const val TAG = "addKitViewModel"
    }

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    fun setCurrentData(userModel: UserModel?, farmModel: FarmModel?, kitModel: KitModel?){
        currentUserModel.value = userModel
        currentFarmModel.value = farmModel
        kitModel?.let{
            this.currentKitModel.value = it
        }
    }

    fun getCurrentFarm():MutableLiveData<FarmModel> = currentFarmModel
    fun getCurrentKit() : MutableLiveData<KitModel> = currentKitModel


    fun setNumberPickerValue(currentValue : Float, type: NumberPickerType?){
        when(type){
            NumberPickerType.KIT_WIDTH -> kitWidth.value = currentValue
            NumberPickerType.KIT_LENGTH -> kitLength.value = currentValue
            NumberPickerType.WATER_MIN -> waterMin.value = currentValue
            NumberPickerType.WATER_MAX -> waterMax.value = currentValue
            NumberPickerType.NUTRIENT_MIN -> nutrientMin.value = currentValue
            NumberPickerType.NUTRIENT_MAX -> nutrientMax.value = currentValue
            NumberPickerType.TURBIDITY_MIN -> turbidityMin.value = currentValue
            NumberPickerType.TURBIDITY_MAX -> turbidityMax.value = currentValue
            else -> { }
        }
    }

    fun getNumberPickerValue(type: NumberPickerType?) : MutableLiveData<Float>?{
        return when(type){
            NumberPickerType.KIT_WIDTH -> kitWidth
            NumberPickerType.KIT_LENGTH -> kitLength
            NumberPickerType.WATER_MIN -> waterMin
            NumberPickerType.WATER_MAX -> waterMax
            NumberPickerType.NUTRIENT_MIN -> nutrientMin
            NumberPickerType.NUTRIENT_MAX ->nutrientMax
            NumberPickerType.TURBIDITY_MIN -> turbidityMin
            NumberPickerType.TURBIDITY_MAX -> turbidityMax
            else -> null
        }
    }

    fun createKit(name:String, position:String){
        val db = firestore.collection("farms").document(currentUserModel.value?.farmId!!).collection("kits")
        val ref : DocumentReference = db.document()

        try {
            currentKitModel.value?.apply {
                this.kitId = kitId ?: ref.id
                this.isPlanted = isPlanted ?: false
                this.name = name
                this.position = position
                this.length = kitLength.value?.toInt()
                this.width = kitWidth.value?.toInt()
                this.waterLv = ScoreLevel(waterMin.value, waterMax.value)
                this.nutrientLv = ScoreLevel(nutrientMin.value, nutrientMax.value)
                this.turbidityLv = ScoreLevel(turbidityMin.value, turbidityMax.value)
                this.timestamp = ViewUtility().getCurrentTimestamp()
            }

            val kitRef = db.document(currentKitModel.value!!.kitId!!)
                kitRef.set(currentKitModel.value!!.toHashMap()).addOnCompleteListener {
                if(it.isSuccessful){
                    if(currentKitModel.value!!.kitId == ref.id){
                        val dataRef = kitRef.collection("dataMonitorings")
                        val dataId = dataRef.document().id
                        val initialData = DataMonitoringModel(userId = Firebase.auth.uid, dataId = dataId)
                        dataRef.document(dataId).set(initialData.toHashMap()).addOnCompleteListener {
                            if(it.isSuccessful){
                                isKitAdd.value = true
                            }
                        }
                    } else {
                        isKitAdd.value = true
                    }
                } else {
                    addKitError.value = it.exception.toString()
                    isKitAdd.value = false
                }
            }
        } catch (e:Exception){
            Log.e(TAG, "Error submit kit", e)
        }
    }
}

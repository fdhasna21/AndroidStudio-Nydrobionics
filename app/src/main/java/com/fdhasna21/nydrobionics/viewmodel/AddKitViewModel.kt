package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.model.KitModel
import com.fdhasna21.nydrobionics.dataclass.model.KitModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.enumclass.NumberPickerDataType
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddKitViewModel : ViewModel() {
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isKitAdd : MutableLiveData<Boolean> = MutableLiveData(false)
    var addKitError : MutableLiveData<String> = MutableLiveData("")

    var currentUserModel : MutableLiveData<UserModel> = MutableLiveData(UserModel())
    var currentFarmModel : MutableLiveData<FarmModel> = MutableLiveData(FarmModel())

    private var kitWidth : MutableLiveData<ScoreLevel> = MutableLiveData(ScoreLevel(max=20f))
    private var kitLength : MutableLiveData<ScoreLevel> = MutableLiveData(ScoreLevel(max=20f))
    private var waterMin : MutableLiveData<ScoreLevel> = MutableLiveData(ScoreLevel())
    private var waterMax : MutableLiveData<ScoreLevel> = MutableLiveData(ScoreLevel(max=100f))
    private var nutrientMin : MutableLiveData<ScoreLevel> = MutableLiveData(ScoreLevel())
    private var nutrientMax : MutableLiveData<ScoreLevel> = MutableLiveData(ScoreLevel(max=100f))
    private var turbidityMin : MutableLiveData<ScoreLevel> = MutableLiveData(ScoreLevel())
    private var turbidityMax : MutableLiveData<ScoreLevel> = MutableLiveData(ScoreLevel(max=25f))
    private var kitModel : MutableLiveData<KitModel> = MutableLiveData(KitModel())
    //todo : mekanisme max min nya belom

    companion object {
        const val TAG = "addKit"
    }

    fun setCurrentData(userModel: UserModel?, farmModel: FarmModel?, kitModel: KitModel?){
        currentUserModel.value = userModel
        currentFarmModel.value = farmModel
        kitModel?.let{
            this.kitModel.value = it
        }
    }

    fun setNumberPickerValue(currentValue : Float, type: NumberPickerDataType?){
        when(type){
            NumberPickerDataType.KIT_WIDTH -> kitWidth.value?.score = currentValue
            NumberPickerDataType.KIT_LENGTH -> kitLength.value?.score = currentValue
            NumberPickerDataType.WATER_MIN -> {
                waterMin.value?.score = currentValue
                waterMin.value?.max = waterMax.value?.score
            }
            NumberPickerDataType.WATER_MAX -> {
                waterMax.value?.score = currentValue
                waterMax.value?.min = waterMin.value?.score
            }
            NumberPickerDataType.NUTRIENT_MIN -> {
                nutrientMin.value?.score = currentValue
                nutrientMin.value?.max = nutrientMax.value?.score
            }
            NumberPickerDataType.NUTRIENT_MAX -> {
                nutrientMax.value?.score = currentValue
                nutrientMax.value?.min = nutrientMin.value?.score
            }
            NumberPickerDataType.TURBIDITY_MIN -> {
                turbidityMin.value?.score = currentValue
                turbidityMin.value?.max = turbidityMax.value?.score
            }
            NumberPickerDataType.TURBIDITY_MAX -> {
                turbidityMax.value?.score = currentValue
                turbidityMax.value?.min = turbidityMin.value?.score
            }
            else -> { }
        }
    }

    fun getNumberPickerValue(type: NumberPickerDataType?) : MutableLiveData<ScoreLevel>?{
        return when(type){
            NumberPickerDataType.KIT_WIDTH -> kitWidth
            NumberPickerDataType.KIT_LENGTH -> kitLength
            NumberPickerDataType.WATER_MIN -> waterMin
            NumberPickerDataType.WATER_MAX -> waterMax
            NumberPickerDataType.NUTRIENT_MIN -> nutrientMin
            NumberPickerDataType.NUTRIENT_MAX ->nutrientMax
            NumberPickerDataType.TURBIDITY_MIN -> turbidityMin
            NumberPickerDataType.TURBIDITY_MAX -> turbidityMax
            else -> null
        }
    }

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }


    fun createKit(name:String, position:String){
        val db = firestore.collection("farms").document(currentUserModel.value?.farmId!!).collection("kits")
        val ref : DocumentReference = db.document()

        kitModel.value?.apply {
            this.kitId = ref.id
            this.name = name
            this.position = position
            this.length = kitLength.value?.score?.toInt()
            this.width = kitWidth.value?.score?.toInt()
            this.waterLv = ScoreLevel(waterMin.value?.score, waterMax.value?.score)
            this.nutrientLv = ScoreLevel(nutrientMin.value?.score, nutrientMax.value?.score)
            this.turbidityLv = ScoreLevel(turbidityMin.value?.score, turbidityMax.value?.score)
        }

        Log.i(TAG, "${kitModel.value}")

        db.document(ref.id).set(kitModel.value!!.toHashMap()).addOnCompleteListener {
            if(it.isSuccessful){
                isKitAdd.value = true
            } else {
                addKitError.value = it.exception.toString()
                isKitAdd.value = false
            }
        }
    }
}

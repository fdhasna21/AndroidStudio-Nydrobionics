package com.fdhasna21.nydrobionics.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel
import com.fdhasna21.nydrobionics.dataclass.UriFileExtensions
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.enumclass.NumberPickerType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

class AddPlantViewModel : ViewModel() {
    private var auth : FirebaseAuth = Firebase.auth
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var storage : FirebaseStorage = Firebase.storage
    private var growth : MutableLiveData<Float> = MutableLiveData(0f)
    private var tempMin : MutableLiveData<Float> = MutableLiveData(0f)
    private var tempMax : MutableLiveData<Float> = MutableLiveData(0f)
    private var humidMin : MutableLiveData<Float> = MutableLiveData(0f)
    private var humidMax : MutableLiveData<Float> = MutableLiveData(0f)
    private var acidMin : MutableLiveData<Float> = MutableLiveData(0f)
    private var acidMax : MutableLiveData<Float> = MutableLiveData(0f)
    private var imageUri : MutableLiveData<UriFileExtensions> = MutableLiveData(null)

    var plantModel : MutableLiveData<PlantModel> = MutableLiveData(PlantModel())

    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isPlantAdd : MutableLiveData<Boolean> = MutableLiveData(false)
    var addPlantError : MutableLiveData<String> = MutableLiveData("")

    companion object {
        const val TAG = "addPlant"
    }

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    fun setCurrentData(plantModel: PlantModel?){
        plantModel?.let{
            this.plantModel.value = it
        }
    }

    fun setNumberPickerValue(currentValue : Float, type: NumberPickerType?){
        when(type){
            NumberPickerType.GROWTH_TIME -> growth.value = currentValue
            NumberPickerType.TEMP_MIN -> tempMin.value = currentValue
            NumberPickerType.TEMP_MAX -> tempMax.value = currentValue
            NumberPickerType.HUMID_MIN -> humidMin.value = currentValue
            NumberPickerType.HUMID_MAX -> humidMax.value = currentValue
            NumberPickerType.ACID_MIN -> acidMin.value = currentValue
            NumberPickerType.ACID_MAX -> acidMax.value = currentValue
            else -> { }
        }
    }

    fun getNumberPickerValue(type: NumberPickerType?) : MutableLiveData<Float>?{
        return when(type){
            NumberPickerType.GROWTH_TIME -> growth
            NumberPickerType.TEMP_MIN -> tempMin
            NumberPickerType.TEMP_MAX -> tempMax
            NumberPickerType.HUMID_MIN -> humidMin
            NumberPickerType.HUMID_MAX -> humidMax
            NumberPickerType.ACID_MIN -> acidMin
            NumberPickerType.ACID_MAX -> acidMax
            else -> null
        }
    }
    
    fun createPlant(name:String, description:String){
        try{
            plantModel.value?.apply {
                this.name = name
                this.description = description
                this.growthTime = growth.value?.toInt()
                this.tempLv = ScoreLevel(tempMin.value, tempMax.value)
                this.humidLv = ScoreLevel(humidMin.value, humidMax.value)
                this.phLv = ScoreLevel(acidMin.value, acidMax.value)
                this.userId = auth.uid!!
            }

            if(imageUri.value != null){
                val storageReference : StorageReference = storage.getReference("profile_images")
                    .child(System.currentTimeMillis().toString() + ".${imageUri.value?.fileExtensions!!}")
                val uploadTask = storageReference.putFile(imageUri.value?.uri!!)
                uploadTask.continueWithTask {
                    if(!it.isSuccessful){
                        throw it.exception!!.cause!!
                    }
                    storageReference.downloadUrl
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result.let {
                            plantModel.value?.photo_url = it.toString()
                            sendPlantProfile()
                        }
                    }
                }
            } else {
                sendPlantProfile()
            }
        } catch (e:Exception){
            Log.e(TAG, "Error submit plant ", e)
        }
    }

    private fun sendPlantProfile(){
        val db = firestore.collection("plants")
        val ref : DocumentReference = db.document()

        plantModel.value?.apply {
            this.plantId = plantId ?: ref.id
        }

        db.document(ref.id).set(plantModel.value!!.toHashMap()).addOnCompleteListener {
            if(it.isSuccessful){
                isPlantAdd.value = true
            } else {
                addPlantError.value = it.exception.toString()
                isPlantAdd.value = false
            }
        }
    }

    fun setPhotoPlant(uri : Uri?, fileExtension: String?=null){
        imageUri.value = if(uri != null){
            UriFileExtensions(uri, fileExtension!!)
        } else {
            null
        }
        Log.i(TAG, "setPhotoPlant: $uri")
    }

    fun getPhotoProfile() : MutableLiveData<UriFileExtensions>{
        return imageUri
    }
}

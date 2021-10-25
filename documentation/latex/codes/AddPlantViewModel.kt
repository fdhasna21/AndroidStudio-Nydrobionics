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

    var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isPlantAdd : MutableLiveData<Boolean> = MutableLiveData(false)
    var addPlantError : MutableLiveData<String> = MutableLiveData("")

    companion object {
        const val TAG = "addPlantViewModel"
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

        db.document(plantModel.value!!.plantId!!).set(plantModel.value!!.toHashMap()).addOnCompleteListener {
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

    fun getCurrentPlant() : MutableLiveData<PlantModel>{
        return plantModel
    }
}

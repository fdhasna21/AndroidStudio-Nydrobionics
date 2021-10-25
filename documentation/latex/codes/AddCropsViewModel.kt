class AddCropsViewModel : ViewModel() {
    var isPlantExist: MutableLiveData<Boolean> = MutableLiveData(false)
    private var plantModel : MutableLiveData<PlantModel> = MutableLiveData(PlantModel())

    fun setPlantModel(plantModel: PlantModel) {
        this.plantModel.value = plantModel
        isPlantExist.value = true
    }

    fun getPlantModel() : MutableLiveData<PlantModel> = plantModel
}

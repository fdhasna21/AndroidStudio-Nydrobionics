class ProfileUserViewModel : ViewModel(){
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var userModel : MutableLiveData<UserModel> = MutableLiveData(null)
    private var plantModels : MutableLiveData<ArrayList<PlantModel>> = MutableLiveData(null)
    private var allUsers : MutableLiveData<ArrayList<UserModel>> = MutableLiveData(null)

    companion object {
        const val TAG = "profileUserViewModel"
    }

    fun setUserModel(userModel: UserModel?){
        this.userModel.value = userModel
        try {
            this.userModel.value?.let { user ->
                firestore.collection("plants")
                    .whereEqualTo("userId", user.uid)
                    .addSnapshotListener { plantSnapshot, error ->
                        plantSnapshot?.let {
                            val plants : ArrayList<PlantModel> = arrayListOf()
                            for(plantDoc in it.documents){
                                plantDoc.toPlantModel()?.let {
                                    plants.add(it)
                                }
                            }
                            plantModels.value = plants
                        }
                    }
            }
        } catch (e:Exception){
            Log.e(TAG, "setUserModel: ", )
        }
    }

    fun getUserModel() : MutableLiveData<UserModel> = userModel
    fun getUserPosts() : MutableLiveData<ArrayList<PlantModel>> = plantModels
}

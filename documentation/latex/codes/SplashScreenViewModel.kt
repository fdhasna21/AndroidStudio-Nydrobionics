class SplashScreenViewModel : ViewModel() {
    private var auth : FirebaseAuth = Firebase.auth
    private var firestore : FirebaseFirestore = Firebase.firestore
    var currentUserModel : MutableLiveData<UserModel?> = MutableLiveData(null)
    var currentFarmModel : MutableLiveData<FarmModel?> = MutableLiveData(null)
    var isCurrentUserExist : MutableLiveData<Boolean?> = MutableLiveData(null)
    var isCurrentFarmExist : MutableLiveData<Boolean?> = MutableLiveData(null)

    companion object {
        const val TAG = "splashScreen"
    }

    fun getCurrentData(){
        try{
            auth.uid?.let {
                firestore.collection("users").document(auth.uid!!)
                    .get().addOnSuccessListener {
                        if(it.exists()){
                            currentUserModel.value = it.toUserModel()
                            isCurrentUserExist.value = true
                            currentUserModel.value?.farmId?.let {
                                firestore.collection("farms").document(it)
                                    .get().addOnSuccessListener {
                                        if(it.exists()){
                                            currentFarmModel.value = it.toFarmModel()
                                            Log.i(
                                                TAG,
                                                "getCurrentData: ${currentUserModel.value}\n" +
                                                        "${currentFarmModel.value}"
                                            )
                                            isCurrentFarmExist.value = true
                                        } else {
                                            isCurrentFarmExist.value = false
                                        }
                                    }
                            } ?: kotlin.run {
                                isCurrentFarmExist.value = false
                            }

                        } else {
                            isCurrentUserExist.value = false
                        }
                    }
            } ?: kotlin.run {
                isCurrentUserExist.value = false
            }
        } catch (e:Exception){
            Log.e(TAG, "getCurrentData ${auth.uid}", e)
        }
    }
}
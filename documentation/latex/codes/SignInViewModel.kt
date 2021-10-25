class SignInViewModel : ViewModel() {
    private var auth = Firebase.auth
    private var isRememberChecked : MutableLiveData<Boolean> = MutableLiveData(false)
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    private var localDataModel : MutableLiveData<DatabaseModel> = MutableLiveData(DatabaseModel())
    var isUserSignIn : MutableLiveData<Boolean> = MutableLiveData(false)
    var signInError : MutableLiveData<String?> = MutableLiveData(null)

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    fun getDataModel() : DatabaseModel = localDataModel.value!!

    fun setRememberChecked(boolean: Boolean){
        isRememberChecked.value = boolean

    }

    fun signIn(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                localDataModel.value = DatabaseModel(
                    uid = auth.uid,
                    email = email,
                    remember = isRememberChecked.value!!
                )
                isUserSignIn.value = true
            } else {
                signInError.value = it.exception.toString()
                isUserSignIn.value = false
            }
        }
    }
}
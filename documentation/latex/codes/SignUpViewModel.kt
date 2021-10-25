class SignUpViewModel : ViewModel() {
    private var auth = Firebase.auth
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserSignUp : MutableLiveData<Boolean> = MutableLiveData(false)
    var signUpError : MutableLiveData<String?> = MutableLiveData(null)

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    fun signUp(email:String, password:String){
        signUpError.value = null
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                isUserSignUp.value = true
            } else {
                signUpError.value = it.exception.toString()
                isUserSignUp.value = false
            }
        }
    }
}
class ForgetPasswordViewModel : ViewModel() {
    private var auth = Firebase.auth
    private var isEmailNotEmpty : MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserForgetPassword : MutableLiveData<Boolean> = MutableLiveData(null)
    var forgetPasswordError : MutableLiveData<String> = MutableLiveData("")

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean>  {
        isEmailNotEmpty.value = boolean
        return isEmailNotEmpty
    }

    fun sendEmailReset(email:String) {
        forgetPasswordError.value = ""
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                isUserForgetPassword.value = true
            } else {
                forgetPasswordError.value = it.exception.toString()
                isUserForgetPassword.value = false
            }
        }
    }
}
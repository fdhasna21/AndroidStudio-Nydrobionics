class FeedbackViewModel : ViewModel(){
    private var auth : FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore.collection("feedbacks")
    private var ref : DocumentReference = db.document()
    private var feedbackModel : MutableLiveData<FeedbackModel> = MutableLiveData(FeedbackModel())
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isFeedbackSubmit : MutableLiveData<Boolean> = MutableLiveData(false)
    var feedbackSubmitError : MutableLiveData<String> = MutableLiveData("")

    companion object {
        const val TAG = "feedback"
    }

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    fun setRating(rating : Float) : MutableLiveData<FeedbackModel>? {
        feedbackModel.value?.rating = rating
        return feedbackModel
    }

    fun getRating() = feedbackModel.value?.rating

    fun submitFeedback(content:String){
        try{
            feedbackModel.value?.let {
                it.feedbackId = ref.id
                it.content = content
                it.userId = auth.uid
            }

            db.document(ref.id).set(feedbackModel.value!!.toHashMap()).addOnCompleteListener {
                if(it.isSuccessful){
                    isFeedbackSubmit.value = true
                } else {
                    feedbackSubmitError.value = it.exception.toString()
                    isFeedbackSubmit.value = false
                }
            }
        } catch (e:Exception){
            Log.e(TAG, "Error submit feedback", e)
        }
    }
}

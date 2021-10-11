package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.FeedbackModel
import com.fdhasna21.nydrobionics.dataclass.model.FeedbackModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

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

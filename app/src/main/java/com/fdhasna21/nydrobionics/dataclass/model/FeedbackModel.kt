package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedbackModel(
    var feedbackId : String? = null,
    var userId : String? = null,
    var content : String? = null,
    var rating : Float? = null,
    var timestamp: String? = null
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toFeedbackModel() : FeedbackModel? {
            return try {
                val feedbackId = getString("feedbackId")
                val userId = getString("userId")
                val content = getString("content")
                val rating : Double? = get("rating") as Double?
                val timestamp = getString("timestamp")
                val output = FeedbackModel(feedbackId, userId, content, rating?.toFloat(), timestamp)
//                Log.i(TAG, "$output")
                output
            } catch (e :Exception) {
                Log.e(TAG, "Error converting $TAG")
                null
            }
        }

        fun FeedbackModel.toHashMap() : HashMap<String, Any?>{
            val output : HashMap<String, Any?> = hashMapOf()
            output["feedbackId"] = feedbackId
            output["userId"] = userId
            output["content"] = content
            output["rating"] = rating
            output["timestamp"] = ViewUtility().getCurrentTimestamp()
            return output
        }

        private const val TAG = "FeedbackModel"
    }
}

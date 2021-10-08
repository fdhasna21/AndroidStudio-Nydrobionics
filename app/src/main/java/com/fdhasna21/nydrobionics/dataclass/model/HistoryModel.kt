package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryModel(
    var historyId : String? = null,
    var content : String? = null,
    var senderId : String? = null,
    var receiverId : String? = null,
    var date : String? = null,
    var time : String? = null
):Parcelable {
    companion object {
        fun DocumentSnapshot.toHistoryModel() : HistoryModel? {
            return try {
                val historyId =  getString("historyId")
                val content = getString("content")
                val senderId = getString("senderId")
                val receiverId = getString("receiverId")
                val date = getString("date")
                val time = getString("time")
                val output = HistoryModel(historyId, content, senderId, receiverId, date, time)
                Log.i(TAG, "$output")
                output
            } catch (e:Exception){
                Log.e(TAG, "Error converting $TAG")
                null
            }
        }

        fun HistoryModel.toHashMap() : HashMap<String, Any?>{
            val output : HashMap<String, Any?> = hashMapOf()
            output["historyId"] = historyId
            output["content"] = content
            output["senderId"] = senderId
            output["receiverId"] = receiverId
            output["date"] = date
            output["time"] = time
            return output
        }

        private const val TAG = "HistoryModel"
    }
}

package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteModel(
    var noteId : String? = null,
    var title: String? = null,
    var description : String? = null,
    var timestamp : String?=null,
    var date : String? = null,
    var time : String? = null
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toNoteModel() : NoteModel? {
            return try {
                val noteId = getString("noteId")
                val title = getString("title")
                val description = getString("description")
                val timestamp = getString("timestamp")
                val output = NoteModel(noteId, title, description, timestamp)
//                Log.i(TAG, "$output")
                output
            } catch (e : Exception){
                Log.e(TAG, "Error converting $TAG")
                null
            }
        }

        fun NoteModel.toHashMap() : HashMap<String, Any?> {
            val output : HashMap<String, Any?> = hashMapOf()
            output["noteId"] = noteId
            output["title"] = title
            output["description"] = description
            output["timestamp"] = ViewUtility().formatTimestampToString(date, time)
            return output
        }

        private const val TAG = "NoteModel"
    }
}

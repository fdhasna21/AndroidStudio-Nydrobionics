package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteModel(
    var noteId : String? = null,
    var title: String? = null,
    var description : String? = null,
    var date : String? = null,
    var time : String? = null
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toNoteModel() : NoteModel? {
            return try {
                val noteId = getString("noteId")
                val title = getString("title")
                val description = getString("description")
                val date = getString("date")
                val time = getString("time")
                val output = NoteModel(noteId, title, description, date, time)
                Log.i(TAG, "$output")
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
            output["date"] = date
            output["time"] = time
            return output
        }

        private const val TAG = "NoteModel"
    }
}

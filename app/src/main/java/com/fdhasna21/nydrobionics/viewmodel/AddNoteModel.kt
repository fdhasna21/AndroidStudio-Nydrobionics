package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.model.KitModel
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class AddNoteModel : ViewModel(){
    private var auth : FirebaseAuth = Firebase.auth
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var noteModel : MutableLiveData<NoteModel> = MutableLiveData(NoteModel())
    private var timeHour : MutableLiveData<Int> = MutableLiveData(0)
    private var timeMinute : MutableLiveData<Int> = MutableLiveData(0)
    private var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isNoteAdd: MutableLiveData<Boolean> = MutableLiveData(false)
    var addNoteError : MutableLiveData<String> = MutableLiveData("")

    companion object {
        const val TAG = "addNote"
    }

    fun setCurrentData(noteModel: NoteModel?){
        noteModel?.let{
            this.noteModel.value = it
        }
    }

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean> {
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    fun getDate(): Long {
        if(noteModel.value?.date == null){
            noteModel.value?.date = ViewUtility().getCurrentDate()
        }
        val c = Calendar.getInstance()
        val pos = ParsePosition(0)
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        c.time = sdf.parse(noteModel.value!!.date, pos)
        c.add(Calendar.DATE, 1)
        return c.timeInMillis
    }

    fun setDate(date: Long?): String? {
        noteModel.value?.date = ViewUtility().formatDate(date)
        return noteModel.value?.date
    }

    fun setTime(hour: Int, minute: Int) : String?{
        noteModel.value?.time = ViewUtility().formatTime(hour, minute)
        return noteModel.value?.time
    }

    fun getTime(){
        if(noteModel.value?.time == null){
            noteModel.value?.time = ViewUtility().getCurrentTime()
        }
        val c = Calendar.getInstance(TimeZone.getDefault())
        val pos = ParsePosition(0)
        val sdf = SimpleDateFormat("HH:mm", Locale.US)
        c.time = sdf.parse(noteModel.value!!.time, pos)
        timeHour.value = c.get(Calendar.HOUR_OF_DAY)
        timeMinute.value = c.get(Calendar.MINUTE)
    }

    fun getTimeHour() : Int {
        timeHour.value?.let {
            return it
        }
        return 0
    }

    fun getTimeMinute() : Int {
        timeMinute.value?.let {
            return it
        }
        return 0
    }

    fun createNote(title:String, description:String){
        val db = firestore.collection("users").document(auth.uid!!).collection("notes")
        val ref : DocumentReference = db.document()

        try {
            noteModel.value?.apply{
                this.noteId = noteId ?: ref.id
                this.title = title
                this.description = description
            }

            db.document(ref.id).set(noteModel.value!!.toHashMap()).addOnCompleteListener {
                if(it.isSuccessful){
                    isNoteAdd.value = true
                } else {
                    addNoteError.value = it.exception.toString()
                    isNoteAdd.value = false
                }
            }

        } catch (e:Exception){
            Log.e(TAG, "Error submit note", e)
        }
    }
}

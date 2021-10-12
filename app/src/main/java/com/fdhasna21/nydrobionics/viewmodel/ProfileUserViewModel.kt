package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel.Companion.toNoteModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileUserViewModel : ViewModel(){
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var userModel : MutableLiveData<UserModel> = MutableLiveData(null)
    private var noteModels : MutableLiveData<ArrayList<NoteModel>> = MutableLiveData(null)

    companion object {
        const val TAG = "profileUserViewModel"
    }

    fun setUserModel(userModel: UserModel?){
        this.userModel.value = userModel
        try {
            this.userModel.value?.let { user ->
                firestore.collection("users").document(user.uid!!)
                    .collection("notes").get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val notes : ArrayList<NoteModel> = arrayListOf()
                            for(note in it.result.documents) {
                                note.toNoteModel()?.let {
                                    notes.add(it)
                                }
                            }
                            noteModels.value = notes
                        }
                    }
            }
        } catch (e:Exception){
            Log.e(TAG, "setUserModel: ", )
        }
    }

    fun getUserModel() : MutableLiveData<UserModel> = userModel
}

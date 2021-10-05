package com.fdhasna21.nydrobionics.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference

//Real Time Database
class FirebaseMutableLiveData(val databaseReference: DatabaseReference) : MutableLiveData<DataSnapshot?>() {
    private lateinit var listener : ValueEventListener


    private inner class MyValueEventListener : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            value = snapshot
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("FirebaseMutableLiveData", "Can't listen to query $databaseReference", error.toException())
        }

    }

    fun setListener(valueEventListener: ValueEventListener){
        this.listener = valueEventListener
    }

    fun addListener() {
        databaseReference.addValueEventListener(listener)
    }

    fun removeListener(){
        databaseReference.removeEventListener(listener)
    }
}
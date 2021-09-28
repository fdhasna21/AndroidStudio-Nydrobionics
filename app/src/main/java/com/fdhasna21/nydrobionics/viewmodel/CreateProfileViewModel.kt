package com.fdhasna21.nydrobionics.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.Farm
import com.fdhasna21.nydrobionics.dataclass.User
import com.fdhasna21.nydrobionics.enumclass.Gender
import com.fdhasna21.nydrobionics.enumclass.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class CreateProfileViewModel : ViewModel() {
    private val today = formatDate(Calendar.getInstance(TimeZone.getDefault()).timeInMillis)

    private var currentUser : FirebaseUser? = Firebase.auth.currentUser
    private var userEmail : MutableLiveData<String> = MutableLiveData()
    private var user : MutableLiveData<User> = MutableLiveData(
        User(gender = Gender.MALE.toString(),
             dob = today))
    private var farm : MutableLiveData<Farm> = MutableLiveData(
        Farm(

        ))

    var isUserCreated : MutableLiveData<Boolean> = MutableLiveData(false)
    var isFarmCreated : MutableLiveData<Boolean> = MutableLiveData(false)

    var createProfileError : MutableLiveData<String> = MutableLiveData("")


    init {
        userEmail.value = currentUser?.email
    }

    private fun formatDate(date: Long?) : String?{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        return sdf.format(date)
    }


    /** CREATE USER FRAGMENT **/
    fun setRole(role:Role){
        user.value?.role = role.toString()
        Log.i("createProfile", "setRole: ${user.value?.role}")
    }

    fun setGender(gender: Gender){
        user.value?.gender = gender.toString()
        Log.i("createProfile", "setGender: ${user.value?.gender}")
    }

    fun setDOB(date:Long?) : String? {
        user.value?.dob = formatDate(date)
        return user.value?.dob
    }

    fun getDOB() : Long?{
        return if(user.value?.dob == null){
            null
        } else {
            val c = Calendar.getInstance()
            val pos = ParsePosition(0)
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.US)
            c.time = sdf.parse(user.value!!.dob, pos)
            c.add(Calendar.DATE, 1)
            c.timeInMillis
        }
    }

    fun getEmail() : MutableLiveData<String>{
        return userEmail
    }

    fun createUserProfile(name:String, phone:String, address:String, bio:String) {
        user.value?.apply {
            this.name = name
            this.phone = phone
            this.address = address
            this.bio = bio
            this.uid = currentUser?.uid!!
            this.joinedSince = today
            this.performanceRate = 5.0f
        }

        createProfileError.value = ""
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(currentUser?.uid!!).set(user).addOnCompleteListener {
            if(it.isSuccessful){
                isUserCreated.value = true
            } else {
                createProfileError.value = it.exception.toString()
                isUserCreated.value = false
            }
        }
    }


    /** CREATE FARM FRAGMENT **/
    fun createFarmProfile(name:String, description:String, location:String){
        if(user.value?.role!! == Role.OWNER.toString()){
            createProfileError.value = ""
            val db = FirebaseFirestore.getInstance().collection("farms")
            val ref : DocumentReference = db.document()

            farm.value?.apply {
                //gps sama photo masih null
                this.ownerId = currentUser?.uid!!
                this.farmId = ref.id
                this.name = name
                this.description = description
                this.location = location
            }

            db.document(ref.id).set(farm).addOnCompleteListener {
                if(it.isSuccessful){
                    isFarmCreated.value = true
                } else {
                    createProfileError.value = it.exception.toString()
                    isFarmCreated.value = false
                }
            }
        }
    }
}
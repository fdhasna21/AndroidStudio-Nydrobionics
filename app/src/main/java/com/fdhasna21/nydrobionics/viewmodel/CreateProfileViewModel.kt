package com.fdhasna21.nydrobionics.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.UriFileExtensions
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.enumclass.Gender
import com.fdhasna21.nydrobionics.enumclass.Role
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class CreateProfileViewModel : ViewModel() {
    private val today = formatDate(Calendar.getInstance(TimeZone.getDefault()).timeInMillis)
    private var currentUser : FirebaseUser? = Firebase.auth.currentUser
    private var storage : FirebaseStorage = Firebase.storage
    private var userEmail : MutableLiveData<String> = MutableLiveData()
    private var userImageUri : MutableLiveData<UriFileExtensions> = MutableLiveData(null)

    private var userModel : MutableLiveData<UserModel> = MutableLiveData(
        UserModel(gender = Gender.MALE.toString(),
             dob = today)
    )
    private var farmModel : MutableLiveData<FarmModel> = MutableLiveData(
        FarmModel(

        )
    )

    var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
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

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean>{
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    /** CREATE USER FRAGMENT **/
    fun setPhotoProfile(uri : Uri?, fileExtension: String?=null){
        userImageUri.value = if(uri != null){
             UriFileExtensions(uri, fileExtension!!)
        } else {
            null
        }

        Log.i("createProfile", "setPhotoProfile: $uri")
    }

    fun getPhotoProfile() : MutableLiveData<UriFileExtensions>{
        return userImageUri
    }

    fun setRole(role:Role){
        userModel.value?.role = role.toString()
    }

    fun setGender(gender: Gender){
        userModel.value?.gender = gender.toString()
    }

    fun getGenderPosition() : Int {
        return Gender.getType(userModel.value?.gender!!)?.getPosition()!!
    }

    fun setDOB(date:Long?) : String? {
        userModel.value?.dob = formatDate(date)
        return userModel.value?.dob
    }

    fun getDOB() : Long?{
        return if(userModel.value?.dob == null){
            null
        } else {
            val c = Calendar.getInstance()
            val pos = ParsePosition(0)
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.US)
            c.time = sdf.parse(userModel.value!!.dob, pos)
            c.add(Calendar.DATE, 1)
            c.timeInMillis
        }
    }

    fun getEmail() : MutableLiveData<String>{
        return userEmail
    }

    fun getUserModel() : UserModel?{
        return userModel.value
    }

    fun setUserModel(userModel: UserModel){
        this.userModel.value = userModel
    }

    fun createUserProfile(name:String, phone:String, address:String, bio:String) {
        userModel.value?.apply {
            this.name = name
            this.phone = phone
            this.address = address
            this.bio = bio
            this.uid = currentUser?.uid!!
            this.joinedSince = today
            this.performanceRate = 5.0f
        }

        if(userImageUri.value != null){
            val storageReference : StorageReference = storage.getReference("profile_images")
                .child(System.currentTimeMillis().toString() + ".${userImageUri.value?.fileExtensions!!}")
            val uploadTask = storageReference.putFile(userImageUri.value?.uri!!)
            uploadTask.continueWithTask {
                if(!it.isSuccessful){
                    throw it.exception!!.cause!!
                }
                storageReference.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.let {
                        userModel.value?.photo_url = it.toString()
                        sendUserProfile()
                    }
                }
            }
        } else {
            sendUserProfile()
        }

    }

    private fun sendUserProfile(){
        createProfileError.value = ""
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(currentUser?.uid!!).set(userModel.value!!.toHashMap()).addOnCompleteListener {
            if(it.isSuccessful){
                isUserCreated.value = true
                isNotEmpties.value = false
            } else {
                createProfileError.value = it.exception.toString()
                isUserCreated.value = false
            }
        }
    }


    /** CREATE FARM FRAGMENT **/
    fun createFarmProfile(name:String, description:String, location:String){
        if(userModel.value?.role!! == Role.OWNER.toString()){
            createProfileError.value = ""
            val db = FirebaseFirestore.getInstance().collection("farms")
            val ref : DocumentReference = db.document()

            farmModel.value?.apply {
                //photo masih null
                this.ownerId = currentUser?.uid!!
                this.farmId = ref.id
                this.name = name
                this.description = description
                this.location = location
            }

            db.document(ref.id).set(farmModel.value!!.toHashMap()).addOnCompleteListener {
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
package com.fdhasna21.nydrobionics.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.UriFileExtensions
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel.Companion.toHashMap
import com.fdhasna21.nydrobionics.dataclass.model.UserModel.Companion.toUserModel
import com.fdhasna21.nydrobionics.enumclass.Gender
import com.fdhasna21.nydrobionics.enumclass.Role
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateProfileViewModel : ViewModel() {
    private val today = ViewUtility().getCurrentDate()
    private var auth : FirebaseAuth = Firebase.auth
    private var storage : FirebaseStorage = Firebase.storage
    private var firestore : FirebaseFirestore = Firebase.firestore
    private var userImageUri : MutableLiveData<UriFileExtensions> = MutableLiveData(null)

    private var userModel : MutableLiveData<UserModel> = MutableLiveData(
        UserModel(gender = Gender.MALE.toString(),
             dob = today)
    )
    private var farmModel : MutableLiveData<FarmModel> = MutableLiveData(FarmModel())
    private var updateStaff : MutableLiveData<ArrayList<UserModel>> = MutableLiveData(arrayListOf())
    private var currentStaff : MutableLiveData<ArrayList<UserModel>> = MutableLiveData(null)

    var isNotEmpties : MutableLiveData<Boolean> = MutableLiveData(false)
    var isUserCreated : MutableLiveData<Boolean> = MutableLiveData(false)
    var isFarmCreated : MutableLiveData<Boolean> = MutableLiveData(false)
    var isStaffAdded : MutableLiveData<Boolean> = MutableLiveData(false)
    var createProfileError : MutableLiveData<String> = MutableLiveData("")

    private var updateUser : MutableLiveData<UserModel> = MutableLiveData(null)

    companion object{
        const val TAG = "createProfileViewModel"
    }

    fun checkNotEmpty(boolean: Boolean) : MutableLiveData<Boolean>{
        isNotEmpties.value = boolean
        return isNotEmpties
    }

    /** SET MODEL **/
    fun setCurrentUser(userModel: UserModel?){
        this.userModel.value = userModel
        isUserCreated.value = true
        updateUserGender.value  = Gender.getType(userModel?.gender!!)?.getPosition()!!
    }

    fun setCurrentFarm(farmModel : FarmModel?){
        this.farmModel.value = farmModel
        isFarmCreated.value = true
    }

    fun getCurrentUserModel() : UserModel? = userModel.value

    fun getCurrentFarmModel() : FarmModel? = farmModel.value


    /** CREATE USER FRAGMENT **/
    fun setPhotoProfile(uri : Uri?, fileExtension: String?=null){
        userImageUri.value = if(uri != null){
             UriFileExtensions(uri, fileExtension!!)
        } else {
            null
        }
        Log.i(TAG, "setPhotoProfile: $uri")
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
        userModel.value?.dob = ViewUtility().formatDateToString(date)
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

    fun createUserProfile(name:String, phone:String, address:String, bio:String) {
        try {
            userModel.value?.apply {
                this.name = name
                this.email = auth.currentUser?.email
                this.phone = phone
                this.address = address
                this.bio = bio
                this.uid = auth.uid
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
        } catch (e:Exception){
            Log.e(TAG, "Error submit user", e)
        }
    }

    private fun sendUserProfile(){
        createProfileError.value = ""
        firestore.collection("users").document(auth.uid!!).set(userModel.value!!.toHashMap()).addOnCompleteListener {
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
        try {
            createProfileError.value = ""
            val db = firestore.collection("farms")
            val ref : DocumentReference = db.document()

            farmModel.value?.apply {
                this.farmId = ref.id
                this.name = name
                this.description = description
                this.location = location
            }

            db.document(ref.id).set(farmModel.value!!.toHashMap()).addOnCompleteListener {
                if(it.isSuccessful){
                    userModel.value?.farmId = ref.id
                    sendUserProfile()
                    if(isUserCreated.value == true){
                        isFarmCreated.value = true
                    } else {
                        createProfileError.value = it.exception.toString()
                        isFarmCreated.value = false
                    }
                } else {
                    createProfileError.value = it.exception.toString()
                    isFarmCreated.value = false
                }
            }
        } catch (e:Exception){
            Log.e(TAG, "Error submit farm", e)
        }
    }


    /** EDIT FARM **/
    fun getCurrentStaff() : MutableLiveData<ArrayList<UserModel>>  = currentStaff

    fun getStaff() : MutableLiveData<ArrayList<UserModel>> = updateStaff

    fun addStaff(userModel : UserModel){
        updateStaff.value?.let {
            val array : ArrayList<UserModel> = it
            array.add(userModel)
            updateStaff.value = array
            Log.i(TAG, "addStaff: ${updateStaff.value}")
        }
    }

    fun removeStaff(position : Int){
        updateStaff.value?.let {
            val array : ArrayList<UserModel> = it
            array.removeAt(position)
            updateStaff.value = array
            Log.i(TAG, "removeStaff: ${updateStaff.value}")
        }
    }

    fun createStaff(){
        try {
            currentStaff.value?.let {
                for (staff in it) {
                    firestore.collection("users").document(staff.uid!!)
                        .update("farmId", null)
                }
            }

            updateStaff.value?.let {
                for (staff in it) {
                    firestore.collection("users").document(staff.uid!!)
                        .update("farmId", farmModel.value?.farmId)
                }
                isStaffAdded.value = true
            }
        } catch (e:Exception){
            Log.e(TAG, "createStaff: ", e)
        }
    }

    fun updateStaff() {
        try {
            firestore.collection("users")
                .get().addOnCompleteListener {
                    if(it.isSuccessful) {
                        val staffs: ArrayList<UserModel> = arrayListOf()
                        for (staffDocument in it.result.documents) {
                            staffDocument?.let { staff ->
                                staff.toUserModel()?.let { staffModel ->
                                    staffModel.farmId?.let {
                                        if (it == farmModel.value?.farmId) {
                                            staffs.add(staffModel)
                                        }
                                    }
                                }
                            }
                        }
                        currentStaff.value = staffs
                        updateStaff.value = staffs
                    }
                }
        } catch (e:Exception){
            Log.e(TAG, "updateCurrentStaff for farm ${farmModel.value?.farmId}: ", e)
        }
    }

    /** EDIT USER **/
    private var updateUserGender : MutableLiveData<Int> = MutableLiveData(0)
    fun getUpdateGender() : Int? = updateUserGender.value
}
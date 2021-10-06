package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var name : String? = null,
    var gender : String? = null,
    var dob : String? = null,
    var role : String? = null,
    var bio : String? = null,
    var uid : String? = null,
    var performanceRate : Float? = null,
    var address : String? = null,
    var phone : String? = null,
    var joinedSince : String? = null,
    var photo_url : String? = null
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toUser() : UserModel? {
            try{
                Log.i(TAG, "toUser: ${contains("name")}")
                val name = getString("name")
                val gender = getString("gender")
                val dob = getString("dob")
                val role = getString("role")
                val bio = getString("bio")
                val uid = getString("uid")
                val performanceRate : Double? = get("performanceRate") as Double?
                val address = getString("address")
                val phone = getString("phone")
                val joinedSince = getString("joinedSince")
                val output = UserModel(name, gender, dob, role, bio, uid, performanceRate?.toFloat(), address, phone, joinedSince)
                Log.i(TAG, "$output")
                return output
            } catch (e: Exception) {
                Log.e(TAG, "Error converting $TAG", e)
                return null
            }
        }

        fun UserModel.toHashMap():HashMap<String,Any?>{
            val output = hashMapOf<String,Any?>()
            output["name"] = name
            output["gender"] = gender
            output["dob"] = dob
            output["role"] = role
            output["bio"] = bio
            output["uid"] = uid
            output["performanceRate"] = performanceRate
            output["address"] = address
            output["phone"] = phone
            output["joinedSince"] = joinedSince
            output["photo_url"] = photo_url
            return output
        }

        private const val TAG = "UserModel"

    }
}


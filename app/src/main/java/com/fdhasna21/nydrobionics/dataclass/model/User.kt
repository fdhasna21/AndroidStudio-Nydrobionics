package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
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
        fun DocumentSnapshot.toUser() : User? {
            try{
                val name = getString("name")!!
                val gender = getString("gender")!!
                val dob = getString("dob")!!
                val role = getString("role")!!
                val bio = getString("bio")!!
                val uid = getString("uid")!!
                val performanceRate = get("performanceRate")!! as Float
                val address = getString("address")!!
                val phone = getString("phone")!!
                val joinedSince = getString("joinedSince")!!
                return User(name, gender, dob, role, bio, uid, performanceRate, address, phone, joinedSince)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting $TAG profile", e)
//                FirebaseCrashlytics.getInstance().log("Error converting $TAG profile")
//                FirebaseCrashlytics.getInstance().setCustomKey("uid", id)
//                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
        private const val TAG = "User"
    }
}

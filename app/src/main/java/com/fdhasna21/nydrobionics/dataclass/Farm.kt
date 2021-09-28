package com.fdhasna21.nydrobionics.dataclass

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class Farm(
    var farmId : String? = null,
    var name : String? = null,
    var description : String? = null,
    var location : String? = null,
    var gps : String? = null,
    var ownerId : String? = null,
    var photo : String? = null
) : Parcelable {
    companion object{
        fun DocumentSnapshot.toFarm() : Farm? {
            try {
                val id  = getString("id")!!
                val name = getString("name")!!
                val description = getString("description")!!
                val location = getString("location")!!
                val gps = getString("gps")!!
                val ownerId = getString("ownerId")!!
                val photo = getString("photo")!!
                return Farm(id, name, description, location, gps, ownerId, photo)

            }
            catch (e: Exception) {
                Log.e(TAG, "Error converting $TAG profile", e)
//                FirebaseCrashlytics.getInstance().log("Error converting $TAG profile")
//                FirebaseCrashlytics.getInstance().setCustomKey("farmId", id)
//                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
        private const val TAG = "Farm"
    }
}

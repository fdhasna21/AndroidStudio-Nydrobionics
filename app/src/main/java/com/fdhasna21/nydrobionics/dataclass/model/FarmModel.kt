package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class FarmModel(
    var farmId : String? = null,
    var name : String? = null,
    var description : String? = null,
    var location : String? = null,
    var gps : String? = null,
    var ownerId : String? = null,
) : Parcelable {
    companion object{
        fun DocumentSnapshot.toFarm() : FarmModel? {
            try {
                val id  = getString("id")
                val name = getString("name")
                val description = getString("description")
                val location = getString("location")
                val gps = getString("gps")
                val ownerId = getString("ownerId")
                val output = FarmModel(id, name, description, location, gps, ownerId)
                Log.i(TAG, "$output")
                return output
            }
            catch (e: Exception) {
                Log.e(TAG, "Error converting $TAG profile", e)
                return null
            }
        }

        fun FarmModel.toHashMap() : HashMap<String, Any?>{
            val output = hashMapOf<String, Any?>()
            output["farmId"] = farmId
            output["name"] = name
            output["description"] = description
            output["location"] = location
            output["gps"] = gps
            output["ownerId"] = ownerId
            return output
        }

        private const val TAG = "FarmModel"
    }
}

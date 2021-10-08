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
    var gps : String? = null
) : Parcelable {
    companion object{
        fun DocumentSnapshot.toFarmModel() : FarmModel? {
            try {
                val name = getString("name")
                val description = getString("description")
                val location = getString("location")
                val gps = getString("gps")
                val farmId = getString("farmId")
                val output = FarmModel(farmId, name, description, location, gps)
                Log.i(TAG, "$output")
                return output
            }
            catch (e: Exception) {
                Log.e(TAG, "Error converting $TAG", e)
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
            return output
        }

        private const val TAG = "FarmModel"
    }
}

package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class FarmModel(
    var farmId : String? = null,
    var name : String? = null,
    var description : String? = null,
    var location : String? = null,
    var gps : String? = null,
    var timestamp : String? = null,
    var kitModels : ArrayList<KitModel>? = null
) : Parcelable {
    companion object{
        fun DocumentSnapshot.toFarmModel() : FarmModel? {
            try {
                val name = getString("name")
                val description = getString("description")
                val location = getString("location")
                val gps = getString("gps")
                val farmId = getString("farmId")
                val timestamp = getString("timestamp")
                val output = FarmModel(farmId, name, description, location, gps, timestamp, arrayListOf())
//                Log.i(TAG, "$output")
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
            output["timestamp"] = ViewUtility().getCurrentTimestamp()
            return output
        }

        fun FarmModel.replace(input : FarmModel?){
            this.farmId = input?.farmId
            this.name = input?.name
            this.description = input?.description
            this.location = input?.location
            this.gps = input?.gps
            this.timestamp = input?.timestamp
            this.kitModels = arrayListOf<KitModel>()
        }

        private const val TAG = "FarmModel"
    }
}

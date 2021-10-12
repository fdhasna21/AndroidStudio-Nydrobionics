package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel.Companion.getLevelModel
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel.Companion.toHashMap
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.lang.Exception

@Parcelize
data class PlantModel(
    var plantId : String? = null,
    var name : String? = null,
    var description : String? = null,
    var photo_url : String? = null,
    var tempLv : @RawValue ScoreLevel? = null,
    var humidLv : @RawValue ScoreLevel? = null,
    var phLv : @RawValue ScoreLevel? = null,
    var userId : String? = null,
    var growthTime : Int? = 0,
    var timestamp : String? = null
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toPlantModel() : PlantModel? {
            return try {
                val plantId = getString("plantId")
                val name = getString("name")
                val description = getString("description")
                val photo_url = getString("photo_url")
                val tempLv : HashMap<*, *>? = get("tempLv") as HashMap<*, *>?
                val humidLv : HashMap<*, *>? = get("humidLv") as HashMap<*, *>?
                val phLv : HashMap<*, *>? = get("phLv") as HashMap<*, *>?
                val userId = getString("userId")
                val growthTime : Long? = get("growthTime") as Long?
                val timestamp = getString("timestamp")
                val output = PlantModel(plantId, name, description, photo_url,
                    getLevelModel(tempLv.toString()),
                    getLevelModel(humidLv.toString()),
                    getLevelModel(phLv.toString()),
                    userId, growthTime?.toInt(),
                    timestamp)
                Log.i(TAG, "$output")
                output
            } catch (e : Exception){
                Log.e(TAG, "Error converting $TAG", e)
                null
            }
        }

        fun PlantModel.toHashMap() : HashMap<String, Any?>{
            val output : HashMap<String, Any?> = hashMapOf()
            output["plantId"] = plantId
            output["name"] = name
            output["description"] = description
            output["photo_url"] = photo_url
            output["tempLv"] = tempLv?.toHashMap()
            output["humidLv"] = humidLv?.toHashMap()
            output["phLv"] = phLv?.toHashMap()
            output["userId"] = userId
            output["growthTime"] = growthTime
            output["timestamp"] = ViewUtility().getCurrentTimestamp()
            return output
        }

        fun PlantModel.replace(input : PlantModel?){
            this.plantId = input?.plantId
            this.description = input?.description
            this.growthTime = input?.growthTime
            this.humidLv = input?.humidLv
            this.name = input?.name
            this.phLv = input?.phLv
            this.userId = input?.userId
            this.timestamp = input?.timestamp
            this.photo_url = input?.photo_url
            this.tempLv = input?.tempLv
        }

        private const val TAG = "PlantModel"
    }
}

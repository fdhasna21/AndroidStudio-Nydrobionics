package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel.Companion.getLevelModel
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel.Companion.toHashMap
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CropsModel(
    var cropsId : String? = null,
    var plantId : String? = null,
    var userId : String? = null,
    var tempLv : @RawValue ScoreLevel? = null,
    var humidLv : @RawValue ScoreLevel? = null,
    var phLv : @RawValue ScoreLevel? = null,
    var timestamp : String? = null,
    var plantModel: PlantModel? = null
) : Parcelable {
    companion object{
        fun DocumentSnapshot.toCropsModel() : CropsModel?{
            return try {
                val cropsId = getString("cropsId")
                val plantId = getString("plantId")
                val userId = getString("userId")
                val tempLv : HashMap<*, *>? = get("tempLv") as HashMap<*, *>?
                val humidLv : HashMap<*, *>? = get("humidLv") as HashMap<*, *>?
                val phLv : HashMap<*, *>? = get("phLv") as HashMap<*, *>?
                val timestamp = getString("timestamp")
                val output = CropsModel(cropsId, plantId, userId,
                    getLevelModel(tempLv.toString()),
                    getLevelModel(humidLv.toString()),
                    getLevelModel(phLv.toString()),
                    timestamp)//, PlantModel())
                Log.i(TAG, "$output")
                output
            }catch (e:Exception){
                Log.e(TAG, "Error converting $TAG profile", e)
                null
            }
        }

        fun CropsModel.toHashMap() : HashMap<String, Any?>{
            val output : HashMap<String, Any?> = hashMapOf()
            output["cropsId"] = cropsId
            output["plantId"] = plantId
            output["userId"] = userId
            output["tempLv"] = tempLv?.toHashMap()
            output["humidLv"] = humidLv?.toHashMap()
            output["phLv"] = phLv?.toHashMap()
            output["timestamp"] = ViewUtility().getCurrentTimestamp()
            return output
        }

        fun CropsModel.replace(input:CropsModel?){
            this.plantId = input?.plantId
            this.cropsId = input?.cropsId
            this.humidLv = input?.humidLv
            this.tempLv = input?.tempLv
            this.phLv = input?.phLv
            this.userId = input?.userId
            this.timestamp = input?.timestamp
            this.plantModel = PlantModel()
        }

        private const val TAG = "CropsModel"
    }
}

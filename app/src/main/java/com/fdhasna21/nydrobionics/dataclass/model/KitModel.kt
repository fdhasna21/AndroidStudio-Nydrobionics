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

@Parcelize
data class KitModel(
    var kitId : String? = null,
    var name : String?= null,
    var position : String? = null,
    var length : Int? = null,
    var width : Int? = null,
    var waterLv : @RawValue ScoreLevel? = null,
    var nutrientLv : @RawValue ScoreLevel? = null,
    var turbidityLv: @RawValue ScoreLevel? = null,
    var isPlanted : Boolean? = false,
    var timestamp : String? = null,
    var lastMonitoring : DataMonitoringModel? = null,
    var lastCrops : CropsModel? = null
)  : Parcelable {
    companion object{
        fun DocumentSnapshot.toKitModel() : KitModel?{
            return try {
                val kitId = getString("kitId")
                val name = getString("name")
                val position = getString("position")
                val length : Long? = get("length") as Long?
                val width : Long? = get("width") as Long?
                val waterLv : HashMap<*, *>? = get("waterLv") as HashMap<*, *>?
                val nutrientLv : HashMap<*, *>? = get("nutrientLv") as HashMap<*, *>?
                val turbidityLv : HashMap<*, *>?= get("turbidityLv") as HashMap<*, *>?
                val isPlanted = getBoolean("isPlanted")
                val timestamp = getString("timestamp")
                val output = KitModel(kitId, name, position,
                    length?.toInt(), width?.toInt(),
                    getLevelModel(waterLv.toString()),
                    getLevelModel(nutrientLv.toString()),
                    getLevelModel(turbidityLv.toString()),
                    isPlanted, timestamp, null, null)
//                Log.i(TAG, "$output")
                output
            } catch (e: Exception) {
                Log.e(TAG, "Error converting $TAG", e)
                null
            }
        }

        fun KitModel.toHashMap() : HashMap<String, Any?>{
            val output = hashMapOf<String, Any?>()
            output["kitId"] = kitId
            output["name"] = name
            output["position"] = position
            output["length"] = length
            output["width"] = width
            output["waterLv"] = waterLv?.toHashMap()
            output["nutrientLv"] = nutrientLv?.toHashMap()
            output["turbidityLv"] = turbidityLv?.toHashMap()
            output["isPlanted"] = isPlanted
            output["timestamp"] = ViewUtility().getCurrentTimestamp()
            return output
        }

        private const val TAG = "KitModel"
    }
}
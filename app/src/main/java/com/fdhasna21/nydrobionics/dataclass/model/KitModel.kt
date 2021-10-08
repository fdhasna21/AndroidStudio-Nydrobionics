package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel.Companion.getLevelModel
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel.Companion.toHashMap
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
    var turbidityLv: @RawValue ScoreLevel? = null
)  : Parcelable {
    companion object{
        fun DocumentSnapshot.toKitModel() : KitModel?{
            return try {
                val kitId = getString("kitId")
                val name = getString("name")
                val position = getString("position")
                val length : Int? = get("length") as Int?
                val width : Int? = get("width") as Int?
                val waterLv = getString("waterLv")
                val nutrientLv = getString("nutrientLv")
                val turbidityLv = getString("turbidityLv")
                val output = KitModel(kitId, name, position, length, width, getLevelModel(waterLv), getLevelModel(nutrientLv), getLevelModel(turbidityLv))
                Log.i(TAG, "$output")
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
            return output
        }
        private const val TAG = "KitModel"
    }
}
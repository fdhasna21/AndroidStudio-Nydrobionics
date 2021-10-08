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
data class CropsModel(
    var cropsId : String? = null,
    var plantId : String? = null,
    var userId : String? = null,
    var tempLv : @RawValue ScoreLevel? = null,
    var humidLv : @RawValue ScoreLevel? = null,
    var phLv : @RawValue ScoreLevel? = null
) : Parcelable {
    companion object{
        fun DocumentSnapshot.toCropsModel() : CropsModel?{
            return try {
                val cropsId = getString("cropsId")
                val plantId = getString("plantId")
                val userId = getString("userId")
                val tempLv = getString("tempLv")
                val humidLv = getString("humidLv")
                val phLv = getString("phLv")
                val output = CropsModel(cropsId, plantId, userId,
                    getLevelModel(tempLv),
                    getLevelModel(humidLv),
                    getLevelModel(phLv))
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
            return output
        }

        private const val TAG = "CropsModel"
    }
}

package com.fdhasna21.nydrobionics.dataclass

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.lang.Exception
import kotlin.math.round

@Parcelize
data class ScoreLevel(
    var min : Float? = 0f,
    var max : Float? = 0f,
    var score : Float? = 0f
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toLevelModel() : ScoreLevel? {
            return try {
                val min : Double? = get("min") as Double?
                val max : Double? = get("max") as Double?
                ScoreLevel(min?.toFloat(), max?.toFloat())
            } catch (e:Exception){
                Log.e(TAG, "Error converting $TAG", e)
                null
            }
        }

        fun ScoreLevel.toHashMap():HashMap<String,Any?>{
            val output = hashMapOf<String, Any?>()
            output["min"] = min.round()
            output["max"] = max.round()
            return output
        }

        private fun Float?.round(): Float? {
            this?.let {
                var multiplier = 1.0
                repeat(2) { multiplier *= 10 }
                return (round(this * multiplier) / multiplier).toFloat()
            }
            return null
        }

        fun getLevelModel(string: String?) : ScoreLevel? {
            return try {
                val output = Gson().fromJson<ScoreLevel>(string, ScoreLevel::class.java)
                ScoreLevel(output.min, output.max)
            } catch (e:Exception){
                Log.e(TAG, "Error converting $TAG", e)
                null
            }
        }

        private const val TAG = "ScoreLevel"
    }
}

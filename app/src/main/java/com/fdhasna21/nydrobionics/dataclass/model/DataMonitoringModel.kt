package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataMonitoringModel(
    var dataId : String? = null,
    var temperature : Float? = 0f,
    var humidity : Float? = 0f,
    var waterTank : Float? = 0f,
    var nutrientTank : Float? = 0f,
    var turbidity : Float? =0f,
    var ph : Float? = 0f,
    var userId : String? = null,
    var cropsId : String? = null,
    var timestamp : String? = null
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toDataMonitoringModel() : DataMonitoringModel?{
            return try {
                val dataId = getString("dataId")
                val temperature : Double? = get("temperature") as Double?
                val humidity : Double? = get("humidity") as Double?
                val waterTank : Double? = get("waterTank") as Double?
                val nutrientTank : Double? = get("nutrientTank") as Double?
                val turbidity : Double? = get("turbidity") as Double?
                val ph : Double? = get("ph") as Double?
                val userId = getString("userId")
                val cropsId = getString("cropsId")
                val timestamp = getString("timestamp")
                val output = DataMonitoringModel(dataId,
                    temperature?.toFloat(),
                    humidity?.toFloat(),
                    waterTank?.toFloat(),
                    nutrientTank?.toFloat(),
                    turbidity?.toFloat(),
                    ph?.toFloat(),
                    userId, cropsId, timestamp)
//                Log.i(TAG, "$output")
                output
            } catch (e:Exception){
                Log.e(TAG, "Error converting $TAG", e)
                null
            }
        }

        fun DataMonitoringModel.toHashMap() : HashMap<String, Any?>{
            val output : HashMap<String, Any?> = hashMapOf()
            output["dataId"] = dataId
            output["temperature"] = temperature.round()
            output["humidity"] = humidity.round()
            output["waterTank"] = waterTank.round()
            output["nutrientTank"] = nutrientTank.round()
            output["turbidity"] = turbidity.round()
            output["ph"] = ph.round()
            output["userId"] = userId
            output["cropsId"] = cropsId
            output["timestamp"] = ViewUtility().getCurrentTimestamp()
            return output
        }

        fun Float?.round(): Float? {
            this?.let {
                var multiplier = 1.0
                repeat(2) { multiplier *= 10 }
                return (kotlin.math.round(this * multiplier) / multiplier).toFloat()
            }
            return null
        }

        fun DataMonitoringModel.replace(input:DataMonitoringModel?) {
            try {
                this.dataId = input?.dataId
                this.temperature = input?.temperature
                this.humidity = input?.humidity
                this.waterTank = input?.waterTank
                this.nutrientTank = input?.nutrientTank
                this.turbidity = input?.turbidity
                this.ph = input?.ph
                this.userId = input?.userId
                this.cropsId = input?.cropsId
                this.timestamp = input?.timestamp
            } catch (e: Exception) {
                Log.e(TAG, "Error converting $TAG", e)
            }
        }
        private const val TAG = "DataMonitoringModel"
    }
}

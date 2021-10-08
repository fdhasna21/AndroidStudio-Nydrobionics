package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
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
    var date : String? = null,
    var time : String? = null
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
                val date = getString("date")
                val time = getString("time")
                val output = DataMonitoringModel(dataId,
                    temperature?.toFloat(),
                    humidity?.toFloat(),
                    waterTank?.toFloat(),
                    nutrientTank?.toFloat(),
                    turbidity?.toFloat(),
                    ph?.toFloat(),
                    userId, cropsId, date, time)
                Log.i(TAG, "$output")
                output
            } catch (e:Exception){
                Log.e(TAG, "Error converting $TAG", e)
                null
            }
        }

        fun DataMonitoringModel.toHashMap() : HashMap<String, Any?>{
            val output : HashMap<String, Any?> = hashMapOf()
            output["dataId"] = dataId
            output["temperature"] = temperature
            output["humidity"] = humidity
            output["waterTank"] = waterTank
            output["nutrientTank"] = nutrientTank
            output["turbidity"] = turbidity
            output["ph"] = ph
            output["userId"] = userId
            output["cropsId"] = cropsId
            output["date"] = date
            output["time"] = time
            return output
        }

        private const val TAG = "DataMonitoringModel"
    }
}

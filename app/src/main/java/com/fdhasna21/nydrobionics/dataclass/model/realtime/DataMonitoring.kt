package com.fdhasna21.nydrobionics.dataclass.model.realtime

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataMonitoring(
    var dataId : String? = null,
    var temperature : Float? = 0f,
    var humidity : Float? = 0f,
    var water_tank : Float? = 0f,
    var nutrient_tank : Float? = 0f,
    var turbidity : Float? =0f,
    var pH : Float? = 0f,
    var userId : String? = null,
    var cropsId : String? = null
) : Parcelable {

    @Parcelize
    data class KitLog(
        var logId : String? = null,
        var content : String? = null,
        var date : String? = null,
        var time : String? = null,
        var senderId : String? = null
    ) : Parcelable
}

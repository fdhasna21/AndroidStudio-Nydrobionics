package com.fdhasna21.nydrobionics.dataclass.model.realtime

import android.os.Parcelable
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Crops(
    var cropsId : String? = null,
    var plantId : String? = null,
    var kitId : String? = null,
    var userId : String? = null,
    var temp_lvl : @RawValue ScoreLevel? = null,
    var humid_lvl : @RawValue ScoreLevel? = null
) : Parcelable {
    
    @Parcelize
    data class CropsLog(
        var logId : String,
        var date : String,
        var time : String,
        var state : CropsState,
        var userId : String
    ) : Parcelable
    
    enum class CropsState {
        PLANTED,
        HARVESTED;
    }
}

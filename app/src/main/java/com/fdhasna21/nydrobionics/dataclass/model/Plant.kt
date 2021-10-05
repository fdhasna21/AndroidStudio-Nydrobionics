package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Plant(
    var plantId : String? = null,
    var name : String? = null,
    var desc : String? = null,
    var photo_url : String? = null,
    var temp_lvl : @RawValue ScoreLevel? = null,
    var humid_lvl : @RawValue ScoreLevel? = null,
    var date : String? = null,
    var time : String? = null,
    var userId : String? = null,
    var growthTime : Int? = 0
) : Parcelable

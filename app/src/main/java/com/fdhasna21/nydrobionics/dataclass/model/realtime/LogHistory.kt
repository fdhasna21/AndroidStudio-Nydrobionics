package com.fdhasna21.nydrobionics.dataclass.model.realtime

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LogHistory(
    var logId : String? = null,
    var content : String? = null,
    var senderId : String? = null
):Parcelable

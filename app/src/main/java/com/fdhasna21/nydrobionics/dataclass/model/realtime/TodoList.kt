package com.fdhasna21.nydrobionics.dataclass.model.realtime

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodoList(
    var todoId : String? = null,
    var title: String? = null,
    var description : String? = null,
    var date : String? = null,
    var time : String? = null,
    var userId : String? = null
) : Parcelable

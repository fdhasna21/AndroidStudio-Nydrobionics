package com.fdhasna21.nydrobionics.dataclass.model

import android.os.Parcelable
import android.util.Log
import com.fdhasna21.nydrobionics.dataclass.ScoreLevel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class HydroponicKit(
    var kitId : String? = null,
    var length : Int? = null,
    var width : Int? = null,
    var water_lvl : @RawValue ScoreLevel? = null,
    var nutrient_lvl : @RawValue ScoreLevel? = null
)  : Parcelable {
    companion object{
        fun DocumentSnapshot.toKit() : HydroponicKit?{
            try {
                //todo : isi
                return HydroponicKit()
            } catch (e: Exception) {
                Log.e("HydroponicKit", "Error converting $TAG profile", e)
//                FirebaseCrashlytics.getInstance().log("Error converting $TAG profile")
//                FirebaseCrashlytics.getInstance().setCustomKey("kitId", id)
//                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
        private const val TAG = "HydroponicKit"
    }
}
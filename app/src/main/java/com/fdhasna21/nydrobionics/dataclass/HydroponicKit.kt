package com.fdhasna21.nydrobionics.dataclass

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class HydroponicKit(
    var kitId : String? = null,
    var length : Int? = null,
    var width : Int? = null
)  : Parcelable{
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
@Parcelize
data class FarmModel(
    var farmId : String? = null,
    var name : String? = null,
    var description : String? = null,
    var location : String? = null,
    var gps : String? = null,
    var timestamp : String? = null,
    var kitModels : ArrayList<KitModel>? = null
) : Parcelable
@Parcelize
data class PlantModel(
    var plantId : String? = null,
    var name : String? = null,
    var description : String? = null,
    var photo_url : String? = null,
    var tempLv : @RawValue ScoreLevel? = null,
    var humidLv : @RawValue ScoreLevel? = null,
    var phLv : @RawValue ScoreLevel? = null,
    var userId : String? = null,
    var growthTime : Int? = 0,
    var timestamp : String? = null
) : Parcelable
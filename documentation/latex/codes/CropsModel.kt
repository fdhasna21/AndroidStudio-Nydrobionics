@Parcelize
data class CropsModel(
    var cropsId : String? = null,
    var plantId : String? = null,
    var userId : String? = null,
    var tempLv : @RawValue ScoreLevel? = null,
    var humidLv : @RawValue ScoreLevel? = null,
    var phLv : @RawValue ScoreLevel? = null,
    var timestamp : String? = null,
    var plantModel: PlantModel? = null
) : Parcelable
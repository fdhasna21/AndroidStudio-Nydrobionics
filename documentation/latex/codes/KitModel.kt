@Parcelize
data class KitModel(
    var kitId : String? = null,
    var name : String?= null,
    var position : String? = null,
    var length : Int? = null,
    var width : Int? = null,
    var waterLv : @RawValue ScoreLevel? = null,
    var nutrientLv : @RawValue ScoreLevel? = null,
    var turbidityLv: @RawValue ScoreLevel? = null,
    var isPlanted : Boolean? = false,
    var timestamp : String? = null,
    var lastMonitoring : DataMonitoringModel? = null,
    var lastCrops : CropsModel? = null
)  : Parcelable
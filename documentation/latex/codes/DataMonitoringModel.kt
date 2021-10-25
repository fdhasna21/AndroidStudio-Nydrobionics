@Parcelize
data class DataMonitoringModel(
    var dataId : String? = null,
    var temperature : Float? = 0f,
    var humidity : Float? = 0f,
    var waterTank : Float? = 0f,
    var nutrientTank : Float? = 0f,
    var turbidity : Float? =0f,
    var ph : Float? = 0f,
    var userId : String? = null,
    var cropsId : String? = null,
    var timestamp : String? = null
) : Parcelable
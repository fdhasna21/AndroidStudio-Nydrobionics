@Parcelize
data class UserModel(
    var name : String? = null,
    var email: String? =null,
    var gender : String? = null,
    var dob : String? = null,
    var role : String? = null,
    var bio : String? = null,
    var uid : String? = null,
    var performanceRate : Float? = null,
    var address : String? = null,
    var phone : String? = null,
    var joinedSince : String? = null,
    var photo_url : String? = null,
    var farmId : String? = null,
    var timestamp : String? = null
) : Parcelable
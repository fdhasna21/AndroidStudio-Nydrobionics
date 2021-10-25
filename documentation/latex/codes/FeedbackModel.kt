@Parcelize
data class FeedbackModel(
    var feedbackId : String? = null,
    var userId : String? = null,
    var content : String? = null,
    var rating : Float? = null,
    var timestamp: String? = null
) : Parcelable
@Parcelize
data class NoteModel(
    var noteId : String? = null,
    var title: String? = null,
    var description : String? = null,
    var timestamp : String?=null,
    var date : String? = null,
    var time : String? = null
) : Parcelable
@Parcelize
data class ScoreLevel(
    var min : Float? = 0f,
    var max : Float? = 0f,
    var score : Float? = 0f
) : Parcelable {
    companion object {
        fun getLevelModel(string: String?) : ScoreLevel? {
            return try {
                val output = Gson().fromJson<ScoreLevel>(string, ScoreLevel::class.java)
                ScoreLevel(output.min, output.max)
            } catch (e:Exception){
                Log.e(TAG, "Error converting $TAG", e)
                null
            }
        }

        private const val TAG = "ScoreLevel"
    }
}

package com.fdhasna21.nydrobionics.utility

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.ActionBar
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputEditText
import pl.polak.clicknumberpicker.ClickNumberPickerView
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

open class ViewUtility(
    override var context : Context?,
    override var circularProgressButton: CircularProgressButton? = null,
    override var textInputEditTexts: ArrayList<TextInputEditText>? = null,
    override var viewsAsButton: ArrayList<View>? = null,
    override var numberPickers: ArrayList<ClickNumberPickerView>? = null,
    override var actionBar: ActionBar?,
    override var checkBoxes: ArrayList<CheckBox>? = null
) : LoadingInterface, IntentUtility(context) {
    constructor() : this(context = null, actionBar = null)
    constructor(context: Context?) : this(context=context, actionBar = null)

    companion object{
        const val TAG = "viewUtility"
        const val DATE_FORMAT = "dd MMMM yyyy"
        const val TIME_FORMAT = "HH:mm"
        const val TIMESTAMP_FORMAT = "yyyy/MM/dd HH:mm"
        const val STRING_FORMAT = "dd MMMM yyyy HH:mm"
    }

    override var initialLoadingState: Boolean = false
    override fun onLoadingChangeListener(function: (isLoading: Boolean) -> Unit) {}

    /** TIMESTAMP **/
    fun getCurrentDate() : String {
        return formatDateToString(Calendar.getInstance(TimeZone.getDefault()).timeInMillis)!!
    }

    fun getCurrentTime() : String {
        val c = Calendar.getInstance(TimeZone.getDefault())
        return formatTimeToString(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))!!
    }

    fun getCurrentTimestamp() : String{
        return formatTimestampToString()
    }

    fun formatDateToString(date: Long?) : String?{
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
        return sdf.format(date)
    }

    fun formatTimeToString(hour:Int, minute:Int) : String?{
        val c = Calendar.getInstance(TimeZone.getDefault())
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.US)
        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.MINUTE, minute)
        return sdf.format(c.time)
    }

    fun formatStringToDate(date:String? = getCurrentDate(), est:Int?=0): Long {
        date?.let {
            val c = Calendar.getInstance()
            val pos = ParsePosition(0)
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            c.time = sdf.parse(date, pos)
            c.add(Calendar.DATE, (est?:1))
            return c.timeInMillis
        }
        return formatStringToDate(getCurrentDate())
    }

    fun formatStringToTime(time:String?) : Pair<Int, Int> {
        val c = Calendar.getInstance(TimeZone.getDefault())
        val pos = ParsePosition(0)
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.US)
        c.time = sdf.parse(time ?: getCurrentTime(), pos)
        return Pair(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
    }

    fun formatTimestampToString(date: String?=null, time: String?=null) : String {
        val inputFormat = SimpleDateFormat(STRING_FORMAT, Locale.US)
        val outputFormat = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.US)
        val string = if(date != null && time != null){
            "$date $time"
        } else {
            "${getCurrentDate()} ${getCurrentTime()}"
        }
        return outputFormat.format(inputFormat.parse(string))
    }

    fun formatTimestampToDate(timestamp: String?=null) : String{
        val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)
        val outputFormat = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.US)
        val string = if(timestamp != null){
            timestamp
        } else {
            getCurrentTimestamp()
        }
        return outputFormat.format(inputFormat.parse(string))
    }

    fun formatStringToTimestamp(timestamp:String?) : Pair<String, String>{
        val inputFormat = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.US)
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)
        val timeFormat = SimpleDateFormat(TIME_FORMAT, Locale.US)
        val string = timestamp ?: getCurrentTimestamp()
        return Pair(inputFormat.format(dateFormat.parse(string)), inputFormat.format(timeFormat.parse(string)))
    }

    fun capitalizeEachWord(string: String, delimiter: String = " ", separator: String = " "): String {
        return string.split(delimiter).joinToString(separator = separator) {
            it.lowercase().replaceFirstChar { char -> char.titlecase() }
        }
    }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    /** USER INPUT CHECK **/
    fun isEmpty(textInputEditText: TextInputEditText) : Boolean{
        return textInputEditText.text.toString().count() > 0
    }

    fun isEmpties(textInputEditTexts: ArrayList<TextInputEditText>) : Boolean{
        var output : Boolean? = null
        textInputEditTexts.forEach {
            output = if(output == null){
                 isEmpty(it)
            } else {
                output as Boolean && isEmpty(it)
            }
        }
        return output!!
    }

    private fun isChange(lastString:String, textInputEditText: TextInputEditText) : Boolean{
        return textInputEditText.text.toString() != lastString
    }

    fun isChanges(hashMap: HashMap<String, TextInputEditText>?) : Boolean{
        var output : Boolean? = null
        hashMap?.forEach {
            output = if(output == null){
                isChange(it.key, it.value)
            } else {
                output as Boolean || isChange(it.key, it.value)
            }
        } ?: kotlin.run {
            return false
        }
        return output!!
    }

    private fun isInRange(min : Float?, max: Float?) : Boolean{
        return if (min == null && max == null){
            false
        } else {
            min!! > 0f && min < max!!
        }
    }

    fun isInRanges(minMax : HashMap<Float?, Float?>) : Boolean{
        var output : Boolean? = null
        minMax.forEach { 
            output = if(output==null){
                isInRange(it.key, it.value)
            } else {
                output as Boolean && isInRange(it.key, it.value)
            }
        }
        return output!!
    }
}
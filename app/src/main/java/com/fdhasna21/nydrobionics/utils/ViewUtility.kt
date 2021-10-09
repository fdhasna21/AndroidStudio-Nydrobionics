package com.fdhasna21.nydrobionics.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.enumclass.ProfileType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import pl.polak.clicknumberpicker.ClickNumberPickerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class ViewUtility(
    override var context : Context?,
    override var circularProgressButton: CircularProgressButton? = null,
    override var textInputEditTexts: ArrayList<TextInputEditText>? = null,
    override var viewsAsButton: ArrayList<View>? = null,
    override var numberPickers: ArrayList<ClickNumberPickerView>? = null,
    override var actionBar: ActionBar?
) : LoadingInterface, IntentUtility(context) {
    constructor() : this(context = null, actionBar = null)

    companion object{
        const val TAG = "viewUtility"
    }

    override var initialState: Boolean = false

    override fun onLoadingChangeListener(function: (isLoading: Boolean) -> Unit) {}

    fun getCurrentDate() : String? {
        return formatDate(Calendar.getInstance(TimeZone.getDefault()).timeInMillis)
    }

    fun getCurrentTime() : String? {
        val c = Calendar.getInstance(TimeZone.getDefault())
        return formatTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))
    }

    fun formatDate(date: Long?) : String?{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        return sdf.format(date)
    }

    fun formatTime(hour:Int, minute:Int) : String?{
        val c = Calendar.getInstance(TimeZone.getDefault())
        val sdf = SimpleDateFormat("HH:mm", Locale.US)
        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.MINUTE, minute)
        return sdf.format(c.time)
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

    fun isChanges(hashMap: HashMap<String, TextInputEditText>) : Boolean{
        var output : Boolean? = null
        hashMap.forEach {
            output = if(output == null){
                isChange(it.key, it.value)
            } else {
                output as Boolean || isChange(it.key, it.value)
            }
        }
        return output!!
    }

    private fun isInRange(min : Float?, max: Float?) : Boolean{
        return if (min == null && max == null){
            false
        } else {
            min!! > 0f && min!! < max!!
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

    fun isLoading(condition:Boolean, circularProgressButton: CircularProgressButton){
        circularProgressButton.apply {
            isEnabled = !condition
            if(condition){
                startAnimation()
            } else {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    revertAnimation()
                }, 3000)
            }
        }
    }
}
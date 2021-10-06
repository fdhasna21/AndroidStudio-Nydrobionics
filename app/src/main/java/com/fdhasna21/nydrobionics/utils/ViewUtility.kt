package com.fdhasna21.nydrobionics.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import com.google.android.material.textfield.TextInputEditText

class ViewUtility {
    companion object{
        const val TAG = "viewUtility"
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

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun isChange(lastString:String, textInputEditText: TextInputEditText) : Boolean{
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
}
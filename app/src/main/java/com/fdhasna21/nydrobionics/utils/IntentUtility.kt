package com.fdhasna21.nydrobionics.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fdhasna21.nydrobionics.activity.ShowPictureActivity

class IntentUtility(val context: Context){
    fun openBrowser(url: String){
        val intent = Intent()
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(String.format(url))
        context.startActivity(intent)
    }

    fun openEmail(email:String){
        val intent = Intent()
        intent.action = Intent.ACTION_SENDTO
        intent.type = "text/plain"
        intent.data = Uri.parse("mailto:$email")
        context.startActivity(Intent.createChooser(intent, "Send email with"))
    }

    fun openMaps(location:String){
        openBrowser(
            "https://www.google.com/maps?q=${
                location.replace(
                    ' ',
                    '+'
                )
            }"
        )
    }

    fun openCall(phoneNumber:String){
        //todo : blm jelas nomer e
        val intent = Intent()
        intent.action = Intent.ACTION_CALL
        intent.data = Uri.parse("tel:+62"+Uri.encode(phoneNumber.drop(1)))
        context.startActivity(intent)
    }

    fun openImage(imageView:View, title:String="Photo Profile"){
        try {
            val filename = "image_preview.png"
            val stream = context.openFileOutput(filename, AppCompatActivity.MODE_PRIVATE)
            ViewUtility().getBitmapFromView(imageView).compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            val intent = Intent(context, ShowPictureActivity::class.java)
            intent.putExtra("actionBarTitle", title)
            intent.putExtra("photoContent", filename)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

package com.fdhasna21.nydrobionics.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.activity.AddPlantActivity
import com.fdhasna21.nydrobionics.activity.CreateProfileActivity
import com.fdhasna21.nydrobionics.activity.EditProfileUserActivity
import com.fdhasna21.nydrobionics.activity.ShowPictureActivity
import com.fdhasna21.nydrobionics.enumclass.ProfileType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView

open class IntentUtility(open val context: Context?){
    companion object {
        const val TAG = "IntentUtility"
        const val ERROR_MESSAGE = "no activity found"
    }

    fun openBrowser(url: String) {
        try {
            val intent = Intent()
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(String.format(url))
            context!!.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "openBrowser: $ERROR_MESSAGE", e)
        }
    }

    fun openEmail(email:String) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SENDTO
            intent.type = "text/plain"
            intent.data = Uri.parse("mailto:$email")
            context!!.startActivity(Intent.createChooser(intent, "Send email with"))
        } catch (e: Exception) {
            Log.e(TAG, "openEmail: $ERROR_MESSAGE")
        }
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

    fun openCall(phoneNumber:String) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_CALL
            intent.data = Uri.parse("tel:+62" + Uri.encode(phoneNumber.drop(1)))
            context!!.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "openCall: $ERROR_MESSAGE")
        }
    }

    fun openEditPhoto(shapeableImageView: ShapeableImageView, profileType: ProfileType){
        val items = arrayOf("Select photo", "Delete profile picture")
        MaterialAlertDialogBuilder(context!!)
            .setItems(items) { _, which ->
                when(which){
                    0 -> RequestPermission().requestMultiplePermissions((context!! as Activity), listOf(
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), "Change profile picture")
                    1 -> shapeableImageView.setImageDrawable(ContextCompat.getDrawable((context!! as Activity), profileType.getDefaultPicture()))
                }
            }
            .show()
    }

    fun openOptions(edit:(() -> Unit), delete:(() -> Unit)){
        val items = arrayOf("Edit", "Delete")
        MaterialAlertDialogBuilder(context!!)
            .setItems(items){_, which ->
                when(which){
                    0 -> edit
                    1 -> delete
                }
            }
            .show()
    }

    fun openImage(imageView:View, title:String="Photo Profile"){
        try {
            val filename = "image_preview.png"
            val stream = context?.openFileOutput(filename, AppCompatActivity.MODE_PRIVATE)
            ViewUtility(context, actionBar = null).getBitmapFromView(imageView).compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream?.close()
            val intent = Intent(context, ShowPictureActivity::class.java)
            intent.putExtra("actionBarTitle", title)
            intent.putExtra("photoContent", filename)
            context?.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "openImage: $ERROR_MESSAGE", e)
        }
    }

    fun openAppInfo(){
        try {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            context?.startActivity(intent)

        } catch (e:Exception){
            Log.e(TAG, "openAppInfo: $ERROR_MESSAGE", e)
        }
    }

    fun openLanguageSettings(){
        try {
            context?.startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }catch (e:Exception){
            Log.e(TAG, "openLanguageSettings: $ERROR_MESSAGE", e)
        }
    }

    fun openCropImage(){
        try {
            val intent = CropImage.activity()
                .setActivityTitle("Edit Photo")
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAutoZoomEnabled(true)
                .setAllowFlipping(false)
                .getIntent(context!!)
            when(context){
                is CreateProfileActivity -> (context as CreateProfileActivity).startForResult.launch(intent)
                is EditProfileUserActivity -> (context as EditProfileUserActivity).startForResult.launch(intent)
                is AddPlantActivity -> (context as AddPlantActivity).startForResult.launch(intent)
                else -> Log.e(TAG, "openCropImage: $ERROR_MESSAGE")
            }
        } catch (e:Exception){
            Log.e(TAG, "openCropImage: $ERROR_MESSAGE", e)
        }
    }
}

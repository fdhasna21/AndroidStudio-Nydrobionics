package com.fdhasna21.nydrobionics.utils

import android.Manifest
import android.R
import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.fdhasna21.nydrobionics.activity.*
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


class RequestPermission {
    private val manifestPermissions : List<String> = listOf(
        Manifest.permission.INTERNET,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE)
    private val errorListener =
        PermissionRequestErrorListener { error -> Log.e("Dexter", "There was an error: $error") }

    fun requestAllPermissions(activity: SplashScreenActivity){
        val allListeners = CompositeMultiplePermissionsListener(
            SnackbarOnAnyDeniedMultiplePermissionsListener
                .Builder
                .with(activity.findViewById(R.id.content), "All those permissions are needed for this application.")
                .withOpenSettingsButton("SETTINGS")
                .withCallback(object : Snackbar.Callback(){
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        activity.splashIsDone()
                    }
                })
                .build(),
            object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        activity.splashIsDone()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }
        )

        Dexter.withContext(activity)
            .withPermissions(manifestPermissions)
            .withListener(allListeners)
            .withErrorListener(errorListener)
            .check()
    }

    fun requestMultiplePermissions(activity: Activity, manifestPermissions : List<String>, title: String){
        val allListeners = CompositeMultiplePermissionsListener(
            SnackbarOnAnyDeniedMultiplePermissionsListener
                .Builder
                .with(activity.findViewById(R.id.content), "$title will not be available until you accept the permission request.")
                .withOpenSettingsButton("SETTINGS")
                .build(),
            object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        if(manifestPermissions.find { it == Manifest.permission.CAMERA } != null)
                            IntentUtility(activity).openCropImage()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }
        )

        Dexter.withContext(activity)
            .withPermissions(manifestPermissions)
            .withListener(allListeners)
            .withErrorListener(errorListener)
            .check()
    }

    fun requestPermission(activity: Activity, manifestPermission: String, message:String, content:String=""){//, @DrawableRes resId: Int){
        Dexter.withContext(activity)
            .withPermission(manifestPermission)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    if(manifestPermission == Manifest.permission.CALL_PHONE){
                        IntentUtility(activity).openCall(content)
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(activity, "$message will not be available until you accept the permission request.", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener(errorListener)
            .check()
    }
}
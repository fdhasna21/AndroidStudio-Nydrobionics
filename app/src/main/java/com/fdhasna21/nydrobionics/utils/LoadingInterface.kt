package com.fdhasna21.nydrobionics.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.ActionBar
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputEditText
import pl.polak.clicknumberpicker.ClickNumberPickerView

interface LoadingInterface {
    var context : Context?
    var circularProgressButton : CircularProgressButton?
    var textInputEditTexts : ArrayList<TextInputEditText>?
    var viewsAsButton : ArrayList<View>?
    var numberPickers : ArrayList<ClickNumberPickerView>?
    var actionBar : ActionBar?

    var initialState : Boolean
    var isLoading : Boolean
        get() = initialState
        set(value) {
            textInputEditTexts?.forEach {
                it.isCursorVisible = !value
                it.isFocusable = !value
                it.isFocusableInTouchMode = !value
            }
            viewsAsButton?.forEach {
                it.isEnabled = !value
            }
            numberPickers?.forEach {
                it.isEnabled
            }
            circularProgressButton?.apply {
                this.isEnabled = !value
                if(value){
                    startAnimation()
                } else {
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        revertAnimation()
                    }, 3000)
                }
            }
            actionBar?.setDisplayShowHomeEnabled(value)
            onLoadingChangeListener{(initialState)}
            initialState = value
        }

    fun onLoadingChangeListener(function: (isLoading:Boolean) -> Unit)
}
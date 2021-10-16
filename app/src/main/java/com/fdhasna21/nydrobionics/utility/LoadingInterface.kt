package com.fdhasna21.nydrobionics.utility

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.CheckBox
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
    var checkBoxes : ArrayList<CheckBox>?
    var actionBar : ActionBar?

    var initialLoadingState : Boolean
    var isLoading : Boolean
        get() = initialLoadingState
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
                //cannot change intractability because of the library
            }
            circularProgressButton?.apply {
                this.saveInitialState()
                this.isEnabled = true
                if(value){
                    startAnimation()
                } else {
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        revertAnimation()
                    }, 3000)
                }
            }
            checkBoxes?.forEach {
                it.isClickable = !value
            }
            actionBar?.setDisplayShowHomeEnabled(value)
            onLoadingChangeListener{(initialLoadingState)}
            initialLoadingState = value
        }
    fun onLoadingChangeListener(function: (isLoading:Boolean) -> Unit)

//    var swipeRefreshLayout : SwipeRefreshLayout?
//    var initialRefreshState : Boolean
//    var isRefresh : Boolean
//        get() = initialRefreshState
//        set(value) {
//            swipeRefreshLayout?.isRefreshing = value
//            onRefreshChangeListener { (initialRefreshState) }
//            initialRefreshState = value
//        }
//    fun onRefreshChangeListener(function: (isRefresh:Boolean) -> Unit)
}
package com.fdhasna21.nydrobionics.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
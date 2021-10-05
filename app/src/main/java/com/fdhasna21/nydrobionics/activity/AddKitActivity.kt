package com.fdhasna21.nydrobionics.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddKitBinding
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.AddKitViewModel
import com.google.android.material.textfield.TextInputEditText

//todo : include edit kit. mekanisme buat nyalain edittext
class AddKitActivity : AppCompatActivity(), TextWatcher {
    private lateinit var binding : ActivityAddKitBinding
    private lateinit var viewModel : AddKitViewModel
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var tooltip : ArrayList<ImageButton>

    companion object {
        const val TAG = "addKit"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddKitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddKitViewModel::class.java)

        //todo : nerima data dari ProfileKit
        supportActionBar?.title = getString(R.string.create_new_kit)
//        todo supportActionBar?.subtitle = nama farm
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            tooltip = arrayListOf(akSizeInfo,
                                  akWaterLevelInfo,
                                  akNutrientLevelInfo)
            tooltip.forEach {
                it.setOnClickListener {
                    it.performLongClick()
                }
            }
            akSizeInfo.tooltipText = "How many holes on each side length and width"
            akWaterLevelInfo.tooltipText = getString(R.string.level_tooltip)
            akNutrientLevelInfo.tooltipText = getString(R.string.level_tooltip)

            editTexts = arrayListOf(addKitName, addKitPosition)
            editTexts.forEach { it.addTextChangedListener(this@AddKitActivity) }
            checkEmpty()

            addKitSubmit.setOnClickListener {
                //todo : send data
                onBackPressed()
            }
        }

        //todo : +- submit
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty(){
        viewModel.apply {
            checkNotEmpty(ViewUtility().isEmpties(editTexts)).observe(this@AddKitActivity, {
                binding.addKitSubmit.isEnabled = it
            })
        }
    }
}
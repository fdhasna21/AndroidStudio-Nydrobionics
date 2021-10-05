package com.fdhasna21.nydrobionics.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddPlantBinding
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.AddPlantViewModel
import com.google.android.material.textfield.TextInputEditText

class AddPlantActivity : AppCompatActivity(), TextWatcher {
    private lateinit var binding : ActivityAddPlantBinding
    private lateinit var viewModel : AddPlantViewModel
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var tooltips : ArrayList<ImageButton>

    companion object{
        const val TAG = "addPlant"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddPlantViewModel::class.java)
        supportActionBar?.title = getString(R.string.add_new_plant)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            tooltips = arrayListOf(apGrowthInfo,
                                  apTempInfo,
                                  apHumidInfo)
            tooltips.forEach {
                it.setOnClickListener {
                    it.performLongClick()
                }
            }
            //todo : tooltip

            editTexts = arrayListOf(addPlantName, addPlantDescription)
            editTexts.forEach { it.addTextChangedListener(this@AddPlantActivity) }
            checkEmpty()

            addPlantSubmit.setOnClickListener {
                if(intent.getStringExtra("from") == "addCrops"){
                    val intent = Intent()
                    //todo : kirim data nya
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    onBackPressed()
                }
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
            checkNotEmpty(ViewUtility().isEmpties(editTexts)).observe(this@AddPlantActivity, {
                binding.addPlantSubmit.isEnabled = it
            })
        }
    }
}
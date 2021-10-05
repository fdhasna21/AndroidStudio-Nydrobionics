package com.fdhasna21.nydrobionics.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddDataMonitoringBinding
import com.fdhasna21.nydrobionics.viewmodel.AddDataMonitoringViewModel

class AddDataMonitoringActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddDataMonitoringBinding
    private lateinit var viewModel : AddDataMonitoringViewModel
    private lateinit var tooltips : ArrayList<ImageButton>

    companion object{
        const val TAG = "addDataMonitoring"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddDataMonitoringViewModel::class.java)
        supportActionBar?.title = getString(R.string.add_recent_data_monitoring)
//        todo supportActionBar?.subtitle = nama farm
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        //selector kit
        val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")
        val adapter = ArrayAdapter(this, R.layout.row_item_list, items)
        (binding.admSelectKit.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        //todo : tooltip semuanya
        binding.apply {
            tooltips = arrayListOf(admTempInfo,
                                  admHumidInfo,
                                  admAcidityInfo,
                                  admWaterLevelInfo,
                                  admNutrientLevelInfo,
                                  admWaterTurbidityInfo)
            tooltips.forEach {
                it.setOnClickListener {
                    it.performLongClick()
                }
            }

            admHumidInfo.tooltipText = "Min : \n" +
                                       "Max : "

            addMonitoringSubmit.setOnClickListener {
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

}
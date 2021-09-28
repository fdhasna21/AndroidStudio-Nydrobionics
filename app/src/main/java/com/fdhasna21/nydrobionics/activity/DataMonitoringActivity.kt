package com.fdhasna21.nydrobionics.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityDataMonitoringBinding
import com.fdhasna21.nydrobionics.viewmodel.DataMonitoringViewModel

class DataMonitoringActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDataMonitoringBinding
    private lateinit var viewModel : DataMonitoringViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(DataMonitoringViewModel::class.java)
        supportActionBar?.title = getString(R.string.data_monitoring)
//        supportActionBar?.subtitle = //nama kitnya
        supportActionBar?.elevation= 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)
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
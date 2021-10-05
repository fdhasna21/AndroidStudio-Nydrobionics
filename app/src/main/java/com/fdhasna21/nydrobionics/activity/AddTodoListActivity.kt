package com.fdhasna21.nydrobionics.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddTodoListBinding
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.AddTodoListViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class AddTodoListActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var binding : ActivityAddTodoListBinding
    private lateinit var viewModel : AddTodoListViewModel
    private lateinit var editTexts : ArrayList<TextInputEditText>

    companion object {
        const val TAG = "addTodoList"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddTodoListViewModel::class.java)
        supportActionBar?.title = getString(R.string.add_schedule_or_to_do_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            editTexts = arrayListOf(atlDate, atlTime, addTodoTitle, addTodoDesc)
            editTexts.forEach {
                it.setOnClickListener(this@AddTodoListActivity)
                it.addTextChangedListener(this@AddTodoListActivity)
            }

            addTodoSubmit.setOnClickListener(this@AddTodoListActivity)
            checkEmpty()
        }
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

    override fun onClick(v: View?) {
        when(v){
            binding.atlDate -> {
                val pos = ParsePosition(0)
                val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)
                var lastDate = sdf.parse(binding.atlDate.text.toString(), pos)?.time
                lastDate = if(lastDate == null){
                    MaterialDatePicker.todayInUtcMilliseconds()
                } else {
                    lastDate + TimeUnit.DAYS.toMillis(1)
                }

                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select a date")
                    .setSelection(lastDate)
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .build()
                datePicker.show(supportFragmentManager, "DATE_PICKER")
                datePicker.addOnPositiveButtonClickListener{
                    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)
                    binding.atlDate.setText(sdf.format(datePicker.selection))}
                datePicker.addOnNegativeButtonClickListener{}
                datePicker.isCancelable = false
            }
            binding.atlTime -> {
                val isSystem24Hour = DateFormat.is24HourFormat(this)
                val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
                val calendar = Calendar.getInstance(TimeZone.getDefault())

                val pos = ParsePosition(0)
                val sdf = SimpleDateFormat(if (isSystem24Hour) "HH:mm" else "KK:mm a", Locale.US)
                calendar.time = sdf.parse(binding.atlTime.text.toString(), pos) ?: calendar.time

                val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(clockFormat)
                    .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                    .setMinute(calendar.get(Calendar.MINUTE))
                    .setTitleText("Select a time")
                    .build()
                timePicker.show(supportFragmentManager, "TIME_PICKER")
                timePicker.isCancelable = false

                timePicker.addOnPositiveButtonClickListener {
                    val timeFormat = if (isSystem24Hour) "HH:mm" else "KK:mm a"
                    val sdf = SimpleDateFormat(timeFormat, Locale.US)
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                    calendar.set(Calendar.MINUTE, timePicker.minute)
                    binding.atlTime.setText(sdf.format(calendar.time))
                }
                timePicker.addOnNegativeButtonClickListener {}
            }
            binding.addTodoSubmit -> {
                //todo : send data
                onBackPressed()
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty(){
        viewModel.apply {
            checkNotEmpty(ViewUtility().isEmpties(editTexts)).observe(this@AddTodoListActivity, {
                binding.addTodoSubmit.isEnabled = it
            })
        }
    }
}
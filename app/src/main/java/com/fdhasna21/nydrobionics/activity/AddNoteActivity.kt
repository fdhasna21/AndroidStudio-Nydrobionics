package com.fdhasna21.nydrobionics.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddNoteBinding
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel
import com.fdhasna21.nydrobionics.fragment.CreateFarmFragment
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.AddNoteModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.collections.ArrayList

class AddNoteActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var binding : ActivityAddNoteBinding
    private lateinit var viewModel : AddNoteModel
    private lateinit var utility : ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>

    companion object {
        const val TAG = "addNoteList"
    }

    //todo : kurang edit (tampilin data, check update)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddNoteModel::class.java)
        viewModel.setCurrentData(intent.getParcelableExtra<NoteModel>("currentNoteModel"))

        supportActionBar?.title = getString(R.string.add_schedule_or_to_do_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            editTexts = arrayListOf(addNoteDate, addNoteTime, addNoteTitle, addNoteDesc)
            utility = ViewUtility(
                context = this@AddNoteActivity,
                circularProgressButton = addNoteSubmit,
                textInputEditTexts = editTexts,
                actionBar = supportActionBar
            )

            editTexts.forEach {
                it.setOnClickListener(this@AddNoteActivity)
                it.addTextChangedListener(this@AddNoteActivity)
            }
            addNoteSubmit.setOnClickListener(this@AddNoteActivity)
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
            binding.addNoteDate -> {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Date of Birth")
                    .setSelection(viewModel.getDate())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .build()
                datePicker.show(supportFragmentManager, "DATE_PICKER")
                datePicker.addOnPositiveButtonClickListener{
                    binding.addNoteDate.setText(viewModel.setDate(datePicker.selection))
                }
                datePicker.addOnNegativeButtonClickListener{}
                datePicker.isCancelable = false
            }
            binding.addNoteTime -> {
                viewModel.getTime()
                val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(viewModel.getTimeHour())
                    .setMinute(viewModel.getTimeMinute())
                    .setTitleText("Select a time")
                    .build()
                timePicker.show(supportFragmentManager, "TIME_PICKER")
                timePicker.isCancelable = false

                timePicker.addOnPositiveButtonClickListener {
                    binding.addNoteTime.setText(viewModel.setTime(timePicker.hour, timePicker.minute))
                }
                timePicker.addOnNegativeButtonClickListener {}
            }
            binding.addNoteSubmit -> {
                utility.isLoading = true
                viewModel.createNote(binding.addNoteTitle.text.toString(),
                    binding.addNoteDesc.text.toString())
                viewModel.isNoteAdd.observe(this, {
                    if(it){
                        utility.isLoading = false
                        Toast.makeText(this, "New note created.", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                        finish()
                    } else {
                        utility.isLoading = false
                        viewModel.addNoteError.observe(this, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                                Log.i(CreateFarmFragment.TAG, it)
                                viewModel.addNoteError.value = ""
                            }
                        })
                    }
                })
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
            checkNotEmpty(utility.isEmpties(editTexts)).observe(this@AddNoteActivity, {
                binding.addNoteSubmit.isEnabled = it
            })
        }
    }
}
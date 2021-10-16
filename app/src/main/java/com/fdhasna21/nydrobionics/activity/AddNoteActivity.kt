package com.fdhasna21.nydrobionics.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddNoteBinding
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel
import com.fdhasna21.nydrobionics.fragment.createprofile.CreateFarmFragment
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.AddNoteViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddNoteActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var binding : ActivityAddNoteBinding
    private lateinit var viewModel : AddNoteViewModel
    private lateinit var utility : ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private var strEdt : HashMap<String, TextInputEditText> = hashMapOf()

    companion object {
        const val TAG = "addNoteActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddNoteViewModel::class.java)
        viewModel.setCurrentData(intent.getParcelableExtra<NoteModel>(BuildConfig.SELECTED_NOTE))

        supportActionBar?.title =
            if(intent.getParcelableExtra<NoteModel>(BuildConfig.SELECTED_NOTE) == null) {
                getString(R.string.add_notes)
            } else {
                getString(R.string.edit_notes)
            }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            editTexts = arrayListOf(addNoteTitle, addNoteDesc)
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

            viewModel.getCurrentNoteModel().observe(this@AddNoteActivity, {
                if(it!=null) {
                    addNoteTitle.setText(it.title)
                    addNoteDesc.setText(it.description)
                    it.date?.let { date->
                        addNoteDate.setText(date)
                    }
                    it.time?.let{ time->
                        addNoteTime.setText(time)
                    }
                    strEdt[it.title ?: ""] = addNoteTitle
                    strEdt[it.description ?: ""] = addNoteDesc
                    strEdt[it.date ?: ""] = addNoteDate
                    strEdt[it.time ?:""] = addNoteTime
                }
            })
            checkEmpty()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if(intent.getParcelableExtra<NoteModel>(BuildConfig.SELECTED_NOTE) != null){
            viewModel.isNotEmpties.observe(this, {
                when(it){
                    true -> binding.addNoteSubmit.performClick()
                    else -> super.onBackPressed()
                }
            })
        } else {
            super.onBackPressed()
        }
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
                        val toastTxt = if(intent.getParcelableExtra<NoteModel>(BuildConfig.SELECTED_NOTE) != null){
                            "Note updated."
                        } else {
                            "New note created."
                        }
                        Toast.makeText(this, toastTxt, Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
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
        if(intent.getParcelableExtra<NoteModel>(BuildConfig.SELECTED_NOTE) != null){
            viewModel.apply {
                checkNotEmpty(utility.isEmpties(editTexts) && utility.isChanges(strEdt))
                    .observe(this@AddNoteActivity, {
                        binding.addNoteSubmit.isEnabled = it
                    })
            }
        } else {
            viewModel.apply {
                checkNotEmpty(utility.isEmpties(editTexts)).observe(this@AddNoteActivity, {
                    binding.addNoteSubmit.isEnabled = it
                })
            }
        }
    }
}
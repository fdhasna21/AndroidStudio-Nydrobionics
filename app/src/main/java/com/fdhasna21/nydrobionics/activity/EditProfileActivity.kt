package com.fdhasna21.nydrobionics.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityEditProfileBinding
import com.fdhasna21.nydrobionics.databinding.FragmentCreateUserBinding
import com.fdhasna21.nydrobionics.enumclass.Gender
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class EditProfileActivity : AppCompatActivity(), View.OnClickListener, SegmentedButtonGroup.OnPositionChangedListener {
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var bindingFragment : FragmentCreateUserBinding
    private lateinit var viewModel : CreateProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)
        supportActionBar?.title = getString(R.string.edit_profile)
        supportActionBar?.elevation= 0f
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        bindingFragment = binding.editProfileFragment
        setupDefaultData()
        bindingFragment.apply {
            createUserDOB.setOnClickListener(this@EditProfileActivity)
            createUserSubmit.setOnClickListener(this@EditProfileActivity)
            createUserGender.onPositionChangedListener = this@EditProfileActivity
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        //todo : klo ada perubahan, alert dialog
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
            bindingFragment.createUserDOB -> {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("WIB"))
                calendar[Calendar.MONTH] = Calendar.DECEMBER
                val constraintBuilder = CalendarConstraints.Builder()
                constraintBuilder.setValidator(DateValidatorPointBackward.now()).setEnd(calendar.timeInMillis)

                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Date of Birth")
                    .setSelection(viewModel.getDOB())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .setCalendarConstraints(constraintBuilder.build())
                    .build()
                datePicker.show(supportFragmentManager, "DATE_PICKER")
                datePicker.addOnPositiveButtonClickListener{
                    bindingFragment.createUserDOB.setText(viewModel.setDOB(datePicker.selection))
                }
                datePicker.addOnNegativeButtonClickListener{}
                datePicker.isCancelable = false
            }
            bindingFragment.createUserSubmit -> {
                //todo : kirim data
                onBackPressed()
            }
        }
    }

    override fun onPositionChanged(position: Int) {
        when(position){
            0 -> viewModel.setGender(Gender.MALE)
            1 -> viewModel.setGender(Gender.FEMALE)
        }
    }

    private fun setupDefaultData(){
        bindingFragment.apply {
            createUserRoleGroup.visibility = View.GONE
            createUserSubmit.text = getString(R.string.save)

            //todo : start data
        }
    }
}
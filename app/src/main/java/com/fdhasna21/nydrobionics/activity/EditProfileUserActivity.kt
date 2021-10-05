package com.fdhasna21.nydrobionics.activity

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityEditProfileUserBinding
import com.fdhasna21.nydrobionics.databinding.FragmentCreateUserBinding
import com.fdhasna21.nydrobionics.enumclass.Gender
import com.fdhasna21.nydrobionics.utils.IntentUtility
import com.fdhasna21.nydrobionics.utils.RequestPermission
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import kotlin.collections.ArrayList

class EditProfileUserActivity : AppCompatActivity(), View.OnClickListener, SegmentedButtonGroup.OnPositionChangedListener {
    private lateinit var binding : ActivityEditProfileUserBinding
    private lateinit var bindingFragment : FragmentCreateUserBinding
    private lateinit var viewModel : CreateProfileViewModel
    private lateinit var viewsAsButton : ArrayList<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)
        supportActionBar?.title = getString(R.string.edit_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        bindingFragment = binding.editProfileFragment
        setupDefaultData()
        bindingFragment.apply {
            viewsAsButton = arrayListOf(createUserDOB,
                                        createUserSubmit,
                                        createUserEditPhoto,
                                        createUserPhoto)
            viewsAsButton.forEach { it.setOnClickListener(this@EditProfileUserActivity) }
            createUserPhoto.setOnLongClickListener { createUserEditPhoto.performClick() }

            createUserGender.onPositionChangedListener = this@EditProfileUserActivity
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
            bindingFragment.createUserEditPhoto -> {
                val items = arrayOf("Select photo", "Delete profile picture")
                MaterialAlertDialogBuilder(this)
                    .setItems(items) { _, which ->
                        when(which){
                            0 -> RequestPermission().requestMultiplePermissions(this, listOf(
                                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), "Change profile picture")
                            1 -> bindingFragment.createUserPhoto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_farmer))
                        }
                    }
                    .show()
            }
            bindingFragment.createUserPhoto -> IntentUtility(this).openImage(bindingFragment.createUserPhoto, bindingFragment.createUserName.text.toString())
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
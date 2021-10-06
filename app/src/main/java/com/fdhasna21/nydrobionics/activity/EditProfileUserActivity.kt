package com.fdhasna21.nydrobionics.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityEditProfileUserBinding
import com.fdhasna21.nydrobionics.databinding.FragmentCreateUserBinding
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.enumclass.Gender
import com.fdhasna21.nydrobionics.utils.IntentUtility
import com.fdhasna21.nydrobionics.utils.RequestPermission
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class EditProfileUserActivity : AppCompatActivity(), View.OnClickListener, SegmentedButtonGroup.OnPositionChangedListener, TextWatcher {
    private lateinit var binding : ActivityEditProfileUserBinding
    private lateinit var bindingFragment : FragmentCreateUserBinding
    private lateinit var viewModel : CreateProfileViewModel
    private lateinit var viewsAsButton : ArrayList<View>
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private var strEdt : HashMap<String, TextInputEditText> = hashMapOf()
    private var currentUserModel : UserModel? = null

    companion object{
        const val TAG = "editProfileUser"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)
        supportActionBar?.title = getString(R.string.edit_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        bindingFragment = binding.editProfileFragment
        bindingFragment.apply {
            createUserGender.onPositionChangedListener = this@EditProfileUserActivity
            setupDefaultData()

            viewsAsButton = arrayListOf(createUserDOB,
                                        createUserSubmit,
                                        createUserEditPhoto,
                                        createUserPhoto)
            viewsAsButton.forEach { it.setOnClickListener(this@EditProfileUserActivity) }

            viewsAsButton = arrayListOf(roleOwner,
                roleStaff,
                createUserDOB,
                createUserSubmit,
                createUserEditPhoto,
                createUserPhoto)
            viewsAsButton.forEach { it.setOnClickListener(this@EditProfileUserActivity) }

            editTexts = arrayListOf(
                createUserName,
                createUserPhone,
                createUserAddress,
                createUserDOB)
            editTexts.forEach { it.addTextChangedListener(this@EditProfileUserActivity) }

            createUserPhoto.setOnLongClickListener { createUserEditPhoto.performClick() }
            checkUpdate()
        }

        viewModel.getPhotoProfile().observe(this, {
            it?.let {
                Glide.with(this)
                    .load(it.uri)
                    .circleCrop()
                    .into(bindingFragment.createUserPhoto)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        viewModel.isNotEmpties.observe(this, {
            when(it){
                true -> bindingFragment.createUserSubmit.performClick()
                else -> onBackPressed()
            }
        })
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
                            1 -> {
                                bindingFragment.createUserPhoto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_farmer))
                                viewModel.setPhotoProfile(null)
                                checkUpdate()
                            }
                        }
                    }
                    .show()
            }
            bindingFragment.createUserPhoto -> IntentUtility(this).openImage(bindingFragment.createUserPhoto)
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
                isLoading = true
                viewModel.createUserProfile(
                    bindingFragment.createUserName.text.toString(),
                    bindingFragment.createUserPhone.text.toString(),
                    bindingFragment.createUserAddress.text.toString(),
                    bindingFragment.createUserBio.text.toString()
                )
                viewModel.isUserCreated.observe(this, {
                    if(it){
                        isLoading = false
                        Toast.makeText(this, "Profile updated.", Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        intent.putExtra("currentUserModel", viewModel.getUserModel())
                        setResult(RESULT_OK, intent)
                    } else {
                        isLoading = false
                        viewModel.createProfileError.observe(this, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                                viewModel.createProfileError.value = ""
                            }
                        })
                    }
                })

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
            isLoading = false

            currentUserModel = intent.getParcelableExtra<UserModel>("currentUserModel")
            currentUserModel?.let {
                viewModel.setUserModel(it)
                bindingFragment.apply {
                    createUserEmail.setText(Firebase.auth.currentUser?.email)
                    createUserName.setText(it.name)
                    createUserPhone.setText(it.phone)
                    createUserAddress.setText(it.address)
                    createUserBio.setText(it.bio)
                    createUserDOB.setText(it.dob)
                    it.photo_url?.let {
                        Glide.with(this@EditProfileUserActivity)
                            .load(it)
                            .centerCrop()
                            .into(createUserPhoto)
                    }
                    createUserGender.setPosition(Gender.getType(it.gender!!)!!.getPosition(), false)

                    strEdt[it.name!!] = createUserName
                    strEdt[it.phone!!] = createUserPhone
                    strEdt[it.address!!] = createUserAddress
                    strEdt[it.bio!!] = createUserBio
                    strEdt[it.dob!!] = createUserDOB
                }
            }
        }
    }

    private var isLoading : Boolean = false
        set(value) {
            val editableEditText : ArrayList<TextInputEditText> = arrayListOf(
                bindingFragment.createUserName,
                bindingFragment.createUserPhone,
                bindingFragment.createUserAddress,
                bindingFragment.createUserBio,
                bindingFragment.createUserDOB
            )
            editableEditText.forEach {
                it.isCursorVisible = !value
                it.isFocusable = !value
                it.isFocusableInTouchMode = !value
            }
            supportActionBar?.setHomeButtonEnabled(value)
            if(value){
                bindingFragment.createUserSubmit.startAnimation()
            } else {
                bindingFragment.createUserSubmit.revertAnimation()
            }

            field = value
        }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkUpdate()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkUpdate() {
        if(currentUserModel == null){
            viewModel.checkNotEmpty(
                ViewUtility().isEmpties(editTexts)
            ).observe(this, {
                bindingFragment.createUserSubmit.isEnabled = it
            })
        } else {
            viewModel.checkNotEmpty(
                ViewUtility().isChanges(strEdt) ||
                        viewModel.getGenderPosition() != bindingFragment.createUserGender.position ||
                        viewModel.getPhotoProfile().value != null
            ).observe(this, {
                bindingFragment.createUserSubmit.isEnabled = it
            })
        }

    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        Log.i(TAG, "$data")
        try{
            if(data?.resultCode == Activity.RESULT_OK){
                CropImage.getActivityResult(data.data)?.let{
                    val mimeTypeMap = MimeTypeMap.getSingleton()
                    val fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(it.uriContent!!))
                    viewModel.setPhotoProfile(it.uriContent!!, fileExtension)
                    checkUpdate()
                }
            }

        } catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun changeImageProfile(){
        val intent = CropImage.activity()
            .setActivityTitle("Edit Photo")
            .setAspectRatio(1,1)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAutoZoomEnabled(true)
            .setAllowFlipping(false)
            .getIntent(this)
        startForResult.launch(intent)
    }
}
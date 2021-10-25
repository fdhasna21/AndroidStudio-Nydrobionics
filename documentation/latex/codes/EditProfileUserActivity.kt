class EditProfileUserActivity : AppCompatActivity(), View.OnClickListener, SegmentedButtonGroup.OnPositionChangedListener, TextWatcher {
    private lateinit var binding : ActivityEditProfileUserBinding
    private lateinit var bindingFragment : FragmentCreateUserBinding
    private lateinit var viewModel : CreateProfileViewModel
    private lateinit var utility: ViewUtility
    private lateinit var viewsAsButton : ArrayList<View>
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private var strEdt : HashMap<String, TextInputEditText> = hashMapOf()

    companion object {
        const val TAG = "editProfileUserActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)
        viewModel.setCurrentUser(intent.getParcelableExtra<UserModel>(BuildConfig.CURRENT_USER))
        supportActionBar?.title = getString(R.string.edit_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        bindingFragment = binding.editProfileFragment
        bindingFragment.apply {
            setupDefaultData()
            editTexts = arrayListOf(
                createUserName,
                createUserPhone,
                createUserAddress,
                createUserDOB,
                createUserBio)
            viewsAsButton = arrayListOf(createUserDOB,
                                        createUserSubmit,
                                        createUserEditPhoto,
                                        createUserPhoto)
            utility = ViewUtility(
                context = this@EditProfileUserActivity,
                circularProgressButton = createUserSubmit,
                textInputEditTexts = editTexts,
                viewsAsButton = viewsAsButton,
                actionBar = supportActionBar
            )

            createUserGender.onPositionChangedListener = this@EditProfileUserActivity
            editTexts.forEach { it.addTextChangedListener(this@EditProfileUserActivity) }
            viewsAsButton.forEach { it.setOnClickListener(this@EditProfileUserActivity) }
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
                else -> super.onBackPressed()
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
            bindingFragment.createUserEditPhoto -> utility.openEditPhoto(bindingFragment.createUserPhoto, ProfileType.USER)
            bindingFragment.createUserPhoto -> utility.openImage(bindingFragment.createUserPhoto)
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
                utility.isLoading = true
                viewModel.createUserProfile(
                    bindingFragment.createUserName.text.toString(),
                    bindingFragment.createUserPhone.text.toString(),
                    bindingFragment.createUserAddress.text.toString(),
                    bindingFragment.createUserBio.text.toString()
                )
                viewModel.isUserCreated.observe(this, {
                    if(it){
                        utility.isLoading = false
                        Toast.makeText(this, "Profile updated.", Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                        finish()
                    } else {
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
        checkUpdate()
    }

    private fun setupDefaultData(){
        bindingFragment.apply {
            createUserRoleGroup.visibility = View.GONE
            createUserSubmit.text = getString(R.string.save)

            viewModel.getCurrentUserModel()?.let {
                createUserEmail.setText(Firebase.auth.currentUser?.email)
                createUserName.setText(it.name ?: "")
                createUserPhone.setText(it.phone ?: "")
                createUserAddress.setText(it.address ?: "")
                createUserBio.setText(it.bio ?: "")
                createUserDOB.setText(it.dob ?: "")
                Glide.with(this@EditProfileUserActivity)
                    .load(it.photo_url ?: R.drawable.bg_farmer)
                    .centerCrop()
                    .into(createUserPhoto)
                createUserGender.setPosition(Gender.getType(it.gender!!)!!.getPosition(), false)

                strEdt[it.name ?: ""] = createUserName
                strEdt[it.phone ?: ""] = createUserPhone
                strEdt[it.address ?: ""] = createUserAddress
                strEdt[it.bio ?: ""] = createUserBio
                strEdt[it.dob ?: ""] = createUserDOB
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkUpdate()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkUpdate() {
        viewModel.checkNotEmpty(
            utility.isEmpties(editTexts) &&
                    (utility.isChanges(strEdt) ||
                    viewModel.getUpdateGender() != bindingFragment.createUserGender.position ||
                    viewModel.getPhotoProfile().value != null)
        ).observe(this, {
            bindingFragment.createUserSubmit.isEnabled = it
        })
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
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
}
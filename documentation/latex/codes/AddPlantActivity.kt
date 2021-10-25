class AddPlantActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {
    private lateinit var binding : ActivityAddPlantBinding
    private lateinit var viewModel : AddPlantViewModel
    private lateinit var utility: ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var tooltips : ArrayList<ImageButton>
    private lateinit var numberPickers : ArrayList<ClickNumberPickerView>
    private lateinit var viewsAsButton : ArrayList<View>
    private var strEdt : HashMap<String, TextInputEditText> = hashMapOf()

    companion object{
        const val TAG = "addPlantActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddPlantViewModel::class.java)
        viewModel.setCurrentData(intent.getParcelableExtra<PlantModel>(BuildConfig.SELECTED_PLANT))

        supportActionBar?.title = if(intent.getParcelableExtra<PlantModel>(BuildConfig.SELECTED_PLANT) == null) {
            getString(R.string.add_new_plant)
        } else {
            getString(R.string.edit_plant)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            tooltips = arrayListOf(apGrowthInfo,
                                  apTempInfo,
                                  apHumidInfo)
            viewsAsButton = arrayListOf(addPlantPhoto, addPlantEditPhoto, addPlantSubmit)
            editTexts = arrayListOf(addPlantName, addPlantDescription)
            numberPickers = arrayListOf(addPlantGrowthTime,
                addPlantTempMin,
                addPlantTempMax,
                addPlantHumidMin,
                addPlantHumidMax,
                addPlantAcidMin,
                addPlantAcidMax)
            utility = ViewUtility(
                context = this@AddPlantActivity,
                circularProgressButton = addPlantSubmit,
                textInputEditTexts = editTexts,
                viewsAsButton = viewsAsButton,
                numberPickers = numberPickers,
                actionBar = supportActionBar)

            tooltips.forEach {
                it.setOnClickListener {
                    it.performLongClick()
                }
            }
            //todo : tooltip

            viewsAsButton.forEach { it.setOnClickListener(this@AddPlantActivity) }
            addPlantPhoto.setOnLongClickListener { addPlantEditPhoto.performClick() }
            editTexts.forEach { it.addTextChangedListener(this@AddPlantActivity) }
            numberPickers.forEachIndexed { i, view ->
                val numberPickerDataType = getNumberPickerType(view)
                viewModel.getNumberPickerValue(numberPickerDataType)?.observe(this@AddPlantActivity,{
                    if(it != view.value){
                        view.setPickerValue(it)
                    }
                })

                numberPickers[i].setClickNumberPickerListener { previousValue, currentValue, pickerClickType ->
                    viewModel.setNumberPickerValue(currentValue, numberPickerDataType)
                    checkEmpty()
                }
            }

            viewModel.getPhotoProfile().observe(this@AddPlantActivity, {
                it?.let {
                    Glide.with(this@AddPlantActivity)
                        .load(it.uri)
                        .circleCrop()
                        .into(addPlantPhoto)
                }
            })

            viewModel.getCurrentPlant().observe(this@AddPlantActivity, { it ->
                it.plantId?.let { plantId ->
                    supportActionBar?.title = it.name
                    addPlantName.setText(it.name)
                    addPlantDescription.setText(it.description)
                    addPlantGrowthTime.setPickerValue(it.growthTime?.toFloat() ?: 0f)
                    addPlantTempMin.setPickerValue(it.tempLv?.min ?: 0f)
                    addPlantTempMax.setPickerValue(it.tempLv?.max ?: 0f)
                    addPlantHumidMin.setPickerValue(it.humidLv?.min ?: 0f)
                    addPlantHumidMax.setPickerValue(it.humidLv?.max ?: 0f)
                    addPlantAcidMin.setPickerValue(it.phLv?.min ?: 0f)
                    addPlantAcidMax.setPickerValue(it.phLv?.max ?: 0f)

                    Glide.with(this@AddPlantActivity)
                        .load(it.photo_url ?: R.drawable.bg_plant)
                        .circleCrop()
                        .into(addPlantPhoto)

                    if (it.userId != Firebase.auth.uid){
                        addPlantSubmit.visibility = View.GONE
                        utility.isLoading = true
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                    }
                    Log.i(TAG, "onCreate: ${it.userId} ${Firebase.auth.uid}")

                    strEdt[it.name ?: ""] = addPlantName
                    strEdt[it.description ?: ""] = addPlantDescription
                }
            })

            checkEmpty()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if(intent.getParcelableExtra<PlantModel>(BuildConfig.SELECTED_PLANT) != null &&
                viewModel.getCurrentPlant().value?.userId == Firebase.auth.uid){
            viewModel.isNotEmpties.observe(this, {
                when(it){
                    true -> binding.addPlantSubmit.performClick()
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty(){
        val currGrowthTime : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantGrowthTime))?.value ?: 0f
        val currTempMin : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantTempMin))?.value
        val currTempMax : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantTempMax))?.value
        val currHumidMin : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantHumidMin))?.value
        val currHumidMax : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantHumidMax))?.value
        val currAcidMin : Float? = viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantAcidMin))?.value
        val currAcidMax : Float? = viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantAcidMax))?.value
        val hashMap : HashMap<Float?, Float?> = hashMapOf()
        hashMap[currTempMin] = currTempMax
        hashMap[currHumidMin] = currHumidMax
        hashMap[currAcidMin] = currAcidMax

        if(intent.getParcelableExtra<PlantModel>(BuildConfig.SELECTED_PLANT) != null){
            viewModel.getCurrentPlant().value?.let {
                val floNumPick: HashMap<Float, Float> = hashMapOf()
                floNumPick[it.growthTime?.toFloat() ?: 0f] = currGrowthTime
                floNumPick[it.tempLv?.min ?: 0f] = currTempMin ?: 0f
                floNumPick[it.tempLv?.max ?: 0f] = currTempMax ?: 0f
                floNumPick[it.humidLv?.min ?: 0f] = currHumidMin ?: 0f
                floNumPick[it.humidLv?.max ?: 0f] = currHumidMax ?: 0f
                floNumPick[it.phLv?.min ?: 0f] = currAcidMin ?: 0f
                floNumPick[it.phLv?.max ?: 0f] = currAcidMax ?: 0f

                viewModel.checkNotEmpty(utility.isEmpties(editTexts) &&
                            currGrowthTime > 0f &&
                            utility.isInRanges(hashMap) &&
                            (utility.isChanges(strEdt) || utility.isNumberPickerChanges(floNumPick))
                ).observe(this@AddPlantActivity, {
                    binding.addPlantSubmit.isEnabled = it
                })
                Log.i(TAG, "checkEmpty: ${utility.isEmpties(editTexts)} ${currGrowthTime > 0f} ${utility.isInRanges(hashMap)}" +
                        " ${utility.isChanges(strEdt)} ${utility.isNumberPickerChanges(floNumPick)}")
            }
        } else {
            viewModel.checkNotEmpty(
                utility.isEmpties(editTexts) &&
                        currGrowthTime > 0f &&
                        utility.isInRanges(hashMap)
            ).observe(this, {
                binding.addPlantSubmit.isEnabled = it
            })
        }
    }

    private fun getNumberPickerType(view: ClickNumberPickerView) : NumberPickerType?{
        return when (view) {
            binding.addPlantGrowthTime -> NumberPickerType.GROWTH_TIME
            binding.addPlantTempMin -> NumberPickerType.TEMP_MIN
            binding.addPlantTempMax -> NumberPickerType.TEMP_MAX
            binding.addPlantHumidMin -> NumberPickerType.HUMID_MIN
            binding.addPlantHumidMax -> NumberPickerType.HUMID_MAX
            binding.addPlantAcidMin -> NumberPickerType.ACID_MIN
            binding.addPlantAcidMax -> NumberPickerType.ACID_MAX
            else -> null
        }
    }

    override fun onClick(v: View?) {
        when(v){
            binding.addPlantPhoto -> utility.openImage(binding.addPlantPhoto)
            binding.addPlantEditPhoto -> utility.openEditPhoto(imageView = binding.addPlantPhoto, profileType = ProfileType.PLANT)
            binding.addPlantSubmit -> {
                utility.isLoading = true
                viewModel.createPlant(binding.addPlantName.text.toString(),
                    binding.addPlantDescription.text.toString())
                viewModel.isPlantAdd.observe(this@AddPlantActivity, {
                    if(it){
                        utility.isLoading = false
                        if(intent.getStringExtra("from") != null){
                            val intent = Intent()
                            intent.putExtra(BuildConfig.SELECTED_PLANT, viewModel.plantModel.value)
                            setResult(RESULT_OK, intent)
                        } else {
                            val toastTxt = if(intent.getParcelableExtra<PlantModel>(BuildConfig.SELECTED_PLANT) != null){
                                "Plant updated."
                            } else {
                                "New plant added."
                            }
                            Toast.makeText(this, toastTxt, Toast.LENGTH_SHORT).show()
                            super.onBackPressed()
                            finish()
                        }
                    } else {
                        utility.isLoading = false
                        viewModel.addPlantError.observe(this@AddPlantActivity, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this@AddPlantActivity, it, Toast.LENGTH_SHORT).show()
                                Log.i(CreateFarmFragment.TAG, it)
                                viewModel.addPlantError.value = ""
                            }
                        })
                    }
                })
            }
        }
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        Log.i(CreateProfileActivity.TAG, "$data")
        try{
            if(data?.resultCode == Activity.RESULT_OK){
                CropImage.getActivityResult(data.data)?.let{
                    val mimeTypeMap = MimeTypeMap.getSingleton()
                    val fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(it.uriContent!!))
                    viewModel.setPhotoPlant(it.uriContent!!, fileExtension)
                }
            }

        } catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
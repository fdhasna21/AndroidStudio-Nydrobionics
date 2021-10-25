class AddKitActivity : AppCompatActivity(), TextWatcher{
    private lateinit var binding : ActivityAddKitBinding
    private lateinit var viewModel : AddKitViewModel
    private lateinit var utility : ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var tooltips : ArrayList<ImageButton>
    private lateinit var numberPickers : ArrayList<ClickNumberPickerView>
    private var strEdt : HashMap<String, TextInputEditText> = hashMapOf()

    companion object {
        const val TAG = "addKitActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddKitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddKitViewModel::class.java)
        viewModel.setCurrentData(intent.getParcelableExtra<UserModel>(BuildConfig.CURRENT_USER),
            intent.getParcelableExtra<FarmModel>(BuildConfig.CURRENT_FARM),
            intent.getParcelableExtra<KitModel>(BuildConfig.SELECTED_KIT))

        supportActionBar?.title = viewModel.getCurrentKit().value?.name ?: getString(R.string.create_new_kit)
        supportActionBar?.subtitle = viewModel.getCurrentFarm().value?.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            setupDefaultData()
            editTexts = arrayListOf(addKitName, addKitPosition)
            tooltips = arrayListOf(akSizeInfo,
                                  akWaterLevelInfo,
                                  akNutrientLevelInfo,
                                  akTurbidityLevelInfo)
            numberPickers = arrayListOf(addKitWidth,
                addKitLength,
                addKitWaterMin,
                addKitWaterMax,
                addKitNutrientMin,
                addKitNutrientMax,
                addKitTurbidityMin,
                addKitTurbidityMax)
            utility = ViewUtility(context = this@AddKitActivity,
                circularProgressButton = addKitSubmit,
                textInputEditTexts = editTexts,
                numberPickers = numberPickers,
                actionBar = supportActionBar)

            tooltips.forEach {
                it.setOnClickListener {
                    it.performLongClick()
                }
            }
            akSizeInfo.tooltipText = "How many holes on each side length and width (must more than 0)."
            akWaterLevelInfo.tooltipText = getString(R.string.level_tooltip)
            akNutrientLevelInfo.tooltipText = getString(R.string.level_tooltip)
            akTurbidityLevelInfo.tooltipText = "belum ditentuin"
            //todo : turbidity tooltip

            editTexts.forEach { it.addTextChangedListener(this@AddKitActivity) }
            numberPickers.forEachIndexed { i, view ->
                val numberPickerDataType = getNumberPickerType(view)
                viewModel.getNumberPickerValue(numberPickerDataType)?.observe(this@AddKitActivity,{
                    if(it != view.value){
                        view.setPickerValue(it)
                    }
                })

                numberPickers[i].setClickNumberPickerListener { previousValue, currentValue, pickerClickType ->
                    viewModel.setNumberPickerValue(currentValue, numberPickerDataType)
                    checkEmpty()
                }
            }

            addKitSubmit.setOnClickListener {
                utility.isLoading = true
                viewModel.createKit(binding.addKitName.text.toString(),
                                    binding.addKitPosition.text.toString())
                viewModel.isKitAdd.observe(this@AddKitActivity, {
                    if(it){
                        utility.isLoading = false
                        val toastTxt = if(intent.getParcelableExtra<NoteModel>(BuildConfig.SELECTED_NOTE) != null){
                            "Kit updated."
                        } else {
                            "New kit created."
                        }
                        Toast.makeText(this@AddKitActivity, toastTxt, Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                        finish()
                    } else {
                        utility.isLoading = false
                        viewModel.addKitError.observe(this@AddKitActivity, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this@AddKitActivity, it, Toast.LENGTH_SHORT).show()
                                Log.i(CreateFarmFragment.TAG, it)
                                viewModel.addKitError.value = ""
                            }
                        })
                    }
                })
            }
            checkEmpty()
        }
    }

    private fun setupDefaultData(){
        binding.apply {
            viewModel.getCurrentKit().observe(this@AddKitActivity, { it ->
                it.kitId?.let { kitId ->
                    addKitName.setText(it.name)
                    addKitPosition.setText(it.position)
                    addKitWidth.setPickerValue(it.width?.toFloat() ?: 0f)
                    addKitLength.setPickerValue(it.length?.toFloat() ?: 0f)
                    addKitWaterMin.setPickerValue(it.waterLv?.min ?: 0f)
                    addKitWaterMax.setPickerValue(it.waterLv?.max ?: 0f)
                    addKitNutrientMin.setPickerValue(it.nutrientLv?.min ?: 0f)
                    addKitNutrientMax.setPickerValue(it.nutrientLv?.max ?: 0f)
                    addKitTurbidityMin.setPickerValue(it.turbidityLv?.min ?: 0f)
                    addKitTurbidityMax.setPickerValue(it.turbidityLv?.max ?: 0f)

                    strEdt[it.name ?: ""] = addKitName
                    strEdt[it.position ?: ""] = addKitPosition
                }
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if(intent.getParcelableExtra<KitModel>(BuildConfig.SELECTED_KIT) != null){
            viewModel.isNotEmpties.observe(this, {
                when(it){
                    true -> binding.addKitSubmit.performClick()
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
        val currWidth : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitWidth))?.value ?: 0f
        val currLength : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitLength))?.value ?: 0f
        val currWaterMin : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitWaterMin))?.value
        val currWaterMax : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitWaterMax))?.value
        val currNutrientMin : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitNutrientMin))?.value
        val currentNutrientMax : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitNutrientMax))?.value
        val currentTurbidityMin : Float? = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitTurbidityMin))?.value
        val currentTurbidityMax : Float? = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitTurbidityMax))?.value
        val hashMap : HashMap<Float?, Float?> = hashMapOf()
        hashMap[currWaterMin] = currWaterMax
        hashMap[currNutrientMin] = currentNutrientMax
        hashMap[currentTurbidityMin] = currentTurbidityMax

        if(intent.getParcelableExtra<KitModel>(BuildConfig.SELECTED_KIT) != null){
            viewModel.getCurrentKit().value?.let {
                val floNumPick : HashMap<Float, Float> = hashMapOf()
                floNumPick[it.width?.toFloat() ?: 0f] = currWidth
                floNumPick[it.length?.toFloat() ?: 0f] = currLength
                floNumPick[it.waterLv?.min ?: 0f] = currWaterMin ?:0f
                floNumPick[it.waterLv?.max ?: 0f] = currWaterMax ?:0f
                floNumPick[it.nutrientLv?.min ?: 0f] = currNutrientMin ?:0f
                floNumPick[it.nutrientLv?.max ?: 0f] = currentNutrientMax ?:0f
                floNumPick[it.turbidityLv?.min ?: 0f] = currentTurbidityMin ?:0f
                floNumPick[it.turbidityLv?.max ?: 0f] = currentTurbidityMax ?:0f

                viewModel.checkNotEmpty(utility.isEmpties(editTexts) &&
                        currWidth > 0f &&
                        currLength > 0f &&
                        utility.isInRanges(hashMap) &&
                        (utility.isChanges(strEdt) || utility.isNumberPickerChanges(floNumPick))
                ).observe(this@AddKitActivity, {
                    binding.addKitSubmit.isEnabled = it
                })

                Log.i(
                    TAG,
                    "checkEmpty: ${utility.isEmpties(editTexts)} ${currWidth > 0f} ${currLength > 0f} ${utility.isInRanges(hashMap)} ${utility.isChanges(strEdt)} ${utility.isNumberPickerChanges(floNumPick)}"
                )
            }
        } else {
            viewModel.checkNotEmpty(
                utility.isEmpties(editTexts) &&
                        currWidth > 0f &&
                        currLength > 0f &&
                        utility.isInRanges(hashMap)
            ).observe(this@AddKitActivity, {
                binding.addKitSubmit.isEnabled = it
            })
        }
    }

    private fun getNumberPickerType(view: ClickNumberPickerView) : NumberPickerType?{
        return when (view) {
            binding.addKitWidth -> NumberPickerType.KIT_WIDTH
            binding.addKitLength -> NumberPickerType.KIT_LENGTH
            binding.addKitWaterMin -> NumberPickerType.WATER_MIN
            binding.addKitWaterMax -> NumberPickerType.WATER_MAX
            binding.addKitNutrientMin -> NumberPickerType.NUTRIENT_MIN
            binding.addKitNutrientMax -> NumberPickerType.NUTRIENT_MAX
            binding.addKitTurbidityMin -> NumberPickerType.TURBIDITY_MIN
            binding.addKitTurbidityMax -> NumberPickerType.TURBIDITY_MAX
            else -> null
        }
    }
}
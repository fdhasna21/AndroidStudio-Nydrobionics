class AddCropsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityAddCropsBinding
    private lateinit var viewModel : AddDataMonitoringViewModel
    private lateinit var utility: ViewUtility
    private lateinit var viewsAsButton : ArrayList<View>

    companion object{
        const val TAG = "addCrops"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCropsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddDataMonitoringViewModel::class.java)
        viewModel.setCurrentData(intent.getParcelableExtra<UserModel>(BuildConfig.CURRENT_USER),
            intent.getParcelableExtra<FarmModel>(BuildConfig.CURRENT_FARM),
            isAddCrops = true)
        supportActionBar?.title = getString(R.string.add_new_crops)
        supportActionBar?.subtitle = viewModel.currentFarmModel.value?.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            viewsAsButton = arrayListOf(acSelectPlant, acNewPlant, acCropsSubmit)
            viewsAsButton.forEach { it.setOnClickListener(this@AddCropsActivity) }

            utility = ViewUtility(
                context = this@AddCropsActivity,
                circularProgressButton = acCropsSubmit,
                viewsAsButton = viewsAsButton,
                actionBar = supportActionBar
            )

            viewModel.getKitSelector().observe(this@AddCropsActivity, {
                it?.let{
                    val adapter = ArrayAdapter(this@AddCropsActivity, R.layout.row_item_list, it)
                    (acSelectKit.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                    Log.i(TAG, "${adapter.count} ${it}")

                    (acSelectKit.editText as? AutoCompleteTextView)?.setOnItemClickListener { parent, view, position, id ->
                        viewModel.setCurrentKit(position)
                        checkEmpty()
                    }
                }
            })

            viewModel.getCurrentPlant().observe(this@AddCropsActivity, {
                if(it != null){
                    try{
                        val estimated : String = utility.formatDateToString(utility.formatStringToDate(est = it.growthTime!!)).toString()
                        acPlantName.setText(it.name)
                        acPlantDescription.setText(it.description)
                        acPlantGrowth.setText(getString(R.string.d_days, it.growthTime))
                        acPlantEstimated.setText(estimated)
                        acPlantTempMin.setText(getString(R.string.temp_value, it.tempLv?.min))
                        acPlantTempMax.setText(getString(R.string.temp_value, it.tempLv?.max))
                        acPlantHumidMin.setText(getString(R.string.humid_value, it.humidLv?.min))
                        acPlantHumidMax.setText(getString(R.string.humid_value, it.humidLv?.max))
                        acPlantAcidMin.setText(it.phLv?.min.toString())
                        acPlantAcidMax.setText(it.phLv?.max.toString())
                        Glide.with(this@AddCropsActivity)
                            .load(it.photo_url)
                            .circleCrop()
                            .into(acPlantPhoto)
                    } catch (e:Exception){
                        Log.e(AddDataMonitoringActivity.TAG, "cannot parse plant data", e)
                    }
                } else {
                    acPlantSelector.visibility = View.VISIBLE
                    acCropsContent.visibility = View.GONE
                }
                checkEmpty()
            })
            checkEmpty()
        }
    }

    private fun checkEmpty() {
        viewModel.checkNotEmpty(
            viewModel.getCurrentKit().value != null &&
                    viewModel.getCurrentPlant().value != null
        ).observe(this, {
            binding.acCropsSubmit.isEnabled = it
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        finish()
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
            binding.acNewPlant -> {
                val intent = Intent(this, AddPlantActivity::class.java)
                intent.putExtra("from", TAG)
                startForResult.launch(intent)
            }
            binding.acSelectPlant -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("search", TAG)
                startForResult.launch(intent)
            }
            binding.acCropsSubmit -> {
                utility.isLoading = true
                viewModel.addCrops()
                viewModel.isCropsAdd.observe(this, {
                    if(it){
                        utility.isLoading = false
                        Toast.makeText(this, "New crops added", Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                        finish()
                    } else {
                        utility.isLoading = false
                        viewModel.addCropsError.observe(this, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                                Log.i(CreateFarmFragment.TAG, it)
                                viewModel.addCropsError.value = ""
                            }
                        })
                    }
                })
            }
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        Log.i(CreateProfileActivity.TAG, "$data")
        try{
            if(data?.resultCode == Activity.RESULT_OK){
                data.data?.getParcelableExtra<PlantModel>(BuildConfig.SELECTED_PLANT)?.let {
                    binding.acPlantSelector.visibility = View.GONE
                    binding.acCropsContent.visibility = View.VISIBLE
                    viewModel.setCurrentPlant(it)
                }
            }
        } catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}
class EditProfileFarmActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private lateinit var binding : ActivityEditProfileFarmBinding
    private lateinit var bindingFragment : FragmentCreateFarmBinding
    private lateinit var viewModel : CreateProfileViewModel
    private lateinit var viewsAsButton : ArrayList<View>
    private lateinit var utility: ViewUtility
    private lateinit var editTexts : java.util.ArrayList<TextInputEditText>
    private var strEdt : HashMap<String, TextInputEditText> = hashMapOf()

    companion object {
        const val TAG = "editProfileFarm"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileFarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)
        viewModel.setCurrentFarm(intent.getParcelableExtra<FarmModel>(BuildConfig.CURRENT_FARM),
                                 intent.getParcelableExtra<UserModel>(BuildConfig.CURRENT_USER))
        supportActionBar?.title = getString(R.string.farm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        bindingFragment = binding.editFarmFragment
        bindingFragment.apply {
            setupDefaultData()
            viewsAsButton = arrayListOf(esAddStaff, createFarmSubmit)
            editTexts = arrayListOf(
                createFarmName,
                createFarmLoc,
                createFarmDesc
            )
            utility = ViewUtility(
                context = this@EditProfileFarmActivity,
                circularProgressButton = createFarmSubmit,
                textInputEditTexts = editTexts,
                viewsAsButton = viewsAsButton,
                actionBar = supportActionBar
            )

            viewsAsButton.forEach { it.setOnClickListener(this@EditProfileFarmActivity) }
            editTexts.forEach { it.addTextChangedListener(this@EditProfileFarmActivity) }
            setupRecyclerView()
            checkUpdate()
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setupDefaultData() {
        bindingFragment.apply {
            createFarmSubmit.text = getString(R.string.save)
            editStaff.visibility = View.VISIBLE

            viewModel.getCurrentFarmModel()?.let {
                createFarmName.setText(it.name ?: "")
                createFarmDesc.setText(it.description ?: "")
                createFarmLoc.setText(it.location ?: "")

                strEdt[it.name ?: ""] = createFarmName
                strEdt[it.description ?: ""] = createFarmDesc
                strEdt[it.location ?: ""] = createFarmLoc
            }
        }
    }

    private fun setupRecyclerView(){
        val data : ArrayList<UserModel> = arrayListOf()
        val rowAdapter = AdapterType.SEARCH_USER.getAdapter(this, data, type = AdapterType.Companion.SearchSelectType.SELECT)
        viewModel.updateStaff()
        viewModel.getStaff().observe(this, {
            data.clear()
            data.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
            (rowAdapter as UserModelAdapter).setOnItemClickListener(
                object : UserModelAdapter.OnItemClickListener{
                    override fun onItemClicked(
                        userModel: UserModel,
                        position: Int,
                        itemView: View,
                        v: RowItemSearchBinding
                    ) {
                        when(itemView){
                            v.searchRoot -> {
                                val intent = Intent(this@EditProfileFarmActivity, ProfileUserActivity::class.java)
                                intent.putExtra(BuildConfig.SELECTED_USER, userModel)
                                startActivity(intent)
                            }
                            v.searchClose -> viewModel.removeStaff(position)
                        }
                    }

                }
            )
        })
        bindingFragment.esRecyclerView.adapter = rowAdapter
        bindingFragment.esRecyclerView.layoutManager = LinearLayoutManager(this)
        bindingFragment.esRecyclerView.addItemDecoration(object : DividerItemDecoration(this, VERTICAL) {})
        bindingFragment.esRecyclerView.setHasFixedSize(true)
    }

    private fun checkUpdate(){
        viewModel.checkNotEmpty(
            utility.isChanges(strEdt) && utility.isEmpties(editTexts)
        ).observe(this, {
            bindingFragment.createFarmSubmit.isEnabled = it
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        viewModel.isNotEmpties.observe(this, {
            when(it){
                true -> bindingFragment.createFarmSubmit.performClick()
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
            bindingFragment.esAddStaff -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("search", TAG)
                intent.putExtra(BuildConfig.EXCEPT_USERS, viewModel.getStaff().value)
                startForResult.launch(intent)
            }
            bindingFragment.createFarmSubmit -> {
                utility.isLoading = true
                viewModel.createFarmProfile(bindingFragment.createFarmName.text.toString(),
                    bindingFragment.createFarmDesc.text.toString(),
                    bindingFragment.createFarmLoc.text.toString())
                viewModel.isFarmCreated.observe(this, {
                    if(it){
                        utility.isLoading = false
                        Toast.makeText(this, "Farm updated.", Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                    } else {
                        utility.isLoading = false
                        viewModel.createProfileError.observe(this, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                                Log.i(CreateFarmFragment.TAG, it)
                                viewModel.createProfileError.value = ""
                            }
                        })
                    }
                })
//                viewModel.createStaff()
//                viewModel.isStaffAdded.observe(this, {
//                    if(it){
//                        utility.isLoading = false
//                        Toast.makeText(this, "Farm updated", Toast.LENGTH_SHORT).show()
//                        super.onBackPressed()
//                        finish()
//                    }
//                })
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //todo posisi awal sesuai dari data starter
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        Log.i(CreateProfileActivity.TAG, "$data")
        try{
            if(data?.resultCode == Activity.RESULT_OK){
                data.data?.getParcelableExtra<UserModel>(BuildConfig.SELECTED_USER)?.let {
                    viewModel.addStaff(it)
                    checkUpdate()
                }
            }

        } catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkUpdate()
    }

    override fun afterTextChanged(s: Editable?) {}
}
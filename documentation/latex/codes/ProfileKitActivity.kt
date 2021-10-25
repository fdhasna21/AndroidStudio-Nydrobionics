class ProfileKitActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileKitBinding
    lateinit var viewModel : ProfileKitViewModel

    companion object {
        const val TAG = "kitProfileActivity"
        val TAB_LAYOUT = arrayListOf<String>("Overview", "Data Monitoring", "Crops")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileKitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ProfileKitViewModel::class.java)
        viewModel.setCurrentKit(intent.getParcelableExtra<UserModel>(BuildConfig.CURRENT_USER),
            intent.getParcelableExtra<FarmModel>(BuildConfig.CURRENT_FARM),
            intent.getParcelableExtra<KitModel>(BuildConfig.SELECTED_KIT))
        supportActionBar?.title = getString(R.string.hydroponic_kit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.elevation = 0f

        val tabLayoutAdapter = object : FragmentStateAdapter(supportFragmentManager, lifecycle){
            override fun getItemCount(): Int {
                return TAB_LAYOUT.size
            }

            override fun createFragment(position: Int): Fragment {
                return when(position){
                    1 -> KitMonitoringFragment()
                    2 -> KitCropsFragment()
                    else -> KitOverviewFragment()
                }
            }
        }

        binding.profileKitViewPager.adapter = tabLayoutAdapter
        TabLayoutMediator(binding.profileKitTabLayout, binding.profileKitViewPager) { tab, position ->
            tab.text = TAB_LAYOUT[position]
        }.attach()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile_kit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.kitSettings -> {
                val intent = Intent(this, AddKitActivity::class.java)
                intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUser().value)
                intent.putExtra(BuildConfig.CURRENT_FARM, viewModel.getCurrentFarm().value)
                intent.putExtra(BuildConfig.SELECTED_KIT, viewModel.getCurrentKit().value)
                startActivity(intent)
            }
            R.id.kitHistory -> startActivity(Intent(this, ShowRecyclerActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
class MainActivity : AppCompatActivity(), View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener,
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel : MainViewModel
    private lateinit var navHostFragment : NavHostFragment
    private lateinit var navController : NavController
    private lateinit var actionBarToggle : ActionBarDrawerToggle
    lateinit var swipeRefresh: SwipeRefreshLayout
    lateinit var drawerLayout : DrawerLayout

    companion object{
        const val TAG = "mainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.setCurrentData(
            intent.getParcelableExtra<UserModel>(BuildConfig.CURRENT_USER),
            intent.getParcelableExtra<FarmModel>(BuildConfig.CURRENT_FARM))

        navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        binding.apply {
            mainBottomNavigation.setupWithNavController(navController)
            mainVersion.text = BuildConfig.VERSION_NAME
            swipeRefresh = mainSwipeRefresh
            mainAddFragment.setOnClickListener(this@MainActivity)
            mainSwipeRefresh.setOnRefreshListener(this@MainActivity)
            mainDrawerNavigation.setNavigationItemSelectedListener(this@MainActivity)
        }

        setupDrawer()
    }

    private fun setupDrawer() {
        drawerLayout = binding.mainDrawerLayout
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0,0)
        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()

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
            binding.mainAddFragment -> MainAddFragment().show(supportFragmentManager, TAG+"FAB")
        }
    }

    override fun onRefresh() {
        when(binding.mainBottomNavigation.selectedItemId){
            R.id.mainHomeFragment -> viewModel.refreshFarm()
            R.id.mainSocialFragment -> viewModel.refreshPosts()
            R.id.mainNotesFragment -> viewModel.refreshNotes()
            R.id.mainProfileFragment -> {}
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.drawer_edit -> {
                val intent = Intent(this, EditProfileUserActivity::class.java)
                intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUser().value)
                Log.i(TAG, "onNavigationItemSelected: ${viewModel.getCurrentUser().value}")
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.END)
                true
            }
            R.id.drawer_signout -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.sign_out))
                    .setMessage(getString(R.string.sign_out_warning))
                    .setPositiveButton(getString(R.string.sign_out)){ _,_ ->
                        val uid = Firebase.auth.uid!!
                        viewModel.signOut()
                        viewModel.isUserSignOut.observe(this@MainActivity, {
                            it?.let {
                                if(it){
                                    drawerLayout.closeDrawer(GravityCompat.END)
                                    DatabaseHandler(this@MainActivity).signOut(uid)
                                    startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                                    finish()
                                } else {
//                                    dialogButton.forEach { it.isEnabled = true }
                                    viewModel.signOutError.observe(this@MainActivity, {
                                        it?.let {
                                            if (it.isNotEmpty()) {
                                                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                                                Log.i(TAG, it)
                                                viewModel.signOutError.value = null
                                            }
                                        }
                                    })
                                }
                            }

                        })
                    }
                    .setNegativeButton(getString(R.string.cancel)){_,_ ->}
                    .create()
                    .show()
                true
            }
            R.id.drawer_feedback -> {
                val intent = Intent(this, FeedbackActivity::class.java)
                intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUser().value)
                startActivity(intent)
                drawerLayout.closeDrawer(GravityCompat.END)
                true
            }
            R.id.drawer_info -> {
                IntentUtility(this).openAppInfo()
                drawerLayout.closeDrawer(GravityCompat.END)
                true
            }
            R.id.drawer_language ->{
                IntentUtility(this).openLanguageSettings()
                drawerLayout.closeDrawer(GravityCompat.END)
                true
            }
            R.id.drawer_about -> {
                startActivity(Intent(this, AboutMeActivity::class.java))
                drawerLayout.closeDrawer(GravityCompat.END)
                true
            }
            R.id.drawer_my_post -> {
                val intent = Intent(this, ShowRecyclerActivity::class.java)
                intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUser().value)
                intent.putExtra(BuildConfig.ALL_PLANTS, viewModel.getAllPosts().value)
                startActivity(intent)
                true
            }
            else -> false
        }
    }
}
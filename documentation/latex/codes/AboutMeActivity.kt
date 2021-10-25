class AboutMeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutMeBinding
    private var intentData = IntentUtility(this)

    private fun setupToolbar(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = getString(R.string.about_me)
    }

    private fun setupHeader(){
        binding.aboutEmail.text = BuildConfig.CREATOR_EMAIL
        binding.aboutDescription.text = listOf(getString(R.string.tab), getString(R.string.profile_description)).joinToString(" ")
        Glide.with(this)
            .load(R.drawable.profile_photo)
            .circleCrop()
            .into(binding.profilePicture)
    }

    private fun setupFindMe(){
        binding.aboutFindMe.itemIconTintList = null
        binding.aboutFindMe.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.about_whatsapp -> {
                    try{
                        val intent = Intent()
                        applicationContext.packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)
                        intent.action = Intent.ACTION_VIEW
                        intent.type = "text/plain"
                        intent.data = Uri.parse(BuildConfig.CREATOR_WHATSAPP)
                        intent.setPackage("com.whatsapp")
                        startActivity(intent)
                    } catch (e : PackageManager.NameNotFoundException) {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                    }
                    true }
                R.id.about_email ->{
                    intentData.openEmail(BuildConfig.CREATOR_EMAIL)
                    true }
                R.id.about_github -> {
                    intentData.openBrowser(BuildConfig.CREATOR_GITHUB)
                    true }
                R.id.about_project -> {
                    intentData.openBrowser(BuildConfig.PROJECT_URL)
                    true }
                else -> false
            }
        }
    }

    private fun setupCredit() {
        binding.aboutCredit.itemIconTintList = null
        binding.aboutCredit.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.about_lottie -> intentData.openBrowser("https://lottiefiles.com/45869-farmers")
                R.id.about_freepik -> intentData.openBrowser("https://www.freepik.com/")
            }
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutMeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupHeader()
        setupFindMe()
        setupCredit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
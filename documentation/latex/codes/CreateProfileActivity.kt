class CreateProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateProfileBinding
    lateinit var viewModel : CreateProfileViewModel

    companion object{
        const val TAG = "createProfile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)
        viewModel.setCurrentUser(intent.getParcelableExtra<UserModel>(BuildConfig.CURRENT_USER))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        Log.i(TAG, "$data")
        try{
            if(data?.resultCode == Activity.RESULT_OK){
                CropImage.getActivityResult(data.data)?.let{
                    val mimeTypeMap = MimeTypeMap.getSingleton()
                    val fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(it.uriContent!!))
                    viewModel.setPhotoProfile(it.uriContent!!, fileExtension)
                }

                data.data?.getParcelableExtra<UserModel>(BuildConfig.SELECTED_USER)?.let {
                    viewModel.addStaff(it)
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

    fun searchUser(){
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra("search", TAG)
        startForResult.launch(intent)
    }
}
class SignInActivity : AppCompatActivity(), View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var viewModel : SignInViewModel
    private lateinit var utility : ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var viewsAsButton : ArrayList<View>

    companion object{
        const val TAG = "signIn"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        binding.apply {
            editTexts = arrayListOf(signinEmail, signinPassword)
            viewsAsButton = arrayListOf(signinSubmit,
                signinSignUp,
                signinForgetPassword)
            utility = ViewUtility(
                context = this@SignInActivity,
                circularProgressButton = signinSubmit,
                textInputEditTexts = editTexts,
                viewsAsButton = viewsAsButton,
                checkBoxes = arrayListOf(signinRemember),
                actionBar = supportActionBar)

            signinRemember.setOnCheckedChangeListener(this@SignInActivity)
            viewsAsButton.forEach { it.setOnClickListener(this@SignInActivity) }
            editTexts.forEach { it.addTextChangedListener(this@SignInActivity) }
            checkEmpty()
        }
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
            binding.signinSubmit -> {
                utility.isLoading = true
                viewModel.signIn(binding.signinEmail.text.toString(), binding.signinPassword.text.toString())
                viewModel.isUserSignIn.observe(this@SignInActivity, {
                    if(it){
                        DatabaseHandler(this).signIn(viewModel.getDataModel())
                        utility.isLoading = false
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        finish()
                    } else {
                        utility.isLoading = false
                        viewModel.signInError.observe(this, {
                            it?.let {
                                if (it.isNotEmpty()) {
                                    Toast.makeText(this@SignInActivity, it, Toast.LENGTH_SHORT)
                                        .show()
                                    Log.i(TAG, it)
                                    viewModel.signInError.value = null
                                }
                            }
                        })
                    }
                })
            }
            binding.signinSignUp -> startActivity(Intent(this, SignUpActivity::class.java))
            binding.signinForgetPassword -> startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty() {
        viewModel.checkNotEmpty(utility.isEmpties(editTexts)).observe(this, {
            binding.signinSubmit.isEnabled = it
        })
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        viewModel.setRememberChecked(isChecked)
    }
}
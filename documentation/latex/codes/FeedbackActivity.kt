class FeedbackActivity : AppCompatActivity(), TextWatcher, RatingBar.OnRatingBarChangeListener {
    private lateinit var binding : ActivityFeedbackBinding
    private lateinit var viewModel : FeedbackViewModel
    private lateinit var utility : ViewUtility

    companion object {
        const val TAG = "feedback"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FeedbackViewModel::class.java)
        supportActionBar?.title = getString(R.string.feedback)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            utility = ViewUtility(context = this@FeedbackActivity,
                circularProgressButton = feedbackSubmit,
                textInputEditTexts = arrayListOf(feedbackContent),
                actionBar = supportActionBar)

            feedbackRatingbar.rating = 5f
            feedbackRatingbar.onRatingBarChangeListener = this@FeedbackActivity
            feedbackContent.addTextChangedListener(this@FeedbackActivity)
            checkEmpty()

            feedbackSubmit.setOnClickListener {
                utility.isLoading = true
                viewModel.submitFeedback(feedbackContent.text.toString())
                viewModel.isFeedbackSubmit.observe(this@FeedbackActivity, {
                    if(it){
                        utility.isLoading = false
                        Toast.makeText(this@FeedbackActivity, "Thank you for your feedback.", Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                        finish()
                    } else {
                        utility.isLoading = false
                        viewModel.feedbackSubmitError.observe(this@FeedbackActivity, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this@FeedbackActivity, it, Toast.LENGTH_SHORT).show()
                                Log.i(SignInActivity.TAG, it)
                                viewModel.feedbackSubmitError.value = ""
                            }
                        })
                    }
                })
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
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

    private fun checkEmpty() {
        viewModel.checkNotEmpty(
            utility.isEmpty(binding.feedbackContent) &&
            viewModel.getRating() ?: 0f > 0f
        ).observe(this, {
            binding.feedbackSubmit.isEnabled = it
        })
    }

    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        viewModel.setRating(rating)?.observe(this, {
            it.rating?.let {
                binding.feedbackRatingtxt.text = Rating.getType(rating).toString()
            }
        })
    }
}
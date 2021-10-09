package com.fdhasna21.nydrobionics.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.RatingBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityFeedbackBinding
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.enumclass.Rating
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.AddPlantViewModel
import com.fdhasna21.nydrobionics.viewmodel.FeedbackViewModel

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
                        onBackPressed()
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
        onBackPressed()
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
            viewModel.getRating()!! > 0f
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
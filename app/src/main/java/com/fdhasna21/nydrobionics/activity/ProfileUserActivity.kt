package com.fdhasna21.nydrobionics.activity

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.adapter.AdapterType
import com.fdhasna21.nydrobionics.databinding.ActivityProfileUserBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.utility.IntentUtility
import com.fdhasna21.nydrobionics.viewmodel.ProfileUserViewModel

class ProfileUserActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileUserBinding
    private lateinit var viewModel : ProfileUserViewModel

    companion object {
        const val TAG = "profileUserActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ProfileUserViewModel::class.java)
        viewModel.setUserModel(intent.getParcelableExtra<UserModel>(BuildConfig.SELECTED_USER))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.elevation= 0f

        binding.apply {
            viewModel.getUserModel().observe(this@ProfileUserActivity, {
                it?.let {
                    supportActionBar?.title = it.name
                    profileUserName.text = it.name
                    it.bio?.let{
                        profileUserBio.text = it
                    } ?: kotlin.run {
                        profileUserBio.visibility = View.GONE
                    }
                    it.photo_url?.let {
                        Glide.with(this@ProfileUserActivity)
                            .load(it)
                            .circleCrop()
                            .into(profileUserPhoto)
                    }
                }
            })

            profileUserPhoto.setOnClickListener {
                IntentUtility(this@ProfileUserActivity).openImage(profileUserPhoto, viewModel.getUserModel().value?.name ?: "Photo Profile")
            }
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val userModel : ArrayList<UserModel> = arrayListOf()
        val plantModels : ArrayList<PlantModel> = arrayListOf()
        val rowAdapter = AdapterType.POST_PLANT.getAdapter(this, plantModels, allUsers = userModel)
        viewModel.getUserPosts().observe(this,{
            plantModels.clear()
            plantModels.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
        })

        viewModel.getUserModel().observe(this, {
            userModel.clear()
            userModel.addAll(arrayListOf(it))
            rowAdapter.notifyDataSetChanged()
        })

        binding.profileUserRecyclerView.apply {
            adapter = rowAdapter
            layoutManager = LinearLayoutManager(this@ProfileUserActivity)
            addItemDecoration(object : DividerItemDecoration(this@ProfileUserActivity, VERTICAL) {})
            setHasFixedSize(true)
        }
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
}
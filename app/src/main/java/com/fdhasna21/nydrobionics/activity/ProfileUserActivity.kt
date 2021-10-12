package com.fdhasna21.nydrobionics.activity

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityProfileUserBinding
import com.fdhasna21.nydrobionics.databinding.FragmentMainProfileBinding
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.utility.IntentUtility
import com.fdhasna21.nydrobionics.utility.RequestPermission
import com.fdhasna21.nydrobionics.viewmodel.ProfileUserViewModel

class ProfileUserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityProfileUserBinding
    private lateinit var bindingFragment : FragmentMainProfileBinding
    private lateinit var viewModel : ProfileUserViewModel
    private lateinit var viewsAsButton : ArrayList<View>

    companion object {
        val TAB_TITLES = intArrayOf(
            R.string.posts
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ProfileUserViewModel::class.java)
        viewModel.setUserModel(intent.getParcelableExtra<UserModel>(BuildConfig.SELECTED_USER))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        bindingFragment = binding.showUserProfile
        bindingFragment.apply {
            viewsAsButton = arrayListOf(mainProfileEmailGroup,
                                        mainProfilePhoneGroup,
                                        mainProfileAddressGroup,
                                        mainProfilePhoto)
            viewsAsButton.forEach { it.setOnClickListener(this@ProfileUserActivity) }
        }

        viewModel.getUserModel().observe(this,{
            supportActionBar?.title = it.name
        })
        setupTabLayout()
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

    override fun onClick(v: View?) {
        when(v){
            bindingFragment.mainProfileEmail -> IntentUtility(this).openEmail(bindingFragment.mainProfileEmail.text.toString())
            bindingFragment.mainProfilePhoneGroup -> RequestPermission().requestPermission(this, Manifest.permission.CALL_PHONE, "Phone call", bindingFragment.mainProfilePhone.text.toString())
            bindingFragment.mainProfileAddressGroup -> IntentUtility(this).openMaps(bindingFragment.mainProfileAddress.toString())
            bindingFragment.mainProfilePhoto -> IntentUtility(this).openImage(bindingFragment.mainProfilePhoto, bindingFragment.mainProfileName.text.toString())
        }
    }

    private fun setupTabLayout(){
//        //todo : referencenya! plant dari user yg terkait
//        val reference = Firebase.database.getReference("posts")
//        val options = FirebaseRecyclerOptions.Builder<PlantModel>()
//            .setQuery(reference, PlantModel::class.java)
//            .build()
//
//        val tabLayoutAdapter = ViewPagerAdapter(this, arrayListOf(options), AdapterRealTimeType.PLANT)
//        bindingFragment.mainProfileViewPager.adapter = tabLayoutAdapter
//        TabLayoutMediator(bindingFragment.mainProfileTabLayout, bindingFragment.mainProfileViewPager) { tab, position ->
//            tab.text = resources.getString(TAB_TITLES[position])
//        }.attach()
//
//        bindingFragment.mainProfileTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                when(tab!!.position){
//                    0 -> {}
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {}
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {}
//        })
    }
}
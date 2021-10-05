package com.fdhasna21.nydrobionics.activity

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.adapter.ViewPagerAdapter
import com.fdhasna21.nydrobionics.databinding.ActivityProfileUserBinding
import com.fdhasna21.nydrobionics.databinding.FragmentMainProfileBinding
import com.fdhasna21.nydrobionics.dataclass.model.Plant
import com.fdhasna21.nydrobionics.enumclass.DatabaseType
import com.fdhasna21.nydrobionics.utils.IntentUtility
import com.fdhasna21.nydrobionics.utils.RequestPermission
import com.fdhasna21.nydrobionics.viewmodel.ProfileUserViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


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
        //todo : user name (get data default)
        supportActionBar?.title = getString(R.string.profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        bindingFragment = binding.showUserProfile
        bindingFragment.apply {
            viewsAsButton = arrayListOf(mainProfilePhoneGroup,
                                        mainProfileAddressGroup,
                                        mainProfilePhoto)
            viewsAsButton.forEach { it.setOnClickListener(this@ProfileUserActivity) }
        }
        setupTabLayout()
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

    override fun onClick(v: View?) {
        when(v){
            bindingFragment.mainProfilePhoneGroup -> RequestPermission().requestPermission(this, Manifest.permission.CALL_PHONE, "Phone call", bindingFragment.mainProfilePhone.text.toString())
            bindingFragment.mainProfileAddressGroup -> IntentUtility(this).openMaps(bindingFragment.mainProfileAddress.toString())
            bindingFragment.mainProfilePhoto -> IntentUtility(this).openImage(bindingFragment.mainProfilePhoto, bindingFragment.mainProfileName.text.toString())
        }
    }

    private fun setupTabLayout(){
        //todo : referencenya! plant dari user yg terkait
        val reference = Firebase.database.getReference("posts")
        val options = FirebaseRecyclerOptions.Builder<Plant>()
            .setQuery(reference, Plant::class.java)
            .build()

        val tabLayoutAdapter = ViewPagerAdapter(this, arrayListOf(options), DatabaseType.PLANT)
        bindingFragment.mainProfileViewPager.adapter = tabLayoutAdapter
        TabLayoutMediator(bindingFragment.mainProfileTabLayout, bindingFragment.mainProfileViewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        bindingFragment.mainProfileTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> {}
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}
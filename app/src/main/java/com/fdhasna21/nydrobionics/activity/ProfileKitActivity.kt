package com.fdhasna21.nydrobionics.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.adapter.ViewPagerAdapter
import com.fdhasna21.nydrobionics.databinding.ActivityProfileKitBinding
import com.fdhasna21.nydrobionics.databinding.LayoutPlantedCropsBinding
import com.fdhasna21.nydrobionics.databinding.RowItemKitMonitoringBinding
import com.fdhasna21.nydrobionics.dataclass.model.Plant
import com.fdhasna21.nydrobionics.enumclass.DatabaseType
import com.fdhasna21.nydrobionics.viewmodel.ProfileKitViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.color.MaterialColors
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileKitActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityProfileKitBinding
    private lateinit var bindingCondition : RowItemKitMonitoringBinding
    private lateinit var bindingPlanted : LayoutPlantedCropsBinding
    private lateinit var viewModel : ProfileKitViewModel

    companion object {
        val TAB_TITLES = intArrayOf(
            R.string.data_monitoring,
            R.string.crops
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileKitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ProfileKitViewModel::class.java)
        //todo : title kit name, subtitle last update
        supportActionBar?.title = getString(R.string.hydroponic_kit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        bindingCondition = binding.kitCondition
        bindingCondition.apply {
            kitDetail.visibility = View.GONE
            kitCard.elevation = 0f
            kitTitle.setTextColor(MaterialColors.getColor(this@ProfileKitActivity, R.attr.colorPrimary, ContextCompat.getColor(this@ProfileKitActivity, R.color.black)))
        }

        bindingPlanted = binding.kitPlantedCrops
        bindingPlanted.apply {
            kitPlantCard.elevation = 0f
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile_kit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.kitSettings -> {
                val intent = Intent(this, AddKitActivity::class.java)
//                todo send data default
//                intent.putExtra("defaultData", CreateProfileActivity.TAG)
                startActivity(intent)
            }
            R.id.kitHistory -> startActivity(Intent(this, ShowHistoryActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when(v){

        }
    }

    private fun setupTabLayout(){
        //todo : referencenya! plant dari user yg terkait
        val reference = Firebase.database.getReference("crops")
        val options = FirebaseRecyclerOptions.Builder<Plant>()
            .setQuery(reference, Plant::class.java)
            .build()

        val tabLayoutAdapter = ViewPagerAdapter(this, arrayListOf(options, options), DatabaseType.PLANT)
        binding.kitViewPager.adapter = tabLayoutAdapter
        TabLayoutMediator(binding.kitTabLayout, binding.kitViewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        binding.kitTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> {}
                    1 -> {}
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

}
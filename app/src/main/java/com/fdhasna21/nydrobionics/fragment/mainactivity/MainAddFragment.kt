package com.fdhasna21.nydrobionics.fragment.mainactivity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.*
import com.fdhasna21.nydrobionics.databinding.FragmentMainAddBinding
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView

class MainAddFragment : BottomSheetDialogFragment(), NavigationView.OnNavigationItemSelectedListener {
    private var _binding : FragmentMainAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        binding.mainAddMenu.itemIconTintList = null
        binding.mainAddMenu.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.addDataMonitoring  -> gotoActivity(AddDataMonitoringActivity::class.java)
            R.id.addCrops           -> gotoActivity(AddCropsActivity::class.java)
            R.id.addNote            -> gotoActivity(AddNoteActivity::class.java)
            R.id.addKit             -> gotoActivity(AddKitActivity::class.java)
            R.id.addPlant           -> gotoActivity(AddPlantActivity::class.java)
            else                    -> false
        }
    }

    private fun gotoActivity(destination : Class<*>) : Boolean{
        dismiss()
        val intent = Intent(requireActivity(), destination)
        intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUser().value)
        intent.putExtra(BuildConfig.CURRENT_FARM, viewModel.getCurrentFarm().value)
        startActivity(intent)
        return true
    }
}
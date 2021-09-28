package com.fdhasna21.nydrobionics.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.FragmentMainAddBinding
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView

class MainAddFragment : BottomSheetDialogFragment(), NavigationView.OnNavigationItemSelectedListener {
    private var _binding : FragmentMainAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainAddMenu.itemIconTintList = null
        binding.mainAddMenu.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // TODO: intent ke activity add
        val intent = Intent()
        return when(item.itemId){
            R.id.addDataMonitoring -> {
                Toast.makeText(requireContext(), "data monitoring", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.addCrops -> {
                Toast.makeText(requireContext(), "data crops", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.addTodoList -> {
                Toast.makeText(requireContext(), "todo list", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.addKit -> {
                Toast.makeText(requireContext(), "kit", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.addPlant -> {
                Toast.makeText(requireContext(), "data plant", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }
}
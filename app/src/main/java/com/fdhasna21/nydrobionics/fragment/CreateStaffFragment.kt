package com.fdhasna21.nydrobionics.fragment

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.MainActivity
import com.fdhasna21.nydrobionics.databinding.FragmentCreateStaffBinding
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel

class CreateStaffFragment : Fragment(), SearchView.OnQueryTextListener, View.OnClickListener {
    private var _binding : FragmentCreateStaffBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : CreateProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.add_staff)
        viewModel = ViewModelProvider(requireActivity()).get(CreateProfileViewModel::class.java)
        //isinya disini

        val searchManager : SearchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.apply {
            createStaffSearch.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            createStaffSearch.setOnQueryTextListener(this@CreateStaffFragment)
            createStaffSubmit.setOnClickListener(this@CreateStaffFragment)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.i("createProfile", "onQueryTextSubmit: $query")
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.i("createProfile", "onQueryTextSubmit: $newText")
        return true
    }

    override fun onClick(v: View?) {
        when(v){
            binding.createStaffSubmit -> {
                //todo : send to firebase
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }
}
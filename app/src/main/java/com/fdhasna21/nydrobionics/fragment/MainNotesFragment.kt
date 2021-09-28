package com.fdhasna21.nydrobionics.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.FragmentMainNotesBinding
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel

class MainNotesFragment : Fragment() {
    private var _binding : FragmentMainNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainNotesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.notes)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }
}
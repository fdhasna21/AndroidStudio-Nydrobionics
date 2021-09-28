package com.fdhasna21.nydrobionics.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.FragmentMainHomeBinding
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel

class MainHomeFragment : Fragment() {
    private var _binding : FragmentMainHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.home)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }
}
package com.fdhasna21.nydrobionics.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.CreateProfileActivity
import com.fdhasna21.nydrobionics.activity.MainActivity
import com.fdhasna21.nydrobionics.databinding.FragmentCreateStaffBinding
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel

class CreateStaffFragment : Fragment(), View.OnClickListener {
    private var _binding : FragmentCreateStaffBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : CreateProfileViewModel
    private lateinit var viewsAsButton : ArrayList<View>

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

        binding.apply {
            viewsAsButton = arrayListOf(createStaffSubmit, createStaffSearch)
            viewsAsButton.forEach { it.setOnClickListener(this@CreateStaffFragment) }
        }
        //todo : observer dari searchnya
    }

    override fun onClick(v: View?) {
        when(v){
            binding.createStaffSubmit -> {
                //todo : send to firebase
                startActivity(Intent(activity, MainActivity::class.java))
            }
            binding.createStaffSearch -> {
                (activity as CreateProfileActivity).searchUser()
            }
        }
    }
}
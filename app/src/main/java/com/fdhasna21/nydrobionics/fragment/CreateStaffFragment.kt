package com.fdhasna21.nydrobionics.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.bumptech.glide.Glide
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

            createStaffFarmId.setText(viewModel.getFarmModel()?.farmId.toString())
            Glide.with(requireActivity())
                .load("http://brokenfortest")
                .placeholder(AvatarGenerator.avatarImage(requireContext(), 200, AvatarConstants.CIRCLE, viewModel.getFarmModel()?.name.toString()))
                .into(createStaffFarmImage)
        }
        //todo : observer dari searchnya
    }

    override fun onClick(v: View?) {
        when(v){
            binding.createStaffSubmit -> {
                //todo : send to firebase
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra("currentUserModel", viewModel.getUserModel())
                intent.putExtra("currentFarmModel", viewModel.getFarmModel())
                startActivity(intent)
                requireActivity().finish()
            }
            binding.createStaffSearch -> {
                (activity as CreateProfileActivity).searchUser()
            }
        }
    }
}
package com.fdhasna21.nydrobionics.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.FragmentCreateFarmBinding
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import java.util.ArrayList

class CreateFarmFragment : Fragment(), View.OnClickListener {
    private var _binding : FragmentCreateFarmBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : CreateProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateFarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.create_farm)
        viewModel = ViewModelProvider(requireActivity()).get(CreateProfileViewModel::class.java)
        binding.createFarmSubmit.setOnClickListener(this)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.createFarmSubmit -> {
                //todo : bikin foto, maps, check kosong, make edittext not clickable
//                Glide.with(this)
//                    .load("http://brokenfortest")
//                    .placeholder(AvatarGenerator.avatarImage(this, 200, AvatarConstants.CIRCLE, "Kotjav"))
//                    .into(imageView3)

//                isLoading = true
//                viewModel.createFarmProfile(
//                    binding.createFarmName.text.toString(),
//                    binding.createFarmDesc.text.toString(),
//                    binding.createFarmLoc.text.toString()
//                )
//                viewModel.isFarmCreated.observe(this, {
//                    if(it){
//                        isLoading = false
//                        Toast.makeText(requireContext(), "Farm created", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root).navigate(R.id.action_createGardenFragment_to_createStaffFragment)
//                    } else {
//                        isLoading = false
//                        viewModel.createProfileError.observe(this, {
//                            if(it.isNotEmpty()){
//                                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//                                Log.i("createUserFragment", it)
//                                viewModel.createProfileError.value = ""
//                            }
//                        })
//                    }
//                })
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //todo posisi awal sesuai current location, tambahin zoom
    }

    private var isLoading : Boolean = false
        set(value) {
            val editableEditText : ArrayList<TextInputEditText> = arrayListOf(
                binding.createFarmName,
                binding.createFarmDesc,
                binding.createFarmLoc
            )
            editableEditText.forEach {
                it.isCursorVisible = !value
                it.isFocusable = !value
                it.isFocusableInTouchMode = !value
            }
            if(value){
                binding.createFarmSubmit.startAnimation()
            } else {
                binding.createFarmSubmit.revertAnimation()
            }

            field = value
        }
}
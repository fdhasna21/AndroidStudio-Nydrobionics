package com.fdhasna21.nydrobionics.fragment.createprofile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.CreateProfileActivity
import com.fdhasna21.nydrobionics.activity.MainActivity
import com.fdhasna21.nydrobionics.databinding.FragmentCreateFarmBinding
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class CreateFarmFragment : Fragment(), View.OnClickListener, TextWatcher {
    private var _binding : FragmentCreateFarmBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : CreateProfileViewModel
    private lateinit var utility: ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>

    companion object {
        const val TAG = "createFarm"
    }

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
        binding.apply {
            editStaff.visibility = View.GONE
            editTexts = arrayListOf(
                createFarmName,
                createFarmLoc,
                createFarmDesc
            )
            utility = ViewUtility(
                context = requireContext(),
                circularProgressButton = createFarmSubmit,
                textInputEditTexts = editTexts,
                actionBar = (requireActivity() as CreateProfileActivity).supportActionBar
            )

            createFarmSubmit.setOnClickListener(this@CreateFarmFragment)
            editTexts.forEach { it.addTextChangedListener(this@CreateFarmFragment) }
            checkEmpty()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.createFarmSubmit -> {
                utility.isLoading = true
                viewModel.createFarmProfile(
                    binding.createFarmName.text.toString(),
                    binding.createFarmDesc.text.toString(),
                    binding.createFarmLoc.text.toString()
                )
                viewModel.isFarmCreated.observe(this, {
                    if(it){
                        utility.isLoading = false
                        Toast.makeText(requireContext(), "Farm created", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUserModel())
                        intent.putExtra(BuildConfig.CURRENT_FARM, viewModel.getCurrentFarmModel())
                        startActivity(intent)
                        Navigation.findNavController(binding.root).navigate(R.id.action_createGardenFragment_to_createStaffFragment)
                    } else {
                        utility.isLoading = false
                        viewModel.createProfileError.observe(this, {
                            if(it.isNotEmpty()){
                                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                                Log.i(TAG, it)
                                viewModel.createProfileError.value = ""
                            }
                        })
                    }
                })
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //todo : Maps. posisi awal sesuai current location, tambahin zoom
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty() {
        viewModel.checkNotEmpty(utility.isEmpties(editTexts)).observe(viewLifecycleOwner, {
            binding.createFarmSubmit.isEnabled = it
        })
    }
}
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
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.CreateProfileActivity
import com.fdhasna21.nydrobionics.databinding.FragmentCreateUserBinding
import com.fdhasna21.nydrobionics.enumclass.Gender
import com.fdhasna21.nydrobionics.enumclass.Role
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class CreateUserFragment : Fragment(), View.OnClickListener, SegmentedButtonGroup.OnPositionChangedListener {
    private var _binding : FragmentCreateUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : CreateProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.create_profile)
        viewModel = (requireActivity() as CreateProfileActivity).viewModel

        binding.apply {
            roleOwner.setOnClickListener(this@CreateUserFragment)
            roleStaff.setOnClickListener(this@CreateUserFragment)
            createUserDOB.setOnClickListener(this@CreateUserFragment)
            createUserSubmit.setOnClickListener(this@CreateUserFragment)
            createUserGender.onPositionChangedListener = this@CreateUserFragment
        }

        viewModel.getEmail().observe(requireActivity(), {
            binding.createUserEmail.setText(it)
        })
    }

    override fun onClick(v: View?) {
        when(v){
            binding.roleOwner -> setRoleType(Role.OWNER)
            binding.roleStaff -> setRoleType(Role.STAFF)
            binding.createUserDOB -> {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("WIB"))
                calendar[Calendar.MONTH] = Calendar.DECEMBER
                val constraintBuilder = CalendarConstraints.Builder()
                constraintBuilder.setValidator(DateValidatorPointBackward.now()).setEnd(calendar.timeInMillis)

                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Date of Birth")
                    .setSelection(viewModel.getDOB())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                    .setCalendarConstraints(constraintBuilder.build())
                    .build()
                datePicker.show(childFragmentManager, "DATE_PICKER")
                datePicker.addOnPositiveButtonClickListener{
                    binding.createUserDOB.setText(viewModel.setDOB(datePicker.selection))
                }
                datePicker.addOnNegativeButtonClickListener{}
                datePicker.isCancelable = false
            }
            binding.createUserSubmit -> {
//                isLoading = true
//                viewModel.createUserProfile(
//                    binding.createUserName.text.toString(),
//                    binding.createUserPhone.text.toString(),
//                    binding.createUserAddress.text.toString(),
//                    binding.createUserBio.text.toString()
//                )
//                viewModel.isUserCreated.observe(this, {
//                    if(it){
//                        isLoading = false
//                        Toast.makeText(requireContext(), "User created", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(binding.root).navigate(
                            if(binding.roleOwner.isChecked){
                                R.id.action_createUserFragment_to_createGardenFragment
                            } else {
                                R.id.action_createUserFragment_to_createStaffFragment
                            })
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

    override fun onPositionChanged(position: Int) {
        when(position){
            0 -> viewModel.setGender(Gender.MALE)
            1 -> viewModel.setGender(Gender.FEMALE)
        }
    }

    private fun setRoleType(role : Role){
        viewModel.setRole(role)
        when(role){
            Role.OWNER -> {
                binding.roleOwner.isChecked = true
                binding.roleStaff.isChecked = false
                binding.createUserRoleDescription.text = getString(R.string.owner_description)
            }
            Role.STAFF -> {
                binding.roleStaff.isChecked = true
                binding.roleOwner.isChecked = false
                binding.createUserRoleDescription.text = getString(R.string.staff_description)
            }
        }
    }

    private var isLoading : Boolean = false
        set(value) {
            val editableEditText : ArrayList<TextInputEditText> = arrayListOf(
                binding.createUserName,
                binding.createUserPhone,
                binding.createUserAddress,
                binding.createUserBio,
                binding.createUserDOB
            )
            editableEditText.forEach {
                it.isCursorVisible = !value
                it.isFocusable = !value
                it.isFocusableInTouchMode = !value
            }
            binding.createUserGender.isEnabled = !value
            binding.roleOwner.isClickable = value
            binding.roleStaff.isClickable = value
            if(value){
                binding.createUserSubmit.startAnimation()
            } else {
                binding.createUserSubmit.revertAnimation()
            }

            field = value
        }
}
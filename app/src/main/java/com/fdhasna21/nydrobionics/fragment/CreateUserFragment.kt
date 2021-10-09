package com.fdhasna21.nydrobionics.fragment

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.CreateProfileActivity
import com.fdhasna21.nydrobionics.activity.MainActivity
import com.fdhasna21.nydrobionics.databinding.FragmentCreateUserBinding
import com.fdhasna21.nydrobionics.enumclass.Gender
import com.fdhasna21.nydrobionics.enumclass.ProfileType
import com.fdhasna21.nydrobionics.enumclass.Role
import com.fdhasna21.nydrobionics.utils.IntentUtility
import com.fdhasna21.nydrobionics.utils.RequestPermission
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import kotlin.collections.ArrayList

class CreateUserFragment : Fragment(), View.OnClickListener, SegmentedButtonGroup.OnPositionChangedListener, TextWatcher {
    private var _binding : FragmentCreateUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : CreateProfileViewModel
    private lateinit var utility: ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var viewsAsButton : ArrayList<View>

    companion object {
        const val TAG = "createUser"
    }

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
            viewsAsButton = arrayListOf(roleOwner,
                                        roleStaff,
                                        createUserDOB,
                                        createUserSubmit,
                                        createUserEditPhoto,
                                        createUserPhoto)
            editTexts = arrayListOf(
                    createUserName,
                    createUserPhone,
                    createUserAddress,
                    createUserDOB,
                    createUserBio)
            utility = ViewUtility(
                context = requireContext(),
                circularProgressButton = createUserSubmit,
                textInputEditTexts = editTexts,
                viewsAsButton = viewsAsButton,
                actionBar = (requireActivity() as CreateProfileActivity).supportActionBar)
            utility.onLoadingChangeListener {
                binding.roleOwner.isClickable = it
                binding.roleStaff.isClickable = it
            }

            editTexts.forEach { it.addTextChangedListener(this@CreateUserFragment) }
            viewsAsButton.forEach { it.setOnClickListener(this@CreateUserFragment) }
            createUserPhoto.setOnLongClickListener { createUserEditPhoto.performClick() }
            createUserGender.onPositionChangedListener = this@CreateUserFragment
            checkEmpty()
        }

        viewModel.getEmail().observe(requireActivity(), {
            binding.createUserEmail.setText(it)
        })

        viewModel.getPhotoProfile().observe(requireActivity(), {
            it?.let {
                Glide.with(this)
                    .load(it.uri)
                    .circleCrop()
                    .into(binding.createUserPhoto)
            }
        })

        viewModel.isUserCreated.observe(viewLifecycleOwner, {
            if(it){
                utility.isLoading = false
                Toast.makeText(requireContext(), "User created", Toast.LENGTH_SHORT).show()
                if (Role.getType(viewModel.getCurrentUser()?.role!!) == Role.OWNER) {
                    Navigation.findNavController(binding.root).navigate(R.id.action_createUserFragment_to_createFarmFragment)
                } else {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.putExtra("currentUserModel", viewModel.getCurrentUser())
                    startActivity(intent)
                    requireActivity().finish()
                }
            } else {
                utility.isLoading = false
                viewModel.createProfileError.observe(viewLifecycleOwner, {
                    if(it.isNotEmpty()){
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        Log.i(TAG, it)
                        viewModel.createProfileError.value = ""
                    }
                })
            }
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
            binding.createUserEditPhoto -> utility.openEditPhoto(binding.createUserPhoto, ProfileType.USER)
            binding.createUserPhoto -> utility.openImage(binding.createUserPhoto)
            binding.createUserSubmit -> {
                utility.isLoading = true
                viewModel.createUserProfile(
                    binding.createUserName.text.toString(),
                    binding.createUserPhone.text.toString(),
                    binding.createUserAddress.text.toString(),
                    binding.createUserBio.text.toString()
                )
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
        checkEmpty()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty() {
        viewModel.checkNotEmpty(
            utility.isEmpties(editTexts) && (binding.roleOwner.isChecked || binding.roleStaff.isChecked)
        ).observe(viewLifecycleOwner, {
            binding.createUserSubmit.isEnabled = it
        })
    }
}
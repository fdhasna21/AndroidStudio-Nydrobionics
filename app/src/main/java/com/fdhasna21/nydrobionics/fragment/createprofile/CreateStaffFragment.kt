package com.fdhasna21.nydrobionics.fragment.createprofile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.CreateProfileActivity
import com.fdhasna21.nydrobionics.activity.MainActivity
import com.fdhasna21.nydrobionics.activity.ProfileUserActivity
import com.fdhasna21.nydrobionics.adapter.AdapterType
import com.fdhasna21.nydrobionics.adapter.UserModelAdapter
import com.fdhasna21.nydrobionics.databinding.FragmentCreateStaffBinding
import com.fdhasna21.nydrobionics.databinding.RowItemSearchBinding
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel

class CreateStaffFragment : Fragment(), View.OnClickListener {
    private var _binding : FragmentCreateStaffBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : CreateProfileViewModel
    private lateinit var utility: ViewUtility
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
            utility = ViewUtility(
                context = requireContext(),
                circularProgressButton = createStaffSubmit,
                viewsAsButton = viewsAsButton,
                actionBar = (requireActivity() as CreateProfileActivity).supportActionBar
            )

            viewsAsButton.forEach { it.setOnClickListener(this@CreateStaffFragment) }
            createStaffFarmId.setText(viewModel.getCurrentFarmModel()?.farmId.toString())
            Glide.with(requireActivity())
                .load("http://brokenfortest")
                .placeholder(AvatarGenerator.avatarImage(requireContext(), 200, AvatarConstants.CIRCLE, viewModel.getCurrentFarmModel()?.name.toString()))
                .into(createStaffFarmImage)
        }
    }

    private fun setupRecyclerView() {
        val data: ArrayList<UserModel> = arrayListOf()
        val rowAdapter = AdapterType.SEARCH_USER.getAdapter(
            requireContext(),
            data,
            type = AdapterType.Companion.SearchSelectType.SELECT
        )
        viewModel.updateStaff()
        viewModel.getStaff().observe(this, {
            data.clear()
            data.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
            (rowAdapter as UserModelAdapter).setOnItemClickListener(
                object : UserModelAdapter.OnItemClickListener {
                    override fun onItemClicked(
                        userModel: UserModel,
                        position: Int,
                        itemView: View,
                        v: RowItemSearchBinding
                    ) {
                        when (itemView) {
                            v.searchRoot -> {
                                val intent = Intent(requireContext(), ProfileUserActivity::class.java)
                                intent.putExtra(BuildConfig.SELECTED_USER, userModel)
                                startActivity(intent)
                            }
                            v.searchClose -> viewModel.removeStaff(position)
                        }
                    }

                }
            )
        })
    }

    override fun onClick(v: View?) {
        when(v){
            binding.createStaffSubmit -> {
                utility.isLoading = true
                viewModel.createStaff()
                viewModel.isStaffAdded.observe(this, {
                    if(it){
                        utility.isLoading = false
                        Toast.makeText(requireContext(), "Staff added.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.putExtra(BuildConfig.CURRENT_USER, viewModel.getCurrentUserModel())
                        intent.putExtra(BuildConfig.CURRENT_FARM, viewModel.getCurrentFarmModel())
                        startActivity(intent)
                        requireActivity().finish()
                    }
                })
            }
            binding.createStaffSearch -> {
                (activity as CreateProfileActivity).searchUser()
            }
        }
    }
}
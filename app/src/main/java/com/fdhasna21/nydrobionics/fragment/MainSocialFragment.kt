package com.fdhasna21.nydrobionics.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.adapter.AdapterType
import com.fdhasna21.nydrobionics.adapter.PostModelAdapter
import com.fdhasna21.nydrobionics.databinding.FragmentMainSocialBinding
import com.fdhasna21.nydrobionics.databinding.RowItemPostBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel

class MainSocialFragment : Fragment() {
    private var _binding : FragmentMainSocialBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    companion object {
        const val TAG = "mainSocialFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainSocialBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.social)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val plantModels : ArrayList<PlantModel> = arrayListOf()
        val userModels : ArrayList<UserModel> = arrayListOf()
        val rowAdapter = AdapterType.POST_PLANT.getAdapter(requireContext(), plantModels, allUsers = userModels)
        viewModel.allPlantModels.observe(viewLifecycleOwner,{
            plantModels.clear()
            plantModels.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
            (rowAdapter as PostModelAdapter).setOnItemClickListener(
                object : PostModelAdapter.OnItemClickListener{
                    override fun onItemClicked(
                        position: Int,
                        itemView: View,
                        v: RowItemPostBinding
                    ) {
                        when(itemView){
                            v.postRoot -> Toast.makeText(requireContext(), "clicked", Toast.LENGTH_SHORT).show()
                            v.postOptions -> Toast.makeText(requireContext(), "option", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            )
            Log.i(TAG, "${rowAdapter.itemCount}")
        })

        viewModel.allUserModels.observe(viewLifecycleOwner, {
            userModels.clear()
            userModels.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
        })

        binding.mainSocialsRecyclerView.adapter = rowAdapter
        binding.mainSocialsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.mainSocialsRecyclerView.addItemDecoration(object : DividerItemDecoration(requireContext(), VERTICAL) {})
        binding.mainSocialsRecyclerView.setHasFixedSize(true)
    }
}
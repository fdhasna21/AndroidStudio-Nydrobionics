package com.fdhasna21.nydrobionics.fragment.profilekit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdhasna21.nydrobionics.activity.ProfileKitActivity
import com.fdhasna21.nydrobionics.adapter.AdapterType
import com.fdhasna21.nydrobionics.databinding.FragmentKitCropsBinding
import com.fdhasna21.nydrobionics.dataclass.model.CropsModel
import com.fdhasna21.nydrobionics.viewmodel.ProfileKitViewModel

class KitCropsFragment : Fragment() {
    private var _binding : FragmentKitCropsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : ProfileKitViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKitCropsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (requireActivity() as ProfileKitActivity).viewModel

        val data : ArrayList<CropsModel> = arrayListOf()
        val rowAdapter = AdapterType.PROFILE_CROPS.getAdapter(requireContext(), data)
        viewModel.getCurrentCrops().observe(viewLifecycleOwner, {
            data.clear()
            data.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
        })

        binding.kitCropsRecyclerView.apply {
            adapter = rowAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}
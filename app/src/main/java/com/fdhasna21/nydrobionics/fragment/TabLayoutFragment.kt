package com.fdhasna21.nydrobionics.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.FragmentTabLayoutBinding

class TabLayoutFragment(private val adapter : RecyclerView.Adapter<*>) : Fragment() {
    private var _binding : FragmentTabLayoutBinding? = null
    val binding get() = _binding!!

    private fun setupRecyclerView(){
        binding.tablayoutRecyclerView.addItemDecoration(object :
            DividerItemDecoration(requireContext(), VERTICAL) {})
        binding.tablayoutRecyclerView.setHasFixedSize(true)
        binding.tablayoutRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tablayoutRecyclerView.adapter = adapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun getAdapter() = adapter

    fun setError(drawableID:Int, messageID:Int, code:Int?){
        binding.tablayoutResponse.progressCircular.visibility = View.INVISIBLE
        binding.tablayoutResponse.layoutError.visibility = View.VISIBLE
        binding.tablayoutRecyclerView.visibility = View.INVISIBLE
        binding.tablayoutResponse.errorImage.setImageDrawable(AppCompatResources.getDrawable(requireContext(), drawableID))
        binding.tablayoutResponse.errorMessage.text = listOf(getString(messageID), code.toString()).joinToString(". Code:")
    }

    fun setNotError(){
        binding.tablayoutResponse.progressCircular.visibility = View.INVISIBLE
        binding.tablayoutResponse.layoutError.visibility = View.GONE
        binding.tablayoutRecyclerView.visibility = View.VISIBLE
    }

    fun setUpdateData(){
        binding.tablayoutResponse.progressCircular.visibility = View.INVISIBLE
        adapter.notifyDataSetChanged()
    }

    fun getData(){
        binding.tablayoutResponse.progressCircular.visibility = View.VISIBLE
    }
}
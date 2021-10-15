package com.fdhasna21.nydrobionics.fragment.mainactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.AddPlantActivity
import com.fdhasna21.nydrobionics.adapter.AdapterType
import com.fdhasna21.nydrobionics.adapter.PostModelAdapter
import com.fdhasna21.nydrobionics.databinding.FragmentMainPostsBinding
import com.fdhasna21.nydrobionics.databinding.RowItemPostBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.utility.IntentUtility
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainPostsFragment : Fragment() {
    private var _binding : FragmentMainPostsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    companion object {
        const val TAG = "mainSocialFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPostsBinding.inflate(layoutInflater, container, false)
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
        viewModel.getAllPosts().observe(viewLifecycleOwner,{
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
                            v.postImageContent -> {
                                IntentUtility(requireContext()).openImage(itemView, viewModel.getPostUser(position)?.name ?: "Photo Profile" )
                            }
                            v.postRoot -> {
                                gotoPost(position)
//                                val intent = Intent(requireContext(), ProfileUserActivity::class.java)
//                                intent.putExtra(BuildConfig.SELECTED_USER, viewModel.getPostUser(position))
//                                startActivity(intent)
                            }
                            v.postOptions -> {
                                val items = arrayOf("Edit", "Delete")
                                MaterialAlertDialogBuilder(context!!)
                                    .setItems(items){_, which ->
                                        when(which){
                                            0 -> gotoPost(position)
                                            1 -> {
                                                viewModel.deletePost(position)
                                                viewModel.isPostDeleted.observe(requireActivity(), {
                                                    when(it){
                                                        true -> Toast.makeText(requireContext(), "Post deleted.", Toast.LENGTH_SHORT).show()
                                                    }
                                                })
                                            }
                                        }
                                    }
                                    .show()
                            }
                        }
                    }

                }
            )
            Log.i(TAG, "${rowAdapter.itemCount}")
        })

        viewModel.getAllUsers().observe(viewLifecycleOwner, {
            userModels.clear()
            userModels.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
        })

        binding.mainSocialsRecyclerView.apply {
            adapter = rowAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(object : DividerItemDecoration(requireContext(), VERTICAL) {})
            setHasFixedSize(true)
        }
    }

    private fun gotoPost(position:Int){
        val intent = Intent(requireContext(), AddPlantActivity::class.java)
        intent.putExtra(BuildConfig.SELECTED_PLANT, viewModel.getPost(position))
        startActivity(intent)
    }
}
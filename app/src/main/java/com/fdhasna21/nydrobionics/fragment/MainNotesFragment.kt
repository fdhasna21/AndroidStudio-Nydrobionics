package com.fdhasna21.nydrobionics.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.AddNoteActivity
import com.fdhasna21.nydrobionics.adapter.AdapterType
import com.fdhasna21.nydrobionics.adapter.NoteModelAdapter
import com.fdhasna21.nydrobionics.databinding.FragmentMainNotesBinding
import com.fdhasna21.nydrobionics.databinding.RowItemNoteBinding
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel

class MainNotesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private var _binding : FragmentMainNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : MainViewModel

    companion object {
        const val TAG = "mainNotesFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainNotesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.notes)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
//        binding.mainNotesRefresh.setOnRefreshListener(this)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val data : ArrayList<NoteModel> = arrayListOf()
        val rowAdapter = AdapterType.NOTE.getAdapter(requireContext(), data)
        viewModel.currentNoteModels.observe(viewLifecycleOwner,{
            data.clear()
            data.addAll(it ?: arrayListOf())
            rowAdapter.notifyDataSetChanged()
            (rowAdapter as NoteModelAdapter).setOnItemClickListener(
                object : NoteModelAdapter.OnItemClickListener{
                    override fun onItemClicked(
                        position: Int,
                        itemView: View,
                        v: RowItemNoteBinding
                    ) {
                        gotoNote(position)
                    }

                    override fun onItemLongClicked(position: Int) {
                        val popUpMenu = PopupMenu(context, view)
                        popUpMenu.menuInflater.inflate(R.menu.menu_popup, popUpMenu.menu)
                        popUpMenu.setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.popipEdit -> {
                                    gotoNote(position)
                                    true
                                }
                                R.id.popupDelete -> {
                                    viewModel.deleteNote(position)
                                    viewModel.isNoteDeleted.observe(requireActivity(), {
                                        when(it){
                                            true -> Toast.makeText(requireContext(), "Note deleted.", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                    true
                                }
                                else -> true
                            }
                        }
                        popUpMenu.show()
                    }
                }
            )
            Log.i(TAG, "${rowAdapter.itemCount}")
//            binding.mainNotesRefresh.isRefreshing = false
        })

        binding.mainNotesRecyclerView.adapter = rowAdapter
        binding.mainNotesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onRefresh() {
        viewModel.refreshNote()
    }

    private fun gotoNote(position:Int){
        val intent = Intent(requireContext(), AddNoteActivity::class.java)
        intent.putExtra("selectedNoteModel", viewModel.getNote(position))
        startActivity(intent)
    }
}
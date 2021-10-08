package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fdhasna21.nydrobionics.databinding.RowItemHistoryBinding
import com.fdhasna21.nydrobionics.dataclass.model.HistoryModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class HistoryRowAdapter(val context: Context, private val options: FirebaseRecyclerOptions<HistoryModel>)
    : FirebaseRecyclerAdapter<HistoryModel, HistoryRowAdapter.ViewHolder>(options){

    inner class ViewHolder(val binding: RowItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: HistoryModel) {
        val postKey = getRef(position).key

        //todo : setting disini
    }
}
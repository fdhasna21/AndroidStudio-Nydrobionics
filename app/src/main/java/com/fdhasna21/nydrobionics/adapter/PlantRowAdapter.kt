package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fdhasna21.nydrobionics.databinding.RowItemPostBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class PlantRowAdapter(val context: Context, private val options: FirebaseRecyclerOptions<PlantModel>)
    : FirebaseRecyclerAdapter<PlantModel, PlantRowAdapter.ViewHolder>(options){

    inner class ViewHolder(val binding: RowItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: PlantModel) {
        val postKey = getRef(position).key

        //todo : setting disini
    }
}
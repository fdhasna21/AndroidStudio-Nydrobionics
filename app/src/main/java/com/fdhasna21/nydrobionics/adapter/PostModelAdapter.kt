package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fdhasna21.nydrobionics.databinding.RowItemPostBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel

class PostModelAdapter(private val context: Context,
                       private var plantModel: ArrayList<PlantModel>) : RecyclerView.Adapter<PostModelAdapter.ViewHolder>(){
    inner class ViewHolder(val binding : RowItemPostBinding) : RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickListener : OnItemClickListener
    companion object {
        const val TAG = "postModelAdapter"
    }

    fun setOnItemClickListener(onItemClickListener : OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(data:PlantModel)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = plantModel[position]
        holder.binding.apply {
            //todo : bikin layoutnya
        }
    }

    override fun getItemCount(): Int {
        return plantModel.size
    }
}
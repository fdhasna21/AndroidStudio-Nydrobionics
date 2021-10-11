package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fdhasna21.nydrobionics.databinding.RowItemNoteBinding
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel

class NoteModelAdapter (
    private val context: Context,
    private var noteModel: ArrayList<NoteModel>
) : RecyclerView.Adapter<NoteModelAdapter.ViewHolder>(){
    inner class ViewHolder(val binding :RowItemNoteBinding) : RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickListener : OnItemClickListener
    companion object {
        const val TAG = "noteModelAdapter"
    }

    fun setOnItemClickListener(onItemClickListener : OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(data:PlantModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = noteModel[position]
        holder.binding.apply {
            //todo : bikin layout
        }
    }

    override fun getItemCount(): Int {
        return noteModel.size
    }
}
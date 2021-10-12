package com.fdhasna21.nydrobionics.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fdhasna21.nydrobionics.databinding.RowItemNoteBinding
import com.fdhasna21.nydrobionics.dataclass.model.NoteModel

class NoteModelAdapter (
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
        fun onItemClicked(position: Int, itemView: View, v:RowItemNoteBinding)
        fun onItemLongClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = noteModel[position]
        holder.binding.apply {
            isYearChange(item.timestamp?.substring(0,4), position, noteYear)
            noteTitle.text = item.title
            noteSubtitle.text = item.description
            noteTimestamp.text = item.timestamp
            noteContentRoot.setOnClickListener {
                onItemClickListener.onItemClicked(position, it, holder.binding)
            }

            noteContentRoot.setOnLongClickListener {
                onItemClickListener.onItemLongClicked(position)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return noteModel.size
    }

    private fun isYearChange(currentYear : String?, position: Int, yearView:TextView){
        val lastIndex : Int = position-1
        yearView.apply {
            visibility = View.GONE
            if (currentYear != null && position < noteModel.size && lastIndex >= 0) {
                val lastYear = noteModel[lastIndex].timestamp?.substring(0,4)
                if (position == 0 || currentYear != lastYear) {
                    visibility = View.VISIBLE
                    yearView.text = currentYear
                }
            } else if (position == 0){
                visibility = View.VISIBLE
                yearView.text = currentYear
            }
        }
    }
}
package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.databinding.RowItemSearchBinding
import com.fdhasna21.nydrobionics.dataclass.model.UserModel

class UserModelAdapter(
    private val context: Context,
    private var userModel: ArrayList<UserModel>,
    private val type: AdapterType.Companion.SearchSelectType?
)
    : RecyclerView.Adapter<UserModelAdapter.ViewHolder>(){
    inner class ViewHolder(val binding: RowItemSearchBinding):RecyclerView.ViewHolder(binding.root)

    private lateinit var onItemClickListener : OnItemClickListener

    fun setOnItemClickListener(onItemClickListener : OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, itemView:View, v:RowItemSearchBinding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = userModel[position]

        holder.binding.apply {
            when(type){
                AdapterType.Companion.SearchSelectType.SEARCH -> {
                    searchClose.visibility = View.GONE
                }
                AdapterType.Companion.SearchSelectType.SELECT-> {
                    searchClose.visibility = View.VISIBLE
                }
            }

            searchTextTitle.text = item.name
            searchTextSubtitle.text = item.email
            Glide.with(context)
                .load(item.photo_url)
                .circleCrop()
                .into(searchPicture)
            searchRoot.setOnClickListener {
                onItemClickListener.onItemClicked(position, it, holder.binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return userModel.size
    }
}
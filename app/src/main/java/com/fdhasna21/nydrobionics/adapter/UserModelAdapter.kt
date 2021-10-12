package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.databinding.RowItemSearchBinding
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserModelAdapter(
    private val context: Context,
    private var userModel: ArrayList<UserModel>,
    private val type: AdapterType.Companion.SearchSelectType?
)
    : RecyclerView.Adapter<UserModelAdapter.ViewHolder>(){
    inner class ViewHolder(val binding: RowItemSearchBinding):RecyclerView.ViewHolder(binding.root)
    private var auth : FirebaseAuth = Firebase.auth
    private lateinit var onItemClickListener : OnItemClickListener

    fun setOnItemClickListener(onItemClickListener : OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(userModel:UserModel, position: Int, itemView:View, v:RowItemSearchBinding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = userModel[position]

        holder.binding.apply {
            searchClose.visibility = View.GONE
            if(type == AdapterType.Companion.SearchSelectType.SELECT){
                if(item.uid != auth.uid){
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
                onItemClickListener.onItemClicked(item, position, it, holder.binding)
            }
            searchClose.setOnClickListener {
                onItemClickListener.onItemClicked(item, position, it, holder.binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return userModel.size
    }
}
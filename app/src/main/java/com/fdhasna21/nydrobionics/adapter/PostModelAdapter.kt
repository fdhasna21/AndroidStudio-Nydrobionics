package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.ProfileUserActivity
import com.fdhasna21.nydrobionics.databinding.RowItemPostBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostModelAdapter(private val context: Context,
                       private var plantModel: ArrayList<PlantModel>,
                       private var userModel:ArrayList<UserModel>)
    : RecyclerView.Adapter<PostModelAdapter.ViewHolder>(){
    inner class ViewHolder(val binding : RowItemPostBinding) : RecyclerView.ViewHolder(binding.root){
        fun getUser(userId:String?) : UserModel?{
            var output : UserModel? = null
            userModel.forEach {
                if(it.uid == userId){
                    output = it
                }
            }
            return output
        }
    }
    private lateinit var onItemClickListener : OnItemClickListener
    companion object {
        const val TAG = "postModelAdapter"
    }

    fun setOnItemClickListener(onItemClickListener : OnItemClickListener) {
        onItemClickListener.let {
            this.onItemClickListener = onItemClickListener
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(position:Int, itemView:View, v:RowItemPostBinding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = plantModel[position]
        holder.binding.apply {
            val user = holder.getUser(item.userId)
            user?.let {
                postUserName.text = it.name
                postUserEmail.text = it.email
                postDescription.text = context.getString(R.string.post_from_plant,
                    item.name,
                    item.growthTime,
                    item.tempLv?.min ?: 0f, item.tempLv?.max ?: 0f,
                    item.humidLv?.min ?:0f, item.humidLv?.max ?: 0f,
                    item.phLv?.min ?: 0f, item.phLv?.max ?: 0f)
                Glide.with(context)
                    .load(it.photo_url ?: R.drawable.bg_farmer)
                    .circleCrop()
                    .into(postUserImage)
                postOptions.visibility = if(it.uid == Firebase.auth.uid && context !is ProfileUserActivity){
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            postTimeStamp.text = item.timestamp
            postImageContent.setOnClickListener {
                onItemClickListener.onItemClicked(position, it, holder.binding)
            }
            postOptions.setOnClickListener{
                onItemClickListener.onItemClicked(position, it, holder.binding)
            }
            postRoot.setOnClickListener {
                onItemClickListener.onItemClicked(position, it, holder.binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return plantModel.size
    }
}
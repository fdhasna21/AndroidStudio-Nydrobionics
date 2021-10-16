package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.RowItemSearchBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PlantModelAdapter(
    private val context: Context,
    private var plantModel: ArrayList<PlantModel>,
    private val type: AdapterType.Companion.SearchSelectType,
    private val userModel : ArrayList<UserModel>
)
    : RecyclerView.Adapter<PlantModelAdapter.ViewHolder>(){
    inner class ViewHolder(val binding: RowItemSearchBinding):RecyclerView.ViewHolder(binding.root){
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
    private var firestore : FirebaseFirestore = Firebase.firestore
    private lateinit var onItemClickListener : OnItemClickListener
    companion object {
        const val TAG = "searchPlantModelAdapter"
    }

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
        val item = plantModel[position]
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
            Glide.with(context)
                .load(item.photo_url ?: R.drawable.bg_plant)
                .circleCrop()
                .into(searchPicture)
            searchRoot.setOnClickListener {
                onItemClickListener.onItemClicked(position, it, holder.binding)
            }

            val user = holder.getUser(item.userId)
            user?.let {
                searchTextSubtitle.text = it.email
            }
        }
    }

    override fun getItemCount(): Int {
        return plantModel.size
    }
}
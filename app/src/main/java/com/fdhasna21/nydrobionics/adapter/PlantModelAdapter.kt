package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.databinding.RowItemSearchBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel.Companion.toUserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PlantModelAdapter(
    private val context: Context,
    private var plantModel: ArrayList<PlantModel>,
    private val type: AdapterType.Companion.SearchSelectType
)
    : RecyclerView.Adapter<PlantModelAdapter.ViewHolder>(){
    inner class ViewHolder(val binding: RowItemSearchBinding):RecyclerView.ViewHolder(binding.root)
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
                .load(item.photo_url)
                .circleCrop()
                .into(searchPicture)
            searchRoot.setOnClickListener {
                onItemClickListener.onItemClicked(position, it, holder.binding)
            }

            item.userId?.let {
                val db = firestore.collection("users").document(it)
                try{
                    db.addSnapshotListener { snapshot, error ->
                        snapshot?.let {
                            val email = it.toUserModel()?.email
                            if(email == null){
                                searchTextSubtitle.visibility = View.GONE
                            } else {
                                searchTextSubtitle.text = email
                            }
                        }
                    }
                } catch (e:Exception){
                    Log.e(TAG, "Error getting user $it", e)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return plantModel.size
    }
}
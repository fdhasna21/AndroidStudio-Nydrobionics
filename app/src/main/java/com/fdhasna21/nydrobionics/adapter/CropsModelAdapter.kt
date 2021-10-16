package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.RowItemCropsBinding
import com.fdhasna21.nydrobionics.dataclass.model.CropsModel
import java.time.Month
import java.time.format.TextStyle
import java.util.*

class CropsModelAdapter(
    private var context: Context,
    private var cropsModel : ArrayList<CropsModel>
) : RecyclerView.Adapter<CropsModelAdapter.ViewHolder>(){
    inner class ViewHolder(val binding : RowItemCropsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemCropsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cropsModel[position]
        holder.binding.apply {
            item.timestamp?.let {
                val month = item.timestamp?.substring(5,7)?.toInt() as Int
                cropsDate.text = item.timestamp?.substring(8,10).toString()
                cropsMonth.text = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.US)
            }

            item.plantModel?.let {
                cropsPlantTitle.text = it.name.toString()
                Glide.with(context)
                    .load(it.photo_url ?: R.drawable.bg_plant)
                    .circleCrop()
                    .into(cropsPlantPhoto)
            }
        }
    }

    override fun getItemCount(): Int {
        return cropsModel.size
    }
}
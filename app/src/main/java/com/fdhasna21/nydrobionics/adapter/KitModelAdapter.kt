package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.RowItemKitBinding
import com.fdhasna21.nydrobionics.dataclass.model.KitModel


class KitModelAdapter(
    private var context: Context,
    private var kitModel: ArrayList<KitModel>,
) : RecyclerView.Adapter<KitModelAdapter.ViewHolder>(){
    inner class ViewHolder(val binding : RowItemKitBinding) : RecyclerView.ViewHolder(binding.root)
    private lateinit var onItemClickListener : OnItemClickListener

    companion object {
        const val TAG = "kitModelAdapter"
    }

    fun setOnItemClickListener(onItemClickListener : OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemKitBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = kitModel[position]
        holder.binding.apply {
            kitName.text = item.name
            item.lastMonitoring?.let {
                kitLastMonitoring.visibility = View.VISIBLE
                kitTimestamp.visibility = View.VISIBLE
                kitTimestamp.text = context.getString(R.string.updated_on_s, it.timestamp)
                kitTemp.text = it.temperature.toString()
                kitHumid.text = it.humidity.toString()
                kitAcidity.text = it.ph.toString()
                kitWaterTank.text = it.waterTank.toString()
                kitNutrientTank.text = it.nutrientTank.toString()
                kitTurbidity.text = it.turbidity.toString()
            } ?: kotlin.run {
                kitLastMonitoring.visibility = View.GONE
                kitTimestamp.visibility = View.GONE
            }

            kitShowDetail.setOnClickListener {
                onItemClickListener.onItemClicked(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return kitModel.size
    }
}
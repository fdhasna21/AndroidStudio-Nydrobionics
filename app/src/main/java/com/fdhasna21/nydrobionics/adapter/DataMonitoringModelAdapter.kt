package com.fdhasna21.nydrobionics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fdhasna21.nydrobionics.databinding.RowItemKitMonitoringBinding
import com.fdhasna21.nydrobionics.dataclass.model.DataMonitoringModel


class DataMonitoringModelAdapter(
    private val context: Context,
    private var dataMonitoringModel: ArrayList<DataMonitoringModel>,
) : RecyclerView.Adapter<DataMonitoringModelAdapter.ViewHolder>(){
    inner class ViewHolder(val binding : RowItemKitMonitoringBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RowItemKitMonitoringBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataMonitoringModel[position]
        holder.binding.apply {
            //tofo : bikin layout
        }
    }

    override fun getItemCount(): Int {
        return dataMonitoringModel.size
    }
}
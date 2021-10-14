package com.fdhasna21.nydrobionics.fragment.profilekit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.activity.ProfileKitActivity
import com.fdhasna21.nydrobionics.databinding.FragmentKitMonitoringBinding
import com.fdhasna21.nydrobionics.viewmodel.ProfileKitViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class KitMonitoringFragment : Fragment() {
    private var _binding : FragmentKitMonitoringBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel : ProfileKitViewModel

    companion object {
        const val TAG = "kitMonitoringFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKitMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (requireActivity() as ProfileKitActivity).viewModel

        viewModel.getCurrentMonitoring().observe(viewLifecycleOwner, {
            val tempList : ArrayList<Entry> = arrayListOf()
            val humidList : ArrayList<Entry> = arrayListOf()
            val acidList : ArrayList<Entry> = arrayListOf()
            val waterList : ArrayList<Entry> = arrayListOf()
            val nutrientList : ArrayList<Entry> = arrayListOf()
            val turbidityList : ArrayList<Entry> = arrayListOf()

            it?.forEachIndexed { index, dataMonitoringModel ->
                tempList.add(Entry(index.toFloat(), dataMonitoringModel.temperature ?: 0f))
                humidList.add(Entry(index.toFloat(), dataMonitoringModel.humidity ?: 0f))
                acidList.add(Entry(index.toFloat(), dataMonitoringModel.ph ?: 0f))
                waterList.add(Entry(index.toFloat(), dataMonitoringModel.waterTank ?: 0f))
                nutrientList.add(Entry(index.toFloat(), dataMonitoringModel.nutrientTank ?: 0f))
                turbidityList.add(Entry(index.toFloat(), dataMonitoringModel.turbidity ?: 0f))
            }

            binding.apply {
                kitCropsTemp.data = LineData(LineDataSet(tempList, getString(R.string.temperature_only)))
                kitCropsHumid.data = LineData(LineDataSet(humidList, getString(R.string.humidity_only)))
                kitCropsAcid.data = LineData(LineDataSet(tempList, getString(R.string.acidity_only)))
                kitCropsWaterTank.data = LineData(LineDataSet(tempList, getString(R.string.water_level_only)))
                kitCropsNutrientTank.data = LineData(LineDataSet(tempList, getString(R.string.nutrient_level_only)))
                kitCropsTurbidity.data = LineData(LineDataSet(tempList, getString(R.string.turbidity_only)))

                kitCropsTemp.invalidate()
                kitCropsHumid.invalidate()
                kitCropsAcid.invalidate()
                kitCropsWaterTank.invalidate()
                kitCropsNutrientTank.invalidate()
                kitCropsTurbidity.invalidate()
            }
        })
    }
}
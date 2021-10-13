package com.fdhasna21.nydrobionics.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddCropsBinding
import com.fdhasna21.nydrobionics.databinding.ActivityAddDataMonitoringBinding
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.enumclass.NumberPickerType
import com.fdhasna21.nydrobionics.fragment.CreateFarmFragment
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.AddDataMonitoringViewModel
import pl.polak.clicknumberpicker.ClickNumberPickerView
import kotlin.Exception

class AddDataMonitoringActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityAddDataMonitoringBinding
    private lateinit var bindingFragment : ActivityAddCropsBinding
    private lateinit var viewModel : AddDataMonitoringViewModel
    private lateinit var utility: ViewUtility
    private lateinit var tooltips : ArrayList<ImageButton>
    private lateinit var viewsAsButton : ArrayList<View>
    private lateinit var numberPickers : ArrayList<ClickNumberPickerView>

    companion object{
        const val TAG = "addDataMonitoring"
    }

    //todo : kurang tombol buat panen, nampilin yg msh ada tanemannya
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddDataMonitoringViewModel::class.java)
        supportActionBar?.title = getString(R.string.add_recent_data_monitoring)
        viewModel.setCurrentData(intent.getParcelableExtra<UserModel>(BuildConfig.CURRENT_USER),
            intent.getParcelableExtra<FarmModel>(BuildConfig.CURRENT_FARM),
            isAddCrops = false)
        supportActionBar?.subtitle = viewModel.currentFarmModel.value?.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        //todo : tooltip semuanya
        binding.apply {
            bindingFragment = binding.addMonitoringCropsCreate
            bindingFragment.apply {
                acSelectKit.visibility = View.GONE
                acCropsSubmit.visibility = View.GONE

                viewsAsButton = arrayListOf(
                    addMonitoringSubmit,
                    addMonitoringCropsButton,
                    acNewPlant,
                    acSelectPlant
                )
                numberPickers = arrayListOf(
                    addMonitoringTemperature,
                    addMonitoringHumidity,
                    addMonitoringAcidity,
                    addMonitoringWaterTank,
                    addMonitoringNutrientTank,
                    addMonitoringWaterTurbidity
                )
                tooltips = arrayListOf(admTempInfo,
                    admHumidInfo,
                    admAcidityInfo,
                    admWaterLevelInfo,
                    admNutrientLevelInfo,
                    admWaterTurbidityInfo)

                utility = ViewUtility(
                    context = this@AddDataMonitoringActivity,
                    circularProgressButton = addMonitoringSubmit,
                    viewsAsButton = viewsAsButton,
                    actionBar = supportActionBar
                )

                numberPickers.forEachIndexed { i, view ->
                    val numberPickerDataType = getNumberPickerType(view)
                    viewModel.getNumberPickerValue(numberPickerDataType)?.observe(this@AddDataMonitoringActivity,{
                        if(it != view.value){
                            view.setPickerValue(it)
                        }
                    })

                    numberPickers[i].setClickNumberPickerListener { previousValue, currentValue, pickerClickType ->
                        viewModel.setNumberPickerValue(currentValue, numberPickerDataType)
                        checkEmpty()
                    }
                }
                viewsAsButton.forEach { it.setOnClickListener(this@AddDataMonitoringActivity) }
                tooltips.forEach {
                    it.setOnClickListener {
                        it.performLongClick()
                    }
                }
                admHumidInfo.tooltipText = "Min : \n" +
                        "Max : "

                viewModel.getCurrentCrops().observe(this@AddDataMonitoringActivity, {
                    if(it == null){ //crops not found
                        addMonitoringCropsPlanted.root.visibility = View.GONE
                        addMonitoringNewCrops.visibility = View.VISIBLE
                    } else { //crops found
                        addMonitoringCropsPlanted.root.visibility = View.VISIBLE
                        addMonitoringNewCrops.visibility = View.GONE
                        addMonitoringCropsPlanted.apply {
                            //todo kurang moreday
//                            val plantedDate : String = utility.formatTimestampToDate(it.timestamp)
//                            val estimateDate : Long = utility.formatStringToDate(plantedDate, it.plantModel?.growthTime)
                            this.kitPlantName.text = it.plantModel?.name.toString()
//                            this.kitPlantDate.text = plantedDate
                            this.kitPlantHarvest.text = getString(R.string.can_be_harvested_at_s, "now")//utility.formatDateToString(estimateDate))
                            Glide.with(this@AddDataMonitoringActivity)
                                .load(it.plantModel?.photo_url)
                                .circleCrop()
                                .into(this.kitPlantPhoto)
                        }
                    }
                })

                viewModel.getCurrentPlant().observe(this@AddDataMonitoringActivity, {
                    if(it != null){
                        try{
                            acPlantSelector.visibility = View.GONE
                            acCropsContent.visibility = View.VISIBLE
                            addMonitoringCropsButton.visibility = View.GONE

                            val estimated : String = utility.formatDateToString(utility.formatStringToDate(est = it.growthTime!!)).toString()
                            acPlantName.setText(it.name)
                            acPlantDescription.setText(it.description)
                            acPlantGrowth.setText(getString(R.string.d_days, it.growthTime))
                            acPlantEstimated.setText(estimated)
                            acPlantTempMin.setText(getString(R.string.temp_value, it.tempLv?.min))
                            acPlantTempMax.setText(getString(R.string.temp_value, it.tempLv?.max))
                            acPlantHumidMin.setText(getString(R.string.humid_value, it.humidLv?.min))
                            acPlantHumidMax.setText(getString(R.string.humid_value, it.humidLv?.max))
                            acPlantAcidMin.setText(it.phLv?.min.toString())
                            acPlantAcidMax.setText(it.phLv?.max.toString())
                            Glide.with(this@AddDataMonitoringActivity)
                                .load(it.photo_url)
                                .circleCrop()
                                .into(acPlantPhoto)
                        } catch (e:Exception){
                            Log.e(TAG, "cannot parse plant data", e)
                        }
                    } else {
                        acPlantSelector.visibility = View.VISIBLE
                        acCropsContent.visibility = View.GONE
                        addMonitoringCropsButton.visibility = View.VISIBLE
                    }
                    checkEmpty()
                })

                viewModel.getKitSelector().observe(this@AddDataMonitoringActivity, {
                    it?.let{
                        val adapter = ArrayAdapter(this@AddDataMonitoringActivity, R.layout.row_item_list, it)
                        (admSelectKit.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                        Log.i(TAG, "${adapter.count} ${it}")

                        (admSelectKit.editText as? AutoCompleteTextView)?.setOnItemClickListener { parent, view, position, id ->
                            viewModel.setCurrentKit(position)
                            checkEmpty()
                        }
                    }
                })
                checkEmpty()
            }
        }
    }

    private fun checkEmpty() {
        val currTemp : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addMonitoringTemperature))?.value ?:0f
        val currHumid : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addMonitoringHumidity))?.value ?:0f
        val currAcid : Float =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addMonitoringAcidity))?.value ?:0f
        val currWaterTank : Float =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addMonitoringWaterTank))?.value ?:0f
        val currNutrientTank : Float =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addMonitoringNutrientTank))?.value ?: 0f
        val currentTurbidity : Float =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addMonitoringWaterTurbidity))?.value ?:0f

        viewModel.checkNotEmpty(
            viewModel.getCurrentKit().value != null &&
                    currTemp > 0f &&
                    currHumid > 0f &&
                    currAcid > 0f &&
                    currWaterTank > 0f &&
                    currNutrientTank > 0f &&
                    currentTurbidity > 0f
        ).observe(this, {
            binding.addMonitoringSubmit.isEnabled = it
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        finish()
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.addMonitoringSubmit -> {
                utility.isLoading = true
                when(binding.addCropsLayout.visibility){
                    View.GONE -> viewModel.addDataMonitoring()
                    View.VISIBLE -> viewModel.addCrops()
                }
                viewModel.isDataMonitoringAdd.observe(this,{
                    if(it){
                        utility.isLoading = false
                        Toast.makeText(this, "New data monitoring added", Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                        finish()
                    } else {
                        utility.isLoading = false
                        viewModel.addDataMonitoringError.observe(this, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                                Log.i(CreateFarmFragment.TAG, it)
                                viewModel.addDataMonitoringError.value = ""
                            }
                        })
                    }
                })
            }
            binding.addMonitoringCropsButton ->{
                when(binding.addCropsLayout.visibility){
                    View.GONE -> {
                        binding.addCropsLayout.visibility = View.VISIBLE
                        binding.addMonitoringCropsButton.text = getString(R.string.cancel)
                    }
                    View.VISIBLE -> {
                        binding.addCropsLayout.visibility = View.GONE
                        binding.addMonitoringCropsButton.text = getString(R.string.add_new_crops)
                    }
                }
            }
            bindingFragment.acNewPlant -> {
                val intent = Intent(this, AddPlantActivity::class.java)
                intent.putExtra("from", TAG)
                startForResult.launch(intent)
            }
            bindingFragment.acSelectPlant -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("search", TAG)
                startForResult.launch(intent)
            }
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        Log.i(CreateProfileActivity.TAG, "$data")
        try{
            if(data?.resultCode == Activity.RESULT_OK){
                data.data?.getParcelableExtra<PlantModel>(BuildConfig.SELECTED_PLANT)?.let {
                    bindingFragment.acPlantSelector.visibility = View.GONE
                    bindingFragment.acCropsContent.visibility = View.VISIBLE
                    viewModel.setCurrentPlant(it)
                }
            }
        } catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getNumberPickerType(view: ClickNumberPickerView) : NumberPickerType?{
        return when (view) {
            binding.addMonitoringTemperature -> NumberPickerType.TEMPERATURE
            binding.addMonitoringHumidity -> NumberPickerType.HUMIDITY
            binding.addMonitoringAcidity -> NumberPickerType.ACIDITY
            binding.addMonitoringWaterTank -> NumberPickerType.WATER_TANK
            binding.addMonitoringNutrientTank -> NumberPickerType.NUTRIENT_TANK
            binding.addMonitoringWaterTurbidity -> NumberPickerType.TURBIDITY
            else -> null
        }
    }
}
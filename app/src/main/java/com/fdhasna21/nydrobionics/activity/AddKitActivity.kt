package com.fdhasna21.nydrobionics.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddKitBinding
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.model.KitModel
import com.fdhasna21.nydrobionics.enumclass.NumberPickerType
import com.fdhasna21.nydrobionics.fragment.CreateFarmFragment
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.AddKitViewModel
import com.google.android.material.textfield.TextInputEditText
import pl.polak.clicknumberpicker.ClickNumberPickerView

class AddKitActivity : AppCompatActivity(), TextWatcher{
    private lateinit var binding : ActivityAddKitBinding
    private lateinit var viewModel : AddKitViewModel
    private lateinit var utility : ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var tooltips : ArrayList<ImageButton>
    private lateinit var numberPickers : ArrayList<ClickNumberPickerView>

    companion object {
        const val TAG = "addKit"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddKitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddKitViewModel::class.java)
        viewModel.setCurrentData(intent.getParcelableExtra("currentUserModel"),
            intent.getParcelableExtra<FarmModel>("currentFarmModel"),
            intent.getParcelableExtra<KitModel>("currentKitModel"))

        supportActionBar?.title = getString(R.string.create_new_kit)
        supportActionBar?.subtitle = viewModel.currentFarmModel.value?.name.toString()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            editTexts = arrayListOf(addKitName, addKitPosition)
            tooltips = arrayListOf(akSizeInfo,
                                  akWaterLevelInfo,
                                  akNutrientLevelInfo,
                                  akTurbidityLevelInfo)
            numberPickers = arrayListOf(addKitWidth,
                addKitLength,
                addKitWaterMin,
                addKitWaterMax,
                addKitNutrientMin,
                addKitNutrientMax,
                addKitTurbidityMin,
                addKitTurbidityMax)
            utility = ViewUtility(context = this@AddKitActivity,
                circularProgressButton = addKitSubmit,
                textInputEditTexts = editTexts,
                numberPickers = numberPickers,
                actionBar = supportActionBar)

            tooltips.forEach {
                it.setOnClickListener {
                    it.performLongClick()
                }
            }
            akSizeInfo.tooltipText = "How many holes on each side length and width (must more than 0)."
            akWaterLevelInfo.tooltipText = getString(R.string.level_tooltip)
            akNutrientLevelInfo.tooltipText = getString(R.string.level_tooltip)
            akTurbidityLevelInfo.tooltipText = "belum ditentuin"
            //todo : turbidity tooltip

            editTexts.forEach { it.addTextChangedListener(this@AddKitActivity) }
            numberPickers.forEachIndexed { i, view ->
                val numberPickerDataType = getNumberPickerType(view)
                viewModel.getNumberPickerValue(numberPickerDataType)?.observe(this@AddKitActivity,{
                    if(it != view.value){
                        view.setPickerValue(it)
                    }
                })

                numberPickers[i].setClickNumberPickerListener { previousValue, currentValue, pickerClickType ->
                    viewModel.setNumberPickerValue(currentValue, numberPickerDataType)
                    checkEmpty()
                }
            }

            addKitSubmit.setOnClickListener {
                utility.isLoading = true
                viewModel.createKit(binding.addKitName.text.toString(),
                                    binding.addKitPosition.text.toString())
                viewModel.isKitAdd.observe(this@AddKitActivity, {
                    if(it){
                        utility.isLoading = false
                        Toast.makeText(this@AddKitActivity, "New kit created.", Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                        finish()
                    } else {
                        utility.isLoading = false
                        viewModel.addKitError.observe(this@AddKitActivity, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this@AddKitActivity, it, Toast.LENGTH_SHORT).show()
                                Log.i(CreateFarmFragment.TAG, it)
                                viewModel.addKitError.value = ""
                            }
                        })
                    }
                })
            }
            checkEmpty()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        checkEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun checkEmpty(){
        val currWidth : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitWidth))?.value ?: 0f
        val currLength : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitLength))?.value ?: 0f
        val currWaterMin : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitWaterMin))?.value
        val currWaterMax : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitWaterMax))?.value
        val currNutrientMin : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitNutrientMin))?.value
        val currentNutrientMax : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitNutrientMax))?.value
        val currentTurbidityMin : Float? = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitTurbidityMin))?.value
        val currentTurbidityMax : Float? = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitTurbidityMax))?.value
        val hashMap : HashMap<Float?, Float?> = hashMapOf()
        hashMap[currWaterMin] = currWaterMax
        hashMap[currNutrientMin] = currentNutrientMax
        hashMap[currentTurbidityMin] = currentTurbidityMax

        viewModel.checkNotEmpty(
            utility.isEmpties(editTexts) &&
                    currWidth > 0f &&
                    currLength > 0f &&
                    utility.isInRanges(hashMap)
        ).observe(this@AddKitActivity, {
            binding.addKitSubmit.isEnabled = it
        })
    }

    private fun getNumberPickerType(view: ClickNumberPickerView) : NumberPickerType?{
        return when (view) {
            binding.addKitWidth -> NumberPickerType.KIT_WIDTH
            binding.addKitLength -> NumberPickerType.KIT_LENGTH
            binding.addKitWaterMin -> NumberPickerType.WATER_MIN
            binding.addKitWaterMax -> NumberPickerType.WATER_MAX
            binding.addKitNutrientMin -> NumberPickerType.NUTRIENT_MIN
            binding.addKitNutrientMax -> NumberPickerType.NUTRIENT_MAX
            binding.addKitTurbidityMin -> NumberPickerType.TURBIDITY_MIN
            binding.addKitTurbidityMax -> NumberPickerType.TURBIDITY_MAX
            else -> null
        }
    }
}
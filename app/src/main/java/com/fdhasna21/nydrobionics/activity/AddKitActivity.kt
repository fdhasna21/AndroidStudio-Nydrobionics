package com.fdhasna21.nydrobionics.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddKitBinding
import com.fdhasna21.nydrobionics.dataclass.model.FarmModel
import com.fdhasna21.nydrobionics.dataclass.model.KitModel
import com.fdhasna21.nydrobionics.dataclass.model.UserModel
import com.fdhasna21.nydrobionics.enumclass.NumberPickerDataType
import com.fdhasna21.nydrobionics.fragment.CreateFarmFragment
import com.fdhasna21.nydrobionics.utils.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.AddKitViewModel
import com.google.android.material.textfield.TextInputEditText
import pl.polak.clicknumberpicker.ClickNumberPickerView

//todo : include edit kit. mekanisme buat nyalain edittext
class AddKitActivity : AppCompatActivity(), TextWatcher{
    private lateinit var binding : ActivityAddKitBinding
    private lateinit var viewModel : AddKitViewModel
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
        viewModel.setCurrentData(intent.getParcelableExtra<UserModel>("currentUserModel"),
            intent.getParcelableExtra<FarmModel>("currentFarmModel"),
            intent.getParcelableExtra<KitModel>("currentKitModel"))

        supportActionBar?.title = getString(R.string.create_new_kit)
        supportActionBar?.subtitle = viewModel.currentFarmModel.value?.name.toString()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            tooltips = arrayListOf(akSizeInfo,
                                  akWaterLevelInfo,
                                  akNutrientLevelInfo,
                                  akTurbidityLevelInfo)
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

            editTexts = arrayListOf(addKitName, addKitPosition)
            editTexts.forEach { it.addTextChangedListener(this@AddKitActivity) }
            checkEmpty()

            numberPickers = arrayListOf(addKitWidth,
                                        addKitLength,
                                        addKitWaterMin,
                                        addKitWaterMax,
                                        addKitNutrientMin,
                                        addKitNutrientMax,
                                        addKitTurbidityMin,
                                        addKitTurbidityMax)

            numberPickers.forEachIndexed { i, view ->
                val numberPickerDataType = getNumberPickerType(view)
                viewModel.getNumberPickerValue(numberPickerDataType)?.observe(this@AddKitActivity,{
                    if(it.score != view.value){
                        view.setPickerValue(it.score!!)
                    }
                })

                numberPickers[i].setClickNumberPickerListener { previousValue, currentValue, pickerClickType ->
                    viewModel.setNumberPickerValue(currentValue, numberPickerDataType)
                    checkEmpty()
                }
            }

            //todo : max minnya diganti aja kalo min lebih besar dari max, gabisa submit

            addKitSubmit.setOnClickListener {
                //todo : send data
                isLoading = true
                viewModel.createKit(binding.addKitName.text.toString(),
                                    binding.addKitPosition.text.toString())
                viewModel.isKitAdd.observe(this@AddKitActivity, {
                    if(it){
                        isLoading = false
                        Toast.makeText(this@AddKitActivity, "Kit added", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                        finish()
                    } else {
                        isLoading = false
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
        }

        //todo : +- submit
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
        val currWidth : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitWidth))?.value?.score!!
        val currLength : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitLength))?.value?.score!!
        val currWaterMin : Float =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitWaterMin))?.value?.score!!
        val currWaterMax : Float =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitWaterMax))?.value?.score!!
        val currNutrientMin : Float =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitNutrientMin))?.value?.score!!
        val currentNutrientMax : Float =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitNutrientMax))?.value?.score!!
        val currentTurbidityMin : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitTurbidityMin))?.value?.score!!
        val currentTurbidityMax : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addKitTurbidityMax))?.value?.score!!
        val hashMap : HashMap<Float, Float> = hashMapOf()
        hashMap[currWaterMin] = currWaterMax
        hashMap[currNutrientMin] = currentNutrientMax
        hashMap[currentTurbidityMin] = currentTurbidityMax

        viewModel.apply {
            checkNotEmpty(
                ViewUtility().isEmpties(editTexts) &&
                    currWidth > 0f &&
                    currLength > 0f &&
                    ViewUtility().isInRanges(hashMap)
            ).observe(this@AddKitActivity, {
                binding.addKitSubmit.isEnabled = it
            })
        }
    }

    private fun getNumberPickerType(view: ClickNumberPickerView) : NumberPickerDataType?{
        return when (view) {
            binding.addKitWidth -> NumberPickerDataType.KIT_WIDTH
            binding.addKitLength -> NumberPickerDataType.KIT_LENGTH
            binding.addKitWaterMin -> NumberPickerDataType.WATER_MIN
            binding.addKitWaterMax -> NumberPickerDataType.WATER_MAX
            binding.addKitNutrientMin -> NumberPickerDataType.NUTRIENT_MIN
            binding.addKitNutrientMax -> NumberPickerDataType.NUTRIENT_MAX
            binding.addKitTurbidityMin -> NumberPickerDataType.TURBIDITY_MIN
            binding.addKitTurbidityMax -> NumberPickerDataType.TURBIDITY_MAX
            else -> null
        }
    }

    private var isLoading : Boolean = false
        set(value) {
            editTexts.forEach {
                it.isCursorVisible = !value
                it.isFocusable = !value
                it.isFocusableInTouchMode = !value
            }
            if(value){
                binding.addKitSubmit.startAnimation()
            } else {
                binding.addKitSubmit.revertAnimation()
            }

            field = value
        }
}
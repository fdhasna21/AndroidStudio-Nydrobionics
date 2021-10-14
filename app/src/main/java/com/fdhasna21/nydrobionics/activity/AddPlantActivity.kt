package com.fdhasna21.nydrobionics.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddPlantBinding
import com.fdhasna21.nydrobionics.dataclass.model.PlantModel
import com.fdhasna21.nydrobionics.enumclass.NumberPickerType
import com.fdhasna21.nydrobionics.enumclass.ProfileType
import com.fdhasna21.nydrobionics.fragment.createprofile.CreateFarmFragment
import com.fdhasna21.nydrobionics.utility.ViewUtility
import com.fdhasna21.nydrobionics.viewmodel.AddPlantViewModel
import com.google.android.material.textfield.TextInputEditText
import pl.polak.clicknumberpicker.ClickNumberPickerView

class AddPlantActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {
    private lateinit var binding : ActivityAddPlantBinding
    private lateinit var viewModel : AddPlantViewModel
    private lateinit var utility: ViewUtility
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var tooltips : ArrayList<ImageButton>
    private lateinit var numberPickers : ArrayList<ClickNumberPickerView>
    private lateinit var viewsAsButton : ArrayList<View>

    companion object{
        const val TAG = "addPlant"
    }

    //todo : kurang edit (tampilin data, check update)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddPlantViewModel::class.java)
        viewModel.setCurrentData(intent.getParcelableExtra<PlantModel>(BuildConfig.SELECTED_PLANT))

        supportActionBar?.title = getString(R.string.add_new_plant)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.apply {
            tooltips = arrayListOf(apGrowthInfo,
                                  apTempInfo,
                                  apHumidInfo)
            viewsAsButton = arrayListOf(addPlantPhoto, addPlantEditPhoto, addPlantSubmit)
            editTexts = arrayListOf(addPlantName, addPlantDescription)
            numberPickers = arrayListOf(addPlantGrowthTime,
                addPlantTempMin,
                addPlantTempMax,
                addPlantHumidMin,
                addPlantHumidMax,
                addPlantAcidMin,
                addPlantAcidMax)
            utility = ViewUtility(
                context = this@AddPlantActivity,
                circularProgressButton = addPlantSubmit,
                textInputEditTexts = editTexts,
                viewsAsButton = viewsAsButton,
                numberPickers = numberPickers,
                actionBar = supportActionBar)

            tooltips.forEach {
                it.setOnClickListener {
                    it.performLongClick()
                }
            }
            //todo : tooltip

            viewsAsButton.forEach { it.setOnClickListener(this@AddPlantActivity) }
            addPlantPhoto.setOnLongClickListener { addPlantEditPhoto.performClick() }
            editTexts.forEach { it.addTextChangedListener(this@AddPlantActivity) }
            numberPickers.forEachIndexed { i, view ->
                val numberPickerDataType = getNumberPickerType(view)
                viewModel.getNumberPickerValue(numberPickerDataType)?.observe(this@AddPlantActivity,{
                    if(it != view.value){
                        view.setPickerValue(it)
                    }
                })

                numberPickers[i].setClickNumberPickerListener { previousValue, currentValue, pickerClickType ->
                    viewModel.setNumberPickerValue(currentValue, numberPickerDataType)
                    checkEmpty()
                }
            }
            checkEmpty()

            viewModel.getPhotoProfile().observe(this@AddPlantActivity, {
                it?.let {
                    Glide.with(this@AddPlantActivity)
                        .load(it.uri)
                        .circleCrop()
                        .into(binding.addPlantPhoto)
                }
            })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
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
        val currGrowthTime : Float = viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantGrowthTime))?.value ?: 0f
        val currTempMin : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantTempMin))?.value
        val currTempMax : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantTempMax))?.value
        val currHumidMin : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantHumidMin))?.value
        val currentHumidMax : Float? =  viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantHumidMax))?.value
        val currentAcidMin : Float? = viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantAcidMin))?.value
        val currentAcidMax : Float? = viewModel.getNumberPickerValue(getNumberPickerType(binding.addPlantAcidMax))?.value
        val hashMap : HashMap<Float?, Float?> = hashMapOf()
        hashMap[currTempMin] = currTempMax
        hashMap[currHumidMin] = currentHumidMax
        hashMap[currentAcidMin] = currentAcidMax

        viewModel.checkNotEmpty(
            utility.isEmpties(editTexts) &&
                    currGrowthTime > 0f &&
                    utility.isInRanges(hashMap)
        ).observe(this, {
            binding.addPlantSubmit.isEnabled = it
        })
    }

    private fun getNumberPickerType(view: ClickNumberPickerView) : NumberPickerType?{
        return when (view) {
            binding.addPlantGrowthTime -> NumberPickerType.GROWTH_TIME
            binding.addPlantTempMin -> NumberPickerType.TEMP_MIN
            binding.addPlantTempMax -> NumberPickerType.TEMP_MAX
            binding.addPlantHumidMin -> NumberPickerType.HUMID_MIN
            binding.addPlantHumidMax -> NumberPickerType.HUMID_MAX
            binding.addPlantAcidMin -> NumberPickerType.ACID_MIN
            binding.addPlantAcidMax -> NumberPickerType.ACID_MAX
            else -> null
        }
    }

    override fun onClick(v: View?) {
        when(v){
            binding.addPlantPhoto -> utility.openImage(binding.addPlantPhoto)
            binding.addPlantEditPhoto -> utility.openEditPhoto(binding.addPlantPhoto, ProfileType.PLANT)
            binding.addPlantSubmit -> {
                utility.isLoading = true
                viewModel.createPlant(binding.addPlantName.text.toString(),
                    binding.addPlantDescription.text.toString())
                viewModel.isPlantAdd.observe(this@AddPlantActivity, {
                    if(it){
                        utility.isLoading = false
                        if(intent.getStringExtra("from") != null){
                            val intent = Intent()
                            intent.putExtra(BuildConfig.SELECTED_PLANT, viewModel.plantModel.value)
                            setResult(RESULT_OK, intent)
                        } else {
                            super.onBackPressed()
                            finish()
                        }
                    } else {
                        utility.isLoading = false
                        viewModel.addPlantError.observe(this@AddPlantActivity, {
                            if(it.isNotEmpty()){
                                Toast.makeText(this@AddPlantActivity, it, Toast.LENGTH_SHORT).show()
                                Log.i(CreateFarmFragment.TAG, it)
                                viewModel.addPlantError.value = ""
                            }
                        })
                    }
                })
            }
        }
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        Log.i(CreateProfileActivity.TAG, "$data")
        try{
            if(data?.resultCode == Activity.RESULT_OK){
                CropImage.getActivityResult(data.data)?.let{
                    val mimeTypeMap = MimeTypeMap.getSingleton()
                    val fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(it.uriContent!!))
                    viewModel.setPhotoPlant(it.uriContent!!, fileExtension)
                }
            }

        } catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
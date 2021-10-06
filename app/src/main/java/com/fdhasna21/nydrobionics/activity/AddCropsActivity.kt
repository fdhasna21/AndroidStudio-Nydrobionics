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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityAddCropsBinding
import com.fdhasna21.nydrobionics.dataclass.model.Plant
import com.fdhasna21.nydrobionics.viewmodel.AddCropsViewModel
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception

class AddCropsActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityAddCropsBinding
    private lateinit var viewModel : AddCropsViewModel
    private lateinit var editTexts : ArrayList<TextInputEditText>
    private lateinit var buttons : ArrayList<View>

    companion object{
        const val TAG = "addCrops"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCropsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddCropsViewModel::class.java)
        supportActionBar?.title = getString(R.string.add_new_crops)
//        todo supportActionBar?.subtitle = nama farm
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        //selector kit
        val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")
        val adapter = ArrayAdapter(this, R.layout.row_item_list, items)
        (binding.acSelectKit.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.apply {
            buttons = arrayListOf(acSelectPlant, acNewPlant, acCropsSubmit)
            buttons.forEach { it.setOnClickListener(this@AddCropsActivity) }
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

    override fun onClick(v: View?) {
        when(v){
            binding.acNewPlant -> {
                val intent = Intent(this, AddPlantActivity::class.java)
                intent.putExtra("from", TAG)
                startForResult.launch(intent)
            }
            binding.acSelectPlant -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("search", TAG)
                startForResult.launch(intent)
            }
            binding.acCropsSubmit -> {
                //todo sendData
                onBackPressed()
            }
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        Log.i(CreateProfileActivity.TAG, "$data")
        try{
            if(data?.resultCode == Activity.RESULT_OK){
                binding.acPlantSelector.visibility = View.GONE
                binding.acCropsContent.visibility = View.VISIBLE

                data.data?.getParcelableExtra<Plant>("selectedPlant")?.let {
                    //todo : update viewModel data nya/tampilin data
                }
                //todo : tampilin datanya
            }
        } catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}
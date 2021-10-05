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
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.canhub.cropper.CropImage
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityProfileFarmBinding
import com.fdhasna21.nydrobionics.databinding.FragmentCreateFarmBinding
import com.fdhasna21.nydrobionics.databinding.FragmentCreateUserBinding
import com.fdhasna21.nydrobionics.dataclass.model.User
import com.fdhasna21.nydrobionics.viewmodel.CreateProfileViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception

class ProfileFarmActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityProfileFarmBinding
    private lateinit var bindingFragment : FragmentCreateFarmBinding
    private lateinit var viewModel : CreateProfileViewModel
    private lateinit var viewsAsButton : ArrayList<View>

    companion object {
        const val TAG = "profileFarm"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileFarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CreateProfileViewModel::class.java)
        supportActionBar?.title = getString(R.string.farm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        bindingFragment = binding.editFarmFragment
        bindingFragment.apply {
            createFarmSubmit.text = getString(R.string.save)
            editStaff.visibility = View.VISIBLE

            viewsAsButton = arrayListOf(esAddStaff, createFarmSubmit)
            viewsAsButton.forEach {
                it.setOnClickListener(this@ProfileFarmActivity)
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onSupportNavigateUp(): Boolean {
        //todo : klo ada perubahan, simpan data (di view model ada variable awalnya terus dibandingin sama currentnya)
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
            bindingFragment.esAddStaff -> {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("search", TAG)
                startForResult.launch(intent)
            }
            bindingFragment.createFarmSubmit -> {
                //todo : send data
                onBackPressed()
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //todo posisi awal sesuai dari data starter
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        Log.i(CreateProfileActivity.TAG, "$data")
        try{
            if(data?.resultCode == Activity.RESULT_OK){
                data.data?.getParcelableExtra<User>("selectedUser")?.let {
                    //todo : update viewModel data nya
                }
            }

        } catch (e: Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private var isLoading : Boolean = false
}
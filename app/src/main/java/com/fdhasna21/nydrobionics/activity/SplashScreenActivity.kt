package com.fdhasna21.nydrobionics.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.databinding.ActivitySplashScreenBinding
import com.fdhasna21.nydrobionics.enumclass.Role
import com.fdhasna21.nydrobionics.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var viewModel : MainViewModel
    private var auth : FirebaseAuth = Firebase.auth

    companion object{
        const val TAG = "splashScreen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        if(auth.currentUser != null){
            viewModel.getUser(auth.uid!!)
            viewModel.isCurrentUserExist.observe(this, {
                when(it){
                    true -> {
                        viewModel.isCurrentFarmExist.observe(this, {
                            when(it){
                                true -> {
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra("currentUserModel", viewModel.currentUserModel.value)
                                    intent.putExtra("currentFarmModel", viewModel.currentFarmModel.value)
                                    startActivity(intent)
                                    finish()
                                }
                                false -> {
                                    if(Role.getType(viewModel.currentUserModel.value?.role!!) == Role.OWNER){
                                        val intent = Intent(this, CreateProfileActivity::class.java)
                                        intent.putExtra("currentUserModel", viewModel.currentUserModel.value)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "wait for owner add you", Toast.LENGTH_SHORT).show()
                                        Log.i(TAG, "wait for owner add you")
                                    }
                                }
                            }
                        })
                    }
                    false -> {
                        startActivity(Intent(this, CreateProfileActivity::class.java))
                    }
                    null ->{
                        //loading
                    }
                }
            })

        } else {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}
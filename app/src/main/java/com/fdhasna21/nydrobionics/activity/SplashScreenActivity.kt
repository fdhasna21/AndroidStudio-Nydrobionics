package com.fdhasna21.nydrobionics.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.fdhasna21.nydrobionics.BuildConfig
import com.fdhasna21.nydrobionics.databinding.ActivitySplashScreenBinding
import com.fdhasna21.nydrobionics.enumclass.Role
import com.fdhasna21.nydrobionics.utility.RequestPermission
import com.fdhasna21.nydrobionics.viewmodel.SplashScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var viewModel : SplashScreenViewModel
    private var auth : FirebaseAuth = Firebase.auth

    companion object{
        const val TAG = "splashScreen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)
        RequestPermission().requestAllPermissions(this)
    }

    fun splashIsDone(){
        if(auth.currentUser != null){
            viewModel.getCurrentData()
            viewModel.isCurrentUserExist.observe(this,{
                when(it){
                    true -> {
                        Log.i(TAG, "user ${viewModel.currentUserModel.value}")
                        viewModel.isCurrentFarmExist.observe(this, {
                            when(it){
                                true -> {
                                    Log.i(TAG, "farm ${viewModel.currentFarmModel.value}")
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra(BuildConfig.CURRENT_USER, viewModel.currentUserModel.value)
                                    intent.putExtra(BuildConfig.CURRENT_FARM, viewModel.currentFarmModel.value)
                                    startActivity(intent)
                                    finish()
                                }
                                false -> {
                                    Log.i(TAG, "currentFarm not exist")
                                    if(Role.getType(viewModel.currentUserModel.value?.role!!) == Role.OWNER){
                                        val intent = Intent(this, CreateProfileActivity::class.java)
                                        intent.putExtra(BuildConfig.SELECTED_USER, viewModel.currentUserModel.value)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "wait for owner add you", Toast.LENGTH_SHORT).show()
                                        Log.i(TAG, "wait for owner add you")
                                    }
                                }
                                null -> {
                                    Log.i(TAG, "currentFarm loading")
                                }
                            }
                        })
                    }
                    false -> {
                        Log.i(TAG, "currentUser not exist")
                        startActivity(Intent(this, CreateProfileActivity::class.java))
                        finish()
                    }
                    null -> {
                        Log.i(TAG, "currentUser loading")
                    }
                }
            })

        } else {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                startActivity(Intent(this, SignInActivity::class.java))
            }, 3000)
        }
    }
}
package com.fdhasna21.nydrobionics.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fdhasna21.nydrobionics.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    private var auth : FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            if(auth.currentUser != null){
//                startActivity(Intent(this, MainActivity::class.java))
//                finish()
//            } else {
//                startActivity(Intent(this, SignInActivity::class.java))
//            }
//        }, 5000)

//        val x = UsersFirebase<User>()
//        x.getUserDataListener("abc", object : FirebaseListenerCallback<User> {
//            override fun onSuccess(result: ArrayList<User>) {
//                Log.i("splashScreen", "onSuccess: $result ${result?.size}")
//            }
//
//            override fun onError(e: Exception) {
//                Log.i("splashScreen", "onError: $e")
//            }
//
//        })

    }
}
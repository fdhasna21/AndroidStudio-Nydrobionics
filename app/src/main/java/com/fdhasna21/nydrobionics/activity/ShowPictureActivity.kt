package com.fdhasna21.nydrobionics.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.fdhasna21.nydrobionics.R
import com.fdhasna21.nydrobionics.databinding.ActivityShowPictureBinding
import java.io.FileInputStream

class ShowPictureActivity : AppCompatActivity() {
    private lateinit var binding : ActivityShowPictureBinding

    private var currentImage : Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titleBar = intent.getStringExtra("actionBarTitle")

        window.statusBarColor = Color.BLACK
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.black)))
        supportActionBar?.title = if(titleBar.isNullOrEmpty()){"Profile Picture"}
        else{titleBar}

        val filename = intent.getStringExtra("photoContent")
        try {
            val `is`: FileInputStream = openFileInput(filename)
            currentImage = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.showPicture.setImageBitmap(currentImage)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onBackPressed()
        return true
    }
}
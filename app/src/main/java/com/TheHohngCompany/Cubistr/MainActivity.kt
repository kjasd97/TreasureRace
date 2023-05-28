package com.TheHohngCompany.Cubistr

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.TheHohngCompany.Cubistr.databinding.ActivityMainBinding
import com.github.jinatonic.confetti.CommonConfetti

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ButtonGame.setOnClickListener {
            val intent = GameActivity.newIntent(this)
            startActivity(intent)
        }

        binding.ButtonWebView.setOnClickListener {
            val intent = AppTest.newIntent(this)
            startActivity(intent)
        }


    }

}
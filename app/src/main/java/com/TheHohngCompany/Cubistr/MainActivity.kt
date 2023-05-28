package com.TheHohngCompany.Cubistr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.TheHohngCompany.Cubistr.databinding.ActivityMainBinding

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
            val intent = AppTestActivity.newIntent(this)
            startActivity(intent)
        }


    }

}
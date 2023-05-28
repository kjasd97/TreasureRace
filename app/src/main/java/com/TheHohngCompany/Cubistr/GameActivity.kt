package com.TheHohngCompany.Cubistr

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.TheHohngCompany.Cubistr.databinding.ActivityGameBinding
import com.TheHohngCompany.Cubistr.databinding.ActivityMainBinding
import com.github.jinatonic.confetti.CommonConfetti

class GameActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val finish = binding.scoreCarOrange.y + 300


        binding.buttonStart.setOnClickListener {

            it.visibility = View.INVISIBLE
            binding.buttonPush.visibility = View.VISIBLE

            val timer = object : CountDownTimer(20000, 300) {
                override fun onTick(millisUntilFinished: Long) {

                    if (binding.carOrange.y <= finish){
                        cancel()
                        binding.restartLayout.visibility = View.VISIBLE
                        binding.buttonPush.visibility = View.INVISIBLE
                    }

                    if (binding.carBlue.y <= finish) {
                        Toast.makeText(this@GameActivity, "You are looser :(", Toast.LENGTH_SHORT).show()
                        cancel()
                        binding.restartLayout.visibility = View.VISIBLE
                        binding.buttonPush.visibility = View.INVISIBLE
                    }

                    binding.carBlue.y -= 30

                }

                override fun onFinish() {
                }
            }
            timer.start()

        }

        binding.buttonPush.setOnClickListener {

            binding.carOrange.y -= 30

            if (binding.carOrange.y <= finish) {

                Toast.makeText(this, "You are the winner !!!", Toast.LENGTH_SHORT).show()

                CommonConfetti.rainingConfetti(
                    binding.root,
                    intArrayOf(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                ).oneShot()

                binding.restartLayout.visibility = View.VISIBLE
                binding.buttonPush.visibility = View.INVISIBLE
            }

        }

        binding.buttonRestart.setOnClickListener {

            binding.carOrange.y = 1592.0F
            binding.carBlue.y = 1592.0F

            if (binding.carOrange.y <= 1592.0F||binding.carBlue.y<=1592.0F){
                binding.restartLayout.visibility = View.INVISIBLE
            }else{
                binding.restartLayout.visibility = View.VISIBLE
            }

            binding.buttonStart.visibility = View.VISIBLE

        }



    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, GameActivity::class.java)
        }
    }

}
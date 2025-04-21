package com.example.coffeeshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coffeeshop.R
import com.example.coffeeshop.databinding.ActivitySplashBinding
import com.example.coffeeshop.databinding.ActivityStartingPageBinding

class StartingPageActivity : AppCompatActivity() {

    lateinit var binding: ActivityStartingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStartingPageBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.guestBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}
package com.example.coffeeshop.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.coffeeshop.Adapter.CategoryAdapter
import com.example.coffeeshop.Adapter.PopularAdapter
import com.example.coffeeshop.ViewModel.MainViewModel
import com.example.coffeeshop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get current user's ID from SharedPreferences
        val sharedPref: SharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPref.getString("current_user_id", null)

        initBanner()
        initCategory()
        initPopular()
        initBottomMenu()
        initFavorite()
    }

    private fun initBottomMenu() {
        binding.cartBtn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.loadBanner().observeForever {
            Glide.with(this@MainActivity)
                .load(it[0].url)
                .into(binding.banner)
            binding.progressBarBanner.visibility = View.GONE
        }
        viewModel.loadBanner()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        viewModel.loadCategory().observeForever {
            binding.recyclerViewCat.layoutManager =
                LinearLayoutManager(
                    this@MainActivity, LinearLayoutManager.HORIZONTAL,
                    false
                )

            binding.recyclerViewCat.adapter = CategoryAdapter(it)
            binding.progressBarCategory.visibility = View.GONE
        }
        viewModel.loadCategory()
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
        viewModel.loadPopular().observeForever {
            binding.recyclerViewPopular.layoutManager = GridLayoutManager(this, 2)
            binding.recyclerViewPopular.adapter = PopularAdapter(it)
            binding.progressBarPopular.visibility = View.GONE
        }
        viewModel.loadPopular()
    }

    private fun initFavorite() {
        binding.favBtn.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }
    }



    // Example logout function (if needed)
    private fun logout() {
        val sharedPref: SharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("current_user_id")
            apply()
        }
        startActivity(Intent(this, StartingPageActivity::class.java))
        finish()
    }
}
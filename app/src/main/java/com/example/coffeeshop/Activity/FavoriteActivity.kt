package com.example.coffeeshop.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.Adapter.ItemsListCategoryAdapter
import com.example.coffeeshop.ViewModel.MainViewModel
import com.example.coffeeshop.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var viewModel: MainViewModel
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get userId from SharedPreferences
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("current_user_id", null)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // Set up back button
        binding.backBtn.setOnClickListener {
            finish()
        }

        // Load favorite items
        loadFavorites()
    }

    private fun loadFavorites() {
        if (userId == null) {
            // Handle case where user is not logged in
            binding.progressBar.visibility = android.view.View.GONE
            return
        }

        binding.progressBar.visibility = android.view.View.VISIBLE
        viewModel.loadFavoriteItems(userId!!).observe(this, Observer { items ->
            binding.favoriteListView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.favoriteListView.adapter = ItemsListCategoryAdapter(items)
            binding.progressBar.visibility = android.view.View.GONE
        })
    }
}
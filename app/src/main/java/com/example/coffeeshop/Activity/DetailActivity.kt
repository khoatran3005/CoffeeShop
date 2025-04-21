package com.example.coffeeshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.coffeeshop.Domain.ItemsModel
import com.example.coffeeshop.R
import com.example.coffeeshop.databinding.ActivityDetailBinding
import com.example.project1762.Helper.ManagmentCart
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailActivity : AppCompatActivity() {

    lateinit var biding:ActivityDetailBinding
    private lateinit var item:ItemsModel
    private lateinit var managerCart:ManagmentCart
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        biding=ActivityDetailBinding.inflate(layoutInflater)
        setContentView(biding.root)

        // Get userId from SharedPreferences
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("current_user_id", null)

        managerCart=ManagmentCart(this)

        bundle()
        setupFavoriteButton()
        initSizeList()

    }

    private fun setupFavoriteButton() {
        // Check if the item is already favorited
        userId?.let { uid ->
            checkFavoriteStatus(item.id, uid) { isFavorited ->
                updateFavoriteIcon(isFavorited)
            }

            // Set up the toggle functionality
            biding.favBtn.setOnClickListener {
                checkFavoriteStatus(item.id, uid) { isFavorited ->
                    if (isFavorited) {
                        removeFromFavorites(item.id, uid)
                        updateFavoriteIcon(false)
                    } else {
                        addToFavorites(item.id, uid)
                        updateFavoriteIcon(true)
                    }
                }
            }
        }
    }

    private fun checkFavoriteStatus(itemId: String, userId: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val favoritesRef = database.getReference("Users").child(userId).child("Favorites").child(itemId)
        favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    private fun addToFavorites(itemId: String, userId: String) {
        val database = FirebaseDatabase.getInstance()
        val favoritesRef = database.getReference("Users").child(userId).child("Favorites")
        favoritesRef.child(itemId).setValue(true)
    }

    private fun removeFromFavorites(itemId: String, userId: String) {
        val database = FirebaseDatabase.getInstance()
        val favoritesRef = database.getReference("Users").child(userId).child("Favorites")
        favoritesRef.child(itemId).removeValue()
    }


    private fun updateFavoriteIcon(isFavorited: Boolean) {
       biding.favBtn.setImageResource(
            if (isFavorited) R.drawable.ic_favorite_filled else R.drawable.favorite_white
       )
    }


    private fun initSizeList() {
        biding.apply {
            smallBtn.setOnClickListener {
                smallBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(0)
            }
            mediumBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
                largeBtn.setBackgroundResource(0)
            }
            largeBtn.setOnClickListener {
                smallBtn.setBackgroundResource(0)
                mediumBtn.setBackgroundResource(0)
                largeBtn.setBackgroundResource(R.drawable.stroke_brown_bg)
            }
        }
    }

    private fun bundle() {
        biding.apply {
            item= intent.getSerializableExtra("object") as ItemsModel

            Glide.with(this@DetailActivity)
                .load(item.picUrl[0])
                .into(biding.picMain)

            titleTxt.text = item.title
            descriptionTxt.text = item.description
            priceTxt.text="$"+item.price
            ratingTxt.text=item.rating.toString()

            addToCartBtn.setOnClickListener {
                item.numberInCart=Integer.valueOf(
                    numberItemTxt.text.toString()
                )
                managerCart.insertItems(item)
            }

            backBtn.setOnClickListener {
                finish()
            }

            plusCart.setOnClickListener {
                numberItemTxt.text=(item.numberInCart+1).toString()
                item.numberInCart++
            }

            minusBtn.setOnClickListener {
                if(item.numberInCart>0) {
                    numberItemTxt.text=(item.numberInCart-1).toString()
                    item.numberInCart--
                }
            }
        }
    }
}
package com.example.coffeeshop.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeeshop.databinding.ActivityLoginBinding
import com.example.project1762.Helper.ManagmentCart
import com.uilover.project195.Helper.ChangeNumberItemsListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Set up login button click listener
        binding.loginBtn.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            // Validate inputs
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Attempt to log in
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        val usersRef = database.getReference("Users")
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val storedPassword = userSnapshot.child("password").getValue(String::class.java)
                        if (storedPassword == password) {
                            // Clear the cart before navigating to MainActivity
                            val cartManager = ManagmentCart(this@LoginActivity)
                            cartManager.clearAllItems(object : ChangeNumberItemsListener {
                                override fun onChanged() {
                                    // Do nothing, as we're navigating away
                                }
                            })

                            // Store userId in SharedPreferences
                            val userId = userSnapshot.key
                            val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("current_user_id", userId)
                                apply()
                            }

                            // Login successful, navigate to MainActivity
                            Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Invalid password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "No user found with this email", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
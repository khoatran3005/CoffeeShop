package com.example.coffeeshop.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coffeeshop.databinding.ActivitySignUpBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Patterns
import androidx.activity.enableEdgeToEdge

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        // Set up sign-up button click listener
        binding.signUpBtn.setOnClickListener {
            val firstName = binding.firstNameInput.text.toString().trim()
            val lastName = binding.lastNameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val confirmPassword = binding.confirmPasswordInput.text.toString().trim()

            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if email already exists
            checkEmailExists(email, firstName, lastName, password)
        }
    }

    private fun checkEmailExists(email: String, firstName: String, lastName: String, password: String) {
        val usersRef = database.getReference("Users")
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@SignUpActivity, "Email already in use", Toast.LENGTH_SHORT).show()
                } else {
                    // Email is unique, add new user
                    addNewUser(firstName, lastName, email, password)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignUpActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addNewUser(firstName: String, lastName: String, email: String, password: String) {
        val newUserRef = database.getReference("Users").push()
        newUserRef.child("firstName").setValue(firstName)
        newUserRef.child("lastName").setValue(lastName)
        newUserRef.child("email").setValue(email)
        newUserRef.child("password").setValue(password)

        // Store userId in SharedPreferences
        val userId = newUserRef.key
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("current_user_id", userId)
            apply()
        }

        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
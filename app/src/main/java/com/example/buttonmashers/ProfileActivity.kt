package com.example.buttonmashers

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        var profileImageView = findViewById<ImageView>(R.id.profileImageView)
        var nameTextView = findViewById<TextView>(R.id.nameTextView)
        var emailTextView = findViewById<TextView>(R.id.emailTextView)
        var editProfileButton = findViewById<Button>(R.id.editProfileButton)

        // Set default profile info
        nameTextView.text = "Homelander"
        emailTextView.text = "john.homie@vought.com"

        // Handle edit profile button click
        editProfileButton.setOnClickListener {
            // You can add an intent to navigate to another activity for editing the profile
            Toast.makeText(this, "Edit Profile button clicked", Toast.LENGTH_SHORT).show()
        }
    }
}

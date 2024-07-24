package com.example.buttonmashers

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ItemActivity : AppCompatActivity() {
    private lateinit var textViewQuantity: TextView
    private lateinit var iconDecrease : ImageButton
    private var number: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item)
        val iconIncrease : ImageButton = findViewById(R.id.iconIncrease)
        textViewQuantity = findViewById(R.id.textViewQuantity)
        iconDecrease = findViewById(R.id.iconDecrease)
        iconDecrease.isEnabled = false// Disable decrease button initially
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // Set the toolbar as the support action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button
        supportActionBar?.setDisplayShowTitleEnabled(true) // Show title

        iconIncrease.setOnClickListener{
            number++// Increase number quantity
            updateNumber()
        }

        iconDecrease.setOnClickListener{
            number--// Decrease number quantity
            updateNumber()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Go back to the previous activity
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item) // Keep default behavior
        }
    }

    private fun updateNumber() {
        // Update the text view with the current quantity
        textViewQuantity.text = number.toString()
        iconDecrease.isEnabled = number > 1// Disable decrease button if number is 1
    }
}
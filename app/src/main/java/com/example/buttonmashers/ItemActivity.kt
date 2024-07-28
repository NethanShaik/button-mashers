package com.example.buttonmashers

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ItemActivity : AppCompatActivity() {
    private lateinit var textViewQuantity: TextView
    private lateinit var iconDecrease : ImageButton
    private lateinit var textViewPrice : TextView
    private var number: Int = 1
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item)
        val iconIncrease : ImageButton = findViewById(R.id.iconIncrease)
        val addToCartButton : Button = findViewById(R.id.addToCartButton)
        val textViewDescription : TextView = findViewById(R.id.textViewDescription)
        val textViewGameName : TextView = findViewById(R.id.textViewGameName)
        val gameImage : ImageView = findViewById(R.id.gameImage)
        textViewPrice = findViewById(R.id.textViewPrice)
        textViewQuantity = findViewById(R.id.textViewQuantity)
        iconDecrease = findViewById(R.id.iconDecrease)
        iconDecrease.isEnabled = false// Disable decrease button initially
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        //Accessing Values from the main activity screen
        textViewPrice.text = intent.getDoubleExtra("gamePrice",0.0).toString()
        val price = intent.getDoubleExtra("gamePrice",0.0)
        textViewDescription.text = intent.getStringExtra("gameDescription")
        toolbar.title = intent.getStringExtra("gameTitle")
        textViewGameName.text = intent.getStringExtra("gameTitle")
        val imageResId = intent.getIntExtra("gameImageResId",0)
        gameImage.setImageResource(imageResId)

        setSupportActionBar(toolbar) // Set the toolbar as the support action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button
        supportActionBar?.setDisplayShowTitleEnabled(true) // Show title

        iconIncrease.setOnClickListener{
            number++// Increase number quantity
            updateNumber()
            updatePrice(price)
        }

        iconDecrease.setOnClickListener{
            number--// Decrease number quantity
            updateNumber()
            updatePrice(price)
        }

    addToCartButton.setOnClickListener{

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
    private fun updatePrice(price: Double) {
        totalPrice = number * price
        textViewPrice.text = "$$totalPrice"

    }
}
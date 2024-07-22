package com.example.buttonmashers

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CartActivity : AppCompatActivity() {
    fun increase_quantity(quantity: Int): Int {
        return quantity + 1
    }

    fun decrease_quantity(quantity: Int): Int {
        return quantity - 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // Set the toolbar as the support action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button
        supportActionBar?.setDisplayShowTitleEnabled(true) // Show title

        val quantity: EditText = findViewById(R.id.quantity)
        val price: TextView = findViewById(R.id.price)
        val increase: ImageButton = findViewById(R.id.increase)
        val decrease: ImageButton = findViewById(R.id.decrease)

        increase.setOnClickListener {

            val quant: Int = quantity.text.toString().toIntOrNull() ?: 0
            val increased_value = increase_quantity(quant)
            quantity.setText(increased_value.toString())

        }

        decrease.setOnClickListener {

            val quant: Int = quantity.text.toString().toIntOrNull() ?: 0
            val decreased_value = decrease_quantity(quant)
            quantity.setText(decreased_value.toString())

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
}
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

    private lateinit var quantity: TextView
    private lateinit var price: TextView
    private lateinit var increase: ImageButton
    private lateinit var decrease: ImageButton
    private var quant: Int = 1
    private var updatedPrice: Double = 50.0

    fun increase_quantity() {
        quant ++
        quantity.text = quant.toString()
        updatedPrice =  quant * 50.0
        price.text = updatedPrice.toString()
        decrease.isEnabled = quant > 1
    }

    fun decrease_quantity(){
        quant --
        quantity.text = quant.toString()
        updatedPrice =  quant * 50.0
        price.text = updatedPrice.toString()
        decrease.isEnabled = quant > 1
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

        quantity = findViewById(R.id.quantity)
        price = findViewById(R.id.price)
        increase = findViewById(R.id.increase)
        decrease = findViewById(R.id.decrease)
        decrease.isEnabled = false
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // Set the toolbar as the support action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button
        supportActionBar?.setDisplayShowTitleEnabled(true) // Show title

        increase.setOnClickListener {
            increase_quantity()
        }

        decrease.setOnClickListener {
            decrease_quantity()

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
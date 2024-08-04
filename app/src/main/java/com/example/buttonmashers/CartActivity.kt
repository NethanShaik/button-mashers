package com.example.buttonmashers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private lateinit var dbHelper: GameDatabaseHelper

interface OnTotalPriceChangeListener {
    fun onTotalPriceChanged(totalPrice: Double)
}

class CartActivity : AppCompatActivity(), OnTotalPriceChangeListener {
    private var gameList: MutableList<String> = mutableListOf()
    private lateinit var quantity: TextView
    private lateinit var price: TextView
    private lateinit var increase: ImageButton
    private lateinit var decrease: ImageButton
    private lateinit var delete_item: ImageButton
    private var quant: Int = 1
    private var updatedPrice: Double = 50.0
    lateinit var total: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        // Setup DB helper.
        dbHelper = GameDatabaseHelper(
            this,
            { fileName -> resources.getIdentifier(fileName, "drawable", packageName) }
        )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 1) // 2 columns
        val orders = dbHelper.getCart()
        val orderAdapter = OrderAdapter(orders?.items?:listOf(),this)
        recyclerView.adapter = orderAdapter

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // Set the toolbar as the support action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button
        supportActionBar?.setDisplayShowTitleEnabled(true) // Show title

        total = findViewById(R.id.total_amount)
        orderAdapter.updateTotalPrice() // Init total price
    }

    override fun onTotalPriceChanged(totalPrice: Double) {
        total.text = String.format("%.2f", totalPrice) // Update the TextView with the total price
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

class OrderAdapter(
    private var orders: List<OrderItem>,
    private val totalPriceChangeListener: OnTotalPriceChangeListener
) : RecyclerView.Adapter<OrderAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameImage: ImageView = view.findViewById(R.id.order_game_image)
        val gameTitle: TextView = view.findViewById(R.id.order_game_title)
        val gameCategory: TextView = view.findViewById(R.id.order_game_category)
        val gamePrice: TextView = view.findViewById(R.id.order_game_price)
        val gameQuantity: TextView = view.findViewById(R.id.order_game_quantity)
        var delete: ImageButton = view.findViewById(R.id.order_game_delete)
        var quantity_increase: ImageButton= view.findViewById(R.id.order_game_increase)
        var quantity_decrease:  ImageButton= view.findViewById(R.id.order_game_decrease)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.gameTitle.text = orders[position].game.title
        holder.gamePrice.text = orders[position].game.price.toString()
        holder.gameQuantity.text = orders[position].quantity.toString()
        holder.gameCategory.text = dbHelper.getAllCategories().filter { it.id==orders[position].game.categoryId }[0].name
        if (orders[position].game.imageResId != 0) {
            holder.gameImage.setImageResource(orders[position].game.imageResId)
        }
        holder.delete.setOnClickListener() {
            deleteItem(position)
        }

        holder.quantity_increase.setOnClickListener {
            increaseQuantity(position, holder.gameQuantity, holder.gamePrice)
            holder.quantity_decrease.isEnabled = holder.gameQuantity.text.toString().toInt() > 1
        }

        holder.quantity_decrease.setOnClickListener {
            decreaseQuantity(position, holder.gameQuantity, holder.gamePrice)
            holder.quantity_decrease.isEnabled = holder.gameQuantity.text.toString().toInt() > 1
        }
    }

    fun updateTotalPrice() {
        var total = 0.00
        for (item in orders) {
            val item_total = item.quantity * item.game.price
            total += item_total
        }
        totalPriceChangeListener.onTotalPriceChanged(total)
    }

    fun deleteItem(index: Int) {
        if (index >= 0 && index < orders.size) {
            dbHelper.removeCartItem(orders[index].game.id)
            val mutable_orders = orders.toMutableList()
            mutable_orders.removeAt(index)
            orders = mutable_orders
            notifyDataSetChanged()
            updateTotalPrice()
        }
    }

    private fun updateOrderItem(order: OrderItem, gameQuantity: TextView, gamePrice: TextView) {
        // Update UI
        gameQuantity.text = order.quantity.toString()
        gamePrice.text = String.format("%.2f", order.game.price * order.quantity)

        // Update total
        updateTotalPrice()

        // Update database
        dbHelper.updateCartItemQuantity(order.game.id, order.quantity)
    }

    private fun increaseQuantity(index: Int, gameQuantity: TextView, gamePrice: TextView) {
        val order = orders[index]

        if (order.quantity < 10) {
            order.quantity += 1
            updateOrderItem(order, gameQuantity, gamePrice)
        }
    }

    private fun decreaseQuantity(index: Int, gameQuantity: TextView, gamePrice: TextView) {
        val order = orders[index]

        if (order.quantity > 1) {
            order.quantity -= 1
            updateOrderItem(order, gameQuantity, gamePrice)
        }
    }

    override fun getItemCount() = orders.size
}
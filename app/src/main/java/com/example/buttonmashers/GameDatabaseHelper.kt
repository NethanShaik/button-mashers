package com.example.buttonmashers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GameDatabaseHelper(
    context: Context,
    private val fileNameToIdConverter: (String) -> Int
) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "gamestore.db"
        private const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Do nothing.
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Do nothing.
    }

    // Get a complete list of games.
    fun getAllGames(): List<Game> {
        val games = mutableListOf<Game>()
        val db = readableDatabase
        val cursor = db.query("games", null, null, null, null, null, null)
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                val title = getString(getColumnIndexOrThrow("title"))
                val description = getString(getColumnIndexOrThrow("description"))
                val releaseDate = getString(getColumnIndexOrThrow("release_date"))
                val price = getDouble(getColumnIndexOrThrow("price"))
                val categoryId = getInt(getColumnIndexOrThrow("category_id"))
                val imagePath = getString(getColumnIndexOrThrow("image_path"))
                games.add(Game(id, title, description, releaseDate, price, categoryId, fileNameToIdConverter(imagePath)))
            }
            close()
        }
        return games
    }

    // Get game categories.
    fun getAllCategories(): List<Category> {
        val categories = mutableListOf<Category>()
        val db = readableDatabase
        val cursor = db.query("categories", null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow("id"))
                val name = getString(getColumnIndexOrThrow("name"))
                categories.add(Category(id, name))
            }
            close()
        }
        return categories
    }

    // Get a complete order history.
    fun getAllOrders(): List<Order> {
        val games = this.getAllGames()

        val orders = mutableListOf<Order>()
        val db = readableDatabase
        val cursorOrders = db.query(
            "orders",
            null,
            null,
            null,
            null,
            null,
            "order_date DESC" // Sort by date in descending order
        )
        with(cursorOrders) {
            while (moveToNext()) {
                val orderId = getInt(getColumnIndexOrThrow("id"))
                val displayedOrderId = getString(getColumnIndexOrThrow("displayed_order_id"))
                val orderDate = getString(getColumnIndexOrThrow("order_date"))

                // Get order items for the current order
                val items = mutableListOf<OrderItem>()
                val cursorItems = db.query(
                    "order_items",
                    null,
                    "order_id = ?",
                    arrayOf(orderId.toString()),
                    null,
                    null,
                    null
                )
                with(cursorItems) {
                    while (moveToNext()) {
                        val gameId = getInt(getColumnIndexOrThrow("game_id"))
                        val quantity = getInt(getColumnIndexOrThrow("quantity"))
                        items.add(OrderItem(orderId=orderId, game=games.find { it.id == gameId }!!, quantity=quantity))
                    }
                    close()
                }
                orders.add(Order(id=orderId, displayedOrderId=displayedOrderId, orderDate=orderDate, items=items))
            }
            close()
        }
        return orders
    }

    // Add a game with quantity to the shopping cart.
    fun addGameToCart(gameId: Int, quantity: Int) {
        val db = writableDatabase

        // Find the existing shopping cart order, or create a new one.
        val cartCursor = db.query(
            "orders",
            null,
            "isShoppingCart = ?",
            arrayOf("1"),
            null,
            null,
            null
        )

        var cartId: Int? = null

        if (cartCursor.moveToFirst()) {
            cartId = cartCursor.getInt(cartCursor.getColumnIndexOrThrow("id"))
        } else {
            // No cart found in DB, create a new one (new empty order with isShoppingCart = 1).
            val values = ContentValues().apply {
                put("isShoppingCart", 1)
                put("displayed_order_id", "Cart") // A default displayed ID
            }
            cartId = db.insert("orders", null, values).toInt()
        }
        cartCursor.close()

        // Check if the game is already in the cart.
        val itemCursor = db.query(
            "order_items",
            null,
            "order_id = ? AND game_id = ?",
            arrayOf(cartId.toString(), gameId.toString()),
            null,
            null,
            null
        )

        if (itemCursor.moveToFirst()) {
            // Game is already in the cart, update the quantity.
            val existingQuantity = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("quantity"))
            val newQuantity = existingQuantity + quantity
            val values = ContentValues().apply {
                put("quantity", newQuantity)
            }
            db.update(
                "order_items",
                values,
                "order_id = ? AND game_id = ?",
                arrayOf(cartId.toString(), gameId.toString())
            )
        } else {
            // Game is not in the cart, add new order item.
            val values = ContentValues().apply {
                put("order_id", cartId)
                put("game_id", gameId)
                put("quantity", quantity)
            }
            db.insert("order_items", null, values)
        }
        itemCursor.close()
    }
}
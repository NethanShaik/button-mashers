package com.example.buttonmashers

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
                orders.add(Order(id=orderId, orderDate=orderDate, items=items))
            }
            close()
        }
        return orders
    }

}
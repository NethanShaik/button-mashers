package com.example.buttonmashers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GameDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

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
                games.add(Game(id, title, description, releaseDate, price, categoryId, imagePath))
            }
            close()
        }
        return games
    }

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
}
package com.example.buttonmashers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

fun copyDatabaseFromAssets(context: Context, overwrite: Boolean = false) {
    val dbName = "gamestore.db"
    val dbPath = context.getDatabasePath(dbName).path

    // Check if the database already exists or should be overwritten.
    if (overwrite || !File(dbPath).exists()) {
        context.assets.open(dbName).use { inputStream ->
            FileOutputStream(dbPath).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}

class MainActivity : AppCompatActivity(), OnGameClickListener {
    private lateinit var dbHelper: GameDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val profileBtn = findViewById<ImageButton>(R.id.profile_btn)
        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val cartBtn = findViewById<ImageButton>(R.id.cart_btn)
        cartBtn.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        // Copy the database from assets into the app's internal storage.
        copyDatabaseFromAssets(this, overwrite = false)

        // Setup DB helper.
        dbHelper = GameDatabaseHelper(
            this,
            { fileName -> resources.getIdentifier(fileName, "drawable", packageName) }
        )

        // Populate game list with games.
        val allGames = dbHelper.getAllGames()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns
        val gameAdapter = GameAdapter(allGames, this)
        recyclerView.adapter = gameAdapter

        // Populate category spinner with categories.
        val categories = listOf(Category(0, "All")) + dbHelper.getAllCategories()
        val categorySpinner = findViewById<Spinner>(R.id.category_spinner)
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedCategory = categories[p2]
                if (selectedCategory.id == 0) {
                    gameAdapter.replaceGames(allGames)
                } else {
                    gameAdapter.replaceGames(allGames.filter { it.categoryId == selectedCategory.id })
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        // Populate sort by spinner with sort options.
        val sortOptions = listOf("Featured", "Price: Low to High", "Price: High to Low")
        val sortBySpinner = findViewById<Spinner>(R.id.sort_by_spinner)
        val sortByAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortBySpinner.adapter = sortByAdapter
        sortBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedOption = sortOptions[p2]
                val updatedGames = when (selectedOption) {
                    "Price: Low to High" -> allGames.sortedBy { it.price }
                    "Price: High to Low" -> allGames.sortedByDescending { it.price }
                    else -> allGames
                }
                gameAdapter.replaceGames(updatedGames)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val cart = dbHelper.getCart()
        val cartItemCount = cart?.items?.sumOf { it.quantity } ?: 0
        val cartNotification = findViewById<TextView>(R.id.notification_count)
        cartNotification.text = cartItemCount.toString()
        cartNotification.visibility = if (cartItemCount > 0) View.VISIBLE else View.GONE
    }

    override fun onGameClick(game: Game) {
        val intent = Intent(this, ItemActivity::class.java)
        intent.putExtra("gameId", game.id)
        intent.putExtra("gameTitle", game.title)
        intent.putExtra("gameDescription", game.description)
        intent.putExtra("gameReleaseDate", game.releaseDate)
        intent.putExtra("gamePrice", game.price)
        intent.putExtra("gameCategoryId", game.categoryId)
        intent.putExtra("gameImageResId", game.imageResId)
        println(game.imageResId)
        startActivity(intent)
    }
}

interface OnGameClickListener {
    fun onGameClick(game: Game)
}

class GameAdapter(
    private var games: List<Game>,
    private val listener: OnGameClickListener
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameImage: ImageView = view.findViewById(R.id.game_image)
        val gameTitle: TextView = view.findViewById(R.id.game_title)
        val gamePrice: TextView = view.findViewById(R.id.game_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.gameTitle.text = game.title
        holder.gamePrice.text = "$${game.price}"

        if (game.imageResId != 0) {
            holder.gameImage.setImageResource(game.imageResId)
        }

        holder.itemView.setOnClickListener {
            listener.onGameClick(game)
        }
    }

    fun replaceGames(newGames: List<Game>) {
        games = newGames
        notifyDataSetChanged()
    }

    override fun getItemCount() = games.size
}

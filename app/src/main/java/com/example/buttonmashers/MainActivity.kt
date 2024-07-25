package com.example.buttonmashers

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
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

//        val itemBtn = findViewById<Button>(R.id.item_btn)
//        itemBtn.setOnClickListener {
//            val intent = Intent(this, ItemActivity::class.java)
//            startActivity(intent)
//        }

        val games = listOf(
            Game(id = 1, title = "The Legend of Adventure", description = "An epic quest in a magical world.", releaseDate = "2021-04-15", price = 59.99, categoryId = 1, imagePath = "path/to/image1.jpg"),
            Game(id = 2, title = "Space Wars", description = "Battle for the galaxy in this thrilling space combat game.", releaseDate = "2022-06-22", price = 49.99, categoryId = 2, imagePath = "path/to/image2.jpg"),
            Game(id = 3, title = "Mystery Manor", description = "Solve puzzles and uncover secrets in a haunted mansion.", releaseDate = "2020-10-31", price = 39.99, categoryId = 3, imagePath = "path/to/image3.jpg"),
            Game(id = 4, title = "Racing Legends", description = "High-speed racing action with stunning graphics.", releaseDate = "2019-08-12", price = 29.99, categoryId = 4, imagePath = "path/to/image4.jpg"),
            Game(id = 5, title = "Farm Life", description = "Build and manage your own farm.", releaseDate = "2023-03-01", price = 19.99, categoryId = 5, imagePath = "path/to/image5.jpg"),
            Game(id = 6, title = "Fantasy Kingdom", description = "Create your own kingdom in a fantasy world.", releaseDate = "2021-11-15", price = 59.99, categoryId = 6, imagePath = "path/to/image6.jpg"),
            Game(id = 7, title = "Zombie Apocalypse", description = "Survive the zombie outbreak and save humanity.", releaseDate = "2020-02-20", price = 44.99, categoryId = 7, imagePath = "path/to/image7.jpg"),
            Game(id = 8, title = "Puzzle Master", description = "Challenging puzzles and brain teasers.", releaseDate = "2018-12-25", price = 14.99, categoryId = 8, imagePath = "path/to/image8.jpg"),
            Game(id = 9, title = "Battle Royale", description = "Fight to be the last one standing in this battle royale game.", releaseDate = "2021-07-07", price = 39.99, categoryId = 9, imagePath = "path/to/image9.jpg")
        )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns
        recyclerView.adapter = GameAdapter(games)
    }
}

class GameAdapter(private val games: List<Game>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

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
        // Load image using an image loading library like Glide or Picasso
        // Glide.with(holder.gameImage.context).load(game.imagePath).into(holder.gameImage)
        holder.gameTitle.text = game.title
        holder.gamePrice.text = "$${game.price}"
    }

    override fun getItemCount() = games.size
}

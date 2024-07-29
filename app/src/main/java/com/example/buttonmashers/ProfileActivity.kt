package com.example.buttonmashers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: GameDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        // Setup DB helper.
        dbHelper = GameDatabaseHelper(
            this,
            { fileName -> resources.getIdentifier(fileName, "drawable", packageName) }
        )

        var gameTitles = dbHelper.getAllGames().filter { (it.owned) }


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        var nameTextView = findViewById<TextView>(R.id.nameTextView)
        var emailTextView = findViewById<TextView>(R.id.emailTextView)
        var editProfileButton = findViewById<Button>(R.id.editProfileButton)

        // Set default profile info
        nameTextView.text = "Homelander"
        emailTextView.text = "john.homie@vought.com"

        // Handle edit profile button click
        editProfileButton.setOnClickListener {
            // You can add an intent to navigate to another activity for editing the profile
            Toast.makeText(this, "Edit Profile button clicked", Toast.LENGTH_SHORT).show()
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // Set the toolbar as the support action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button
        supportActionBar?.setDisplayShowTitleEnabled(true) // Show title

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_owned_titles)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = GameTitleAdapter(gameTitles)
        recyclerView.adapter = adapter
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

class GameTitleAdapter(private val gameTitles: List<Game>) : RecyclerView.Adapter<GameTitleAdapter.GameTitleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameTitleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_owned_title, parent, false)
        return GameTitleViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameTitleViewHolder, position: Int) {
        val gameTitle = gameTitles[position]
        holder.gameTitle.text = gameTitle.title
        holder.hoursPlayed.text = "Hours Played: ${gameTitle.hoursPlayed}"
        holder.gameRating.rating = gameTitle.rating
        holder.gameImage.setImageResource(gameTitle.imageResId)
    }

    override fun getItemCount(): Int = gameTitles.size

    class GameTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameImage: ImageView = itemView.findViewById(R.id.game_image)
        val gameTitle: TextView = itemView.findViewById(R.id.game_title)
        val hoursPlayed: TextView = itemView.findViewById(R.id.hours_played)
        val gameRating: RatingBar = itemView.findViewById(R.id.game_rating)
    }
}


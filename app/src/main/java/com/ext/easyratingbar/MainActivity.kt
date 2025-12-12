package com.ext.easyratingbar

import android.os.Build
import android.os.Bundle
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ext.easy_rating_bar.EasyRatingBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val starRatingBar = findViewById<EasyRatingBar>(R.id.starRatingBar)
        val heartRatingBar = findViewById<EasyRatingBar>(R.id.heartRatingBar)
        val emojiRatingBar = findViewById<EasyRatingBar>(R.id.emojiRatingBar)
        window.statusBarColor = android.graphics.Color.parseColor("#000000") // black
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0, // no light icons
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        // Optional: show current ratings in TextViews (optional)
        val starText = TextView(this)
        val heartText = TextView(this)
        val emojiText = TextView(this)

        starRatingBar.setOnRatingChangeListener { rating ->
            starText.text = "Star Rating: $rating"
        }

        heartRatingBar.setOnRatingChangeListener { rating ->
            heartText.text = "Heart Rating: $rating"
        }

        emojiRatingBar.setOnRatingChangeListener { rating ->
            emojiText.text = "Emoji Rating: $rating"
        }
    }
}

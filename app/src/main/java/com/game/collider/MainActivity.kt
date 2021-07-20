package com.game.collider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.game.collider.components.GameView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(GameView(this))
    }
}

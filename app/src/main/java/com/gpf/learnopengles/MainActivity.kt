package com.gpf.learnopengles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gpf.learnopengles.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }

    }
}
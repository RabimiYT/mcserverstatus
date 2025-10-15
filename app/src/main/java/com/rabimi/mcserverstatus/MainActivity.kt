package com.rabimi.mcserverstatus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rabimi.mcserverstatus.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.text = "MCServer Status\nTPS / PING / Memory\nSurvival / Lobby / PvP"
    }
}

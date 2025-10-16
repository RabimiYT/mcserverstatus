package com.rabimi.mcserverstatus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.rabimi.mcserverstatus.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar をセット
        setSupportActionBar(binding.toolbar)

        // ダークモード切替ボタン
        binding.darkModeToggle.setOnClickListener {
            isDarkMode = !isDarkMode
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.darkModeToggle.setImageResource(R.drawable.ic_moon)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.darkModeToggle.setImageResource(R.drawable.ic_sun)
            }
        }

        // サーバー追加ボタン仮置き
        binding.addServerButton.setOnClickListener {
            Snackbar.make(binding.root, "サーバー追加ボタン押された！", Snackbar.LENGTH_SHORT).show()
        }
    }
}
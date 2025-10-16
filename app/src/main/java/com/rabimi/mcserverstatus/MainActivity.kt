package com.rabimi.mcserverstatus

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private lateinit var darkModeToggle: ImageButton
    private lateinit var addServerButton: Button
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        darkModeToggle = findViewById(R.id.darkModeToggle)
        addServerButton = findViewById(R.id.addServerButton)

        // 現在のモードを取得
        val isDark = prefs.getBoolean("dark_mode", false)
        setDarkMode(isDark)

        darkModeToggle.setOnClickListener {
            val newMode = !prefs.getBoolean("dark_mode", false)
            setDarkMode(newMode)
        }

        addServerButton.setOnClickListener {
            // TODO: サーバー追加処理
        }
    }

    private fun setDarkMode(enabled: Boolean) {
        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkModeToggle.setImageResource(R.drawable.ic_moon)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkModeToggle.setImageResource(R.drawable.ic_sun)
        }
        prefs.edit { putBoolean("dark_mode", enabled) }
    }
}
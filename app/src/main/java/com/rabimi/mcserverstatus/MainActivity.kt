package com.rabimi.mcserverstatus

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val darkModeToggle = findViewById<ImageButton>(R.id.darkModeToggle)
        val addServerButton = findViewById<ImageButton>(R.id.addServerButton)

        // 保存してあるモードに合わせる
        isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        updateToggleIcon(darkModeToggle)

        // ダーク/ライト切替
        darkModeToggle.setOnClickListener {
            isDarkMode = !isDarkMode
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            updateToggleIcon(darkModeToggle)
        }

        // サーバー追加ボタン
        addServerButton.setOnClickListener {
            Toast.makeText(this, "Add Server clicked", Toast.LENGTH_SHORT).show()
            // ここにサーバー追加処理を実装
        }
    }

    private fun updateToggleIcon(button: ImageButton) {
        if (isDarkMode) {
            button.setImageResource(R.drawable.ic_moon)
        } else {
            button.setImageResource(R.drawable.ic_sun)
        }
    }
}
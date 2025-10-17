package com.rabimi.mcserverstatus

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import android.view.animation.AlphaAnimation

class MainActivity : AppCompatActivity() {

    private var isDarkMode = false
    private lateinit var adapter: ServerListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val darkModeToggle = findViewById<ImageButton>(R.id.darkModeToggle)
        val addServerButton = findViewById<ImageButton>(R.id.addServerButton)
        val recyclerView = findViewById<RecyclerView>(R.id.serverRecyclerView)

        // RecyclerView 初期化
        adapter = ServerListAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // デフォルトサーバーを追加
        adapter.addServer(Server("Hypixel", "mc.hypixel.net"))
        adapter.addServer(Server("Minemen (AS)", "as.minemen.club"))

        // ダークモード状態反映
        isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        updateToggleIcon(darkModeToggle)

        // ダークモード切替
        darkModeToggle.setOnClickListener {
            isDarkMode = !isDarkMode
            fadeTransition() // ← フェードアニメーション
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Snackbar.make(it, "Dark Mode 有効", Snackbar.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Snackbar.make(it, "Light Mode 有効", Snackbar.LENGTH_SHORT).show()
            }
            updateToggleIcon(darkModeToggle)
        }

        // サーバー追加ボタン
        addServerButton.setOnClickListener {
            adapter.addServer(Server("New Server", "example.com"))
            Snackbar.make(it, "サーバーを追加しました", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateToggleIcon(button: ImageButton) {
        if (isDarkMode) {
            button.setImageResource(R.drawable.ic_moon)
        } else {
            button.setImageResource(R.drawable.ic_sun)
        }
    }

    private fun fadeTransition() {
        val fade = AlphaAnimation(0.0f, 1.0f)
        fade.duration = 400
        findViewById<RecyclerView>(R.id.serverRecyclerView).startAnimation(fade)
    }
}
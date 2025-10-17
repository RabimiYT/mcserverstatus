package com.rabimi.mcserverstatus

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import androidx.transition.Fade
import androidx.transition.TransitionManager

class MainActivity : AppCompatActivity() {

    private var isDarkMode = false
    private lateinit var serverAdapter: ServerListAdapter
    private lateinit var rootLayout: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val darkModeToggle = findViewById<ImageButton>(R.id.darkModeToggle)
        val addServerButton = findViewById<ImageButton>(R.id.addServerButton)
        rootLayout = findViewById(R.id.serverRecyclerView)

        // RecyclerView設定
        serverAdapter = ServerListAdapter(mutableListOf())
        rootLayout.adapter = serverAdapter
        rootLayout.layoutManager = LinearLayoutManager(this)

        // デフォルトサーバーを追加
        serverAdapter.addServer(Server("Hypixel", "mc.hypixel.net"))
        serverAdapter.addServer(Server("Minemen (AS)", "as.minemen.club"))

        // 保存してあるモードを反映
        isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        updateToggleIcon(darkModeToggle)

        // 🌗 ダーク/ライト切替（フェード付き）
        darkModeToggle.setOnClickListener {
            val fade = Fade()
            val root = window.decorView.findViewById(android.R.id.content) as android.view.ViewGroup
            TransitionManager.beginDelayedTransition(root, fade)

            isDarkMode = !isDarkMode
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Snackbar.make(rootLayout, "Dark mode enabled", Snackbar.LENGTH_SHORT).show()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Snackbar.make(rootLayout, "Light mode enabled", Snackbar.LENGTH_SHORT).show()
            }
            updateToggleIcon(darkModeToggle)
        }

        // ➕ サーバー追加ボタン
        addServerButton.setOnClickListener {
            Snackbar.make(rootLayout, "Add Server clicked", Snackbar.LENGTH_SHORT).show()
            // TODO: サーバー追加ダイアログをここに実装
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
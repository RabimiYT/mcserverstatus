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
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.app.AlertDialog
import android.widget.EditText

data class Server(val name: String, val address: String)

class MainActivity : AppCompatActivity() {

    private var isDarkMode = false
    private lateinit var serverAdapter: ServerListAdapter
    private lateinit var rootLayout: RecyclerView
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val darkModeToggle = findViewById<ImageButton>(R.id.darkModeToggle)
        val addServerButton = findViewById<ImageButton>(R.id.addServerButton)
        rootLayout = findViewById(R.id.serverRecyclerView)

        // RecyclerView設定
        val servers = loadServers()
        serverAdapter = ServerListAdapter(servers)
        rootLayout.adapter = serverAdapter
        rootLayout.layoutManager = LinearLayoutManager(this)

        // 保存してあるモードを反映
        isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        updateToggleIcon(darkModeToggle)

        // 🌗 ダーク/ライト切替（フェード付き + Snackbar）
        darkModeToggle.setOnClickListener {
            val fade = Fade()
            val root = window.decorView.findViewById<ViewGroup>(android.R.id.content)
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
            showAddServerDialog()
        }
    }

    private fun updateToggleIcon(button: ImageButton) {
        if (isDarkMode) {
            button.setImageResource(R.drawable.ic_moon)
        } else {
            button.setImageResource(R.drawable.ic_sun)
        }
    }

    // サーバー追加ダイアログ
    private fun showAddServerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Server")

        val layout = layoutInflater.inflate(R.layout.dialog_add_server, null)
        val nameInput = layout.findViewById<EditText>(R.id.serverNameInput)
        val addressInput = layout.findViewById<EditText>(R.id.serverAddressInput)

        builder.setView(layout)
        builder.setPositiveButton("Add") { _, _ ->
            val name = nameInput.text.toString()
            val address = addressInput.text.toString()
            if (name.isNotEmpty() && address.isNotEmpty()) {
                val newServer = Server(name, address)
                serverAdapter.addServer(newServer)
                saveServers(serverAdapter.servers)
                Snackbar.make(rootLayout, "${newServer.name} added", Snackbar.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // 保存
    private fun saveServers(servers: List<Server>) {
        val sharedPref = getSharedPreferences("server_pref", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val json = gson.toJson(servers)
        editor.putString("servers", json)
        editor.apply()
    }

    // 読み込み
    private fun loadServers(): MutableList<Server> {
        val sharedPref = getSharedPreferences("server_pref", MODE_PRIVATE)
        val json = sharedPref.getString("servers", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Server>>() {}.type
            gson.fromJson(json, type)
        } else {
            // デフォルトサーバー
            mutableListOf(
                Server("Hypixel", "mc.hypixel.net"),
                Server("Minemen (AS)", "as.minemen.club")
            )
        }
    }
}
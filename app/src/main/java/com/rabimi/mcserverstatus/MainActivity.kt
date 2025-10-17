package com.rabimi.mcserverstatus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import androidx.transition.Fade
import androidx.transition.TransitionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import com.google.android.material.R as MaterialR

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

        // 保存済みサーバーを読み込む
        val savedServers = loadServers().toMutableList()
        if (savedServers.isEmpty()) {
            savedServers.add(Server("Hypixel", "mc.hypixel.net"))
            savedServers.add(Server("Minemen (AS)", "as.minemen.club"))
        }

        serverAdapter = ServerListAdapter(savedServers, this)
        rootLayout.adapter = serverAdapter
        rootLayout.layoutManager = LinearLayoutManager(this)

        // ダークモード初期化
        isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        updateToggleIcon(darkModeToggle)

        // ダーク/ライトモード切替
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
            serverAdapter.notifyDataSetChanged()
        }

        // サーバー追加ボタン
        addServerButton.setOnClickListener { showAddServerDialog() }

        // 🔹 サーバー状態を5秒ごとに自動更新
        startAutoUpdate()
    }

    // 🔹 自動更新処理
    private fun startAutoUpdate() {
        lifecycleScope.launch {
            while (true) {
                serverAdapter.servers.forEachIndexed { index, server ->
                    launch(Dispatchers.IO) {
                        val online = isServerOnline(server.address)
                        Log.d("AutoUpdate", "${server.name} isOnline=$online") // 確認用
                        withContext(Dispatchers.Main) {
                            server.isOnline = online
                            serverAdapter.notifyItemChanged(index)
                        }
                    }
                }
                delay(5000L)
            }
        }
    }

    // 🔹 サーバー接続確認
    private fun isServerOnline(address: String): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(address, 25565), 2000)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    // 🔹 サーバー追加ダイアログ
    private fun showAddServerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_server, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.serverNameInput)
        val addressInput = dialogView.findViewById<EditText>(R.id.serverAddressInput)

        androidx.appcompat.app.AlertDialog.Builder(this, MaterialR.style.ThemeOverlay_Material3_Dialog_Alert)
            .setTitle("Add New Server")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().trim()
                val address = addressInput.text.toString().trim()
                if (name.isNotEmpty() && address.isNotEmpty()) {
                    val newServer = Server(name, address)
                    serverAdapter.addServer(newServer)
                    saveServers(serverAdapter.servers)
                    Snackbar.make(rootLayout, "Server added!", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(rootLayout, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // 🔹 ダークモードアイコン切替
    private fun updateToggleIcon(button: ImageButton) {
        button.setImageResource(
            if (isDarkMode) R.drawable.ic_dark_mode else R.drawable.ic_light_mode
        )
    }

    // 🔹 サーバーリスト保存
    private fun saveServers(servers: List<Server>) {
        val prefs = getSharedPreferences("server_prefs", MODE_PRIVATE)
        val editor = prefs.edit()
        val serialized = servers.joinToString("|") { "${it.name},${it.address}" }
        editor.putString("servers", serialized)
        editor.apply()
    }

    // 🔹 サーバーリスト読み込み
    private fun loadServers(): List<Server> {
        val prefs = getSharedPreferences("server_prefs", MODE_PRIVATE)
        val serialized = prefs.getString("servers", null) ?: return emptyList()
        return serialized.split("|").mapNotNull {
            val parts = it.split(",")
            if (parts.size == 2) Server(parts[0], parts[1]) else null
        }
    }
}
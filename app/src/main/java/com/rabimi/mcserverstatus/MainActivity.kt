package com.rabimi.mcserverstatus

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
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

        val savedServers = loadServers().toMutableList()
        if (savedServers.isEmpty()) {
            savedServers.add(Server("Hypixel", "mc.hypixel.net"))
            savedServers.add(Server("Minemen (AS)", "as.minemen.club"))
        }

        serverAdapter = ServerListAdapter(savedServers, this)
        rootLayout.adapter = serverAdapter
        rootLayout.layoutManager = LinearLayoutManager(this)

        isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        updateToggleIcon(darkModeToggle)

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

        addServerButton.setOnClickListener { showAddServerDialog() }

        // サーバー状態を5秒ごとに更新
        startAutoUpdate()
    }

    private fun startAutoUpdate() {
        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                val servers = serverAdapter.servers
                for (server in servers) {
                    val online = isServerOnline(server.address)
                    withContext(Dispatchers.Main) {
                        server.isOnline = online
                        serverAdapter.notifyItemChanged(servers.indexOf(server))
                    }
                }
                delay(5000L)
            }
        }
    }

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

    private fun showAddServerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_server, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.serverNameInput)
        val addressInput = dialogView.findViewById<EditText>(R.id.serverAddressInput)

        // Material3 テーマに変更
        AlertDialog.Builder(this, com.google.android.material.R.style.ThemeOverlay_Material3_Dialog_Alert)
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

    private fun updateToggleIcon(button: ImageButton) {
        button.setImageResource(
            if (isDarkMode) R.drawable.ic_dark_mode else R.drawable.ic_light_mode
        )
    }

    // loadServers / saveServers は既存のまま使用
}
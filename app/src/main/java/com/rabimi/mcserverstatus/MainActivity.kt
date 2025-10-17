package com.rabimi.mcserverstatus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import androidx.transition.Fade
import androidx.transition.TransitionManager
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var isDarkMode = false
    private lateinit var serverAdapter: ServerListAdapter
    private lateinit var rootLayout: RecyclerView
    private val PREFS_NAME = "servers_prefs"
    private val SERVERS_KEY = "servers"

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

        // Adapter に context を渡す
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
        }

        addServerButton.setOnClickListener {
            showAddServerDialog()
        }
    }

    private fun showAddServerDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_server, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.serverNameInput)
        val addressInput = dialogView.findViewById<EditText>(R.id.serverAddressInput)

        AlertDialog.Builder(this)
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
        if (isDarkMode) button.setImageResource(R.drawable.ic_moon)
        else button.setImageResource(R.drawable.ic_sun)
    }

    private fun saveServers(servers: List<Server>) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        for (server in servers) {
            val obj = JSONObject()
            obj.put("name", server.name)
            obj.put("address", server.address)
            jsonArray.put(obj)
        }
        prefs.edit().putString(SERVERS_KEY, jsonArray.toString()).apply()
    }

    private fun loadServers(): List<Server> {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(SERVERS_KEY, null) ?: return emptyList()
        val jsonArray = JSONArray(jsonString)
        val servers = mutableListOf<Server>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            servers.add(Server(obj.getString("name"), obj.getString("address")))
        }
        return servers
    }
}
package com.rabimi.mcserverstatus

import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.system.measureTimeMillis

data class ServerStatus(
    val tps: Double?, // ほとんどのパブリックサーバーではnull
    val ping: Int?,
    val onlinePlayers: Int?,
    val maxPlayers: Int?
)

class ServerDetailActivity : AppCompatActivity() {

    private lateinit var serverNameText: TextView
    private lateinit var serverAddressText: TextView
    private lateinit var pingText: TextView
    private lateinit var onlineText: TextView
    private lateinit var tpsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server_detail)

        // Toolbar設定
        val toolbar = findViewById<Toolbar>(R.id.detailToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 戻るボタン
        toolbar.setNavigationOnClickListener { finish() }

        serverNameText = findViewById(R.id.detailServerName)
        serverAddressText = findViewById(R.id.detailServerAddress)
        pingText = findViewById(R.id.detailPing)
        onlineText = findViewById(R.id.detailOnline)
        tpsText = findViewById(R.id.detailTPS)

        val name = intent.getStringExtra("server_name") ?: "Unknown"
        val address = intent.getStringExtra("server_address") ?: "Unknown"

        serverNameText.text = name
        serverAddressText.text = address
        supportActionBar?.title = name // Toolbarにサーバー名表示

        // 非同期でサーバー状態取得
        GetServerStatusTask(address, 25565) { status ->
            pingText.text = "Ping: ${status.ping ?: "null"}"
            onlineText.text = if (status.onlinePlayers != null && status.maxPlayers != null)
                "Online: ${status.onlinePlayers}/${status.maxPlayers}" else "Online: null"
            tpsText.text = "TPS: ${status.tps ?: "null"}"
        }.execute()
    }

    private class GetServerStatusTask(
        val host: String,
        val port: Int,
        val callback: (ServerStatus) -> Unit
    ) : AsyncTask<Unit, Unit, ServerStatus>() {
        override fun doInBackground(vararg params: Unit?): ServerStatus {
            return try {
                val socket = Socket()
                val pingTime = measureTimeMillis {
                    socket.connect(InetSocketAddress(host, port), 2000)
                }
                socket.close()
                ServerStatus(
                    tps = null,
                    ping = pingTime.toInt(),
                    onlinePlayers = null,
                    maxPlayers = null
                )
            } catch (e: Exception) {
                ServerStatus(tps = null, ping = null, onlinePlayers = null, maxPlayers = null)
            }
        }

        override fun onPostExecute(result: ServerStatus) {
            super.onPostExecute(result)
            callback(result)
        }
    }
}
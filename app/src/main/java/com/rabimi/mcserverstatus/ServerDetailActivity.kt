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
    val ping: Int?
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        serverNameText = findViewById(R.id.detailServerName)
        serverAddressText = findViewById(R.id.detailServerAddress)
        pingText = findViewById(R.id.detailPing)
        onlineText = findViewById(R.id.detailOnline)
        tpsText = findViewById(R.id.detailTPS)

        val name = intent.getStringExtra("server_name") ?: "Unknown"
        val address = intent.getStringExtra("server_address") ?: "Unknown"
        val isOnline = intent.getBooleanExtra("server_isOnline", false)

        serverNameText.text = name
        serverAddressText.text = address
        supportActionBar?.title = name

        // Online 表示
        onlineText.text = if (isOnline) "Online" else "Offline"
        onlineText.setTextColor(
            if (isOnline) 0xFF4CAF50.toInt() else 0xFFF44336.toInt()
        )

        // 非同期で Ping 取得
        GetServerStatusTask(address, 25565) { status ->
            pingText.text = "Ping: ${status.ping ?: "null"}"
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
                    ping = pingTime.toInt()
                )
            } catch (e: Exception) {
                ServerStatus(tps = null, ping = null)
            }
        }

        override fun onPostExecute(result: ServerStatus) {
            super.onPostExecute(result)
            callback(result)
        }
    }
}
package com.rabimi.mcserverstatus

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServerListAdapter(
    val servers: MutableList<Server>,
    private val context: Context
) : RecyclerView.Adapter<ServerListAdapter.ServerViewHolder>() {

    class ServerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.serverName)
        val address: TextView = itemView.findViewById(R.id.serverAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_server, parent, false)
        return ServerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        val server = servers[position]
        holder.name.text = server.name
        holder.address.text = server.address

        // サーバー名タップで詳細画面へ
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ServerDetailActivity::class.java)
            intent.putExtra("server_name", server.name)
            intent.putExtra("server_address", server.address)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = servers.size

    fun addServer(server: Server) {
        servers.add(server)
        notifyItemInserted(servers.size - 1)
    }
}
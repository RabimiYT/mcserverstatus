package com.rabimi.mcserverstatus

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class ServerListAdapter(
    val servers: MutableList<Server>,
    private val context: Context
) : RecyclerView.Adapter<ServerListAdapter.ServerViewHolder>() {

    class ServerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.serverName)
        val address: TextView = itemView.findViewById(R.id.serverAddress)
        val status: TextView = itemView.findViewById(R.id.serverStatus)
        val menuButton: ImageButton = itemView.findViewById(R.id.menuButton)
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

        // 🔹 オンライン状態表示（ダークモード対応）
        holder.status.text = if (server.isOnline) "Online" else "Offline"
        val onlineColor = if (server.isOnline) 0xFF4CAF50.toInt() else 0xFFF44336.toInt()
        holder.status.setTextColor(onlineColor)

        // 🔹 詳細画面へのクリック
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ServerDetailActivity::class.java)
            intent.putExtra("server_name", server.name)
            intent.putExtra("server_address", server.address)
            intent.putExtra("server_isOnline", server.isOnline)
            context.startActivity(intent)
        }

        // 🔸 「…」メニュー
        holder.menuButton.setOnClickListener { view ->
            val popup = androidx.appcompat.widget.PopupMenu(context, view)
            popup.inflate(R.menu.server_item_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit_name -> {
                        showEditDialog(server, true, position)
                        true
                    }
                    R.id.menu_edit_ip -> {
                        showEditDialog(server, false, position)
                        true
                    }
                    R.id.menu_delete -> {
                        servers.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, servers.size)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount() = servers.size

    fun addServer(server: Server) {
        servers.add(server)
        notifyItemInserted(servers.size - 1)
    }

    private fun showEditDialog(server: Server, isName: Boolean, position: Int) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_add_server, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.serverNameInput)
        val addressInput = dialogView.findViewById<EditText>(R.id.serverAddressInput)

        if (isName) {
            nameInput.setText(server.name)
            addressInput.visibility = View.GONE
        } else {
            addressInput.setText(server.address)
            nameInput.visibility = View.GONE
        }

        // 🔹 ボタンが常に見えるように Material2 テーマを指定
        AlertDialog.Builder(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert)
            .setTitle(if (isName) "Edit Name" else "Edit IP")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                if (isName) server.name = nameInput.text.toString().trim()
                else server.address = addressInput.text.toString().trim()
                notifyItemChanged(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
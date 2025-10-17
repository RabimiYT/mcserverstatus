package com.rabimi.mcserverstatus

data class Server(
    var name: String,
    var address: String,
    var isOnline: Boolean = false
)
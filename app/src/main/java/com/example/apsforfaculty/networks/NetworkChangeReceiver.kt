package com.example.apsforfaculty.networks

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission

class NetworkChangeReceiver(listener: NetworkStatusListener) : BroadcastReceiver() {
    interface NetworkStatusListener {
        fun onNetworkConnected()
        fun onNetworkDisconnected()
    }


    private var listener: NetworkStatusListener? = null

    init {
        this.listener = listener
    }


    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo

        if (activeNetwork != null && activeNetwork.isConnected) {
            listener!!.onNetworkConnected() // Internet is connected
        } else {
            listener!!.onNetworkDisconnected() // No internet connection
        }
    }

    companion object {
        fun registerReceiver(context: Context, receiver: NetworkChangeReceiver) {
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(receiver, filter)
        }

        fun unregisterReceiver(context: Context, receiver: NetworkChangeReceiver) {
            context.unregisterReceiver(receiver)
        }

    }

}
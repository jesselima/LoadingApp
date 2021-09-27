package com.udacity.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.udacity.extensions.isConnected

class ConnectivityReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        connectivityReceiverListener?.onNetworkConnectionChanged(
            isConnected = context?.isConnected() ?: false
        )
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}
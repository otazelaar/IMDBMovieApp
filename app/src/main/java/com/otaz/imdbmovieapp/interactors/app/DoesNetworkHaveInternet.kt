package com.otaz.imdbmovieapp.interactors.app

import android.util.Log
import com.otaz.imdbmovieapp.util.TAG
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.net.SocketFactory

//object DoesNetworkHaveInternet {
//    fun execute(socketFactory: SocketFactory): Boolean{
//        return try {
//            Log.d(TAG, "execute: PINGING GOOGLE")
//            val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
//            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
//            socket.close()
//            Log.d(TAG, "execute: PING SUCCESS")
//            true
//        }catch (e: IOException){
//            Log.e(TAG, "execute: No internet connection $e")
//            false
//        }
//    }
//}
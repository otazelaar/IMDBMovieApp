//package com.otaz.imdbmovieapp.presentation.util
//
//import android.content.Context
//import android.content.Context.CONNECTIVITY_SERVICE
//import android.net.ConnectivityManager
//import android.net.Network
//import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
//import android.net.NetworkRequest
//import android.util.Log
//import androidx.lifecycle.LiveData
//import com.otaz.imdbmovieapp.interactors.app.DoesNetworkHaveInternet
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//val TAG = "C-Manager"
//
///**
// * Save all available networks with an internet connection to a set (@validNetworks).
// * As long as the size of the set > 0, this LiveData emits true.
// * MinSdk = 21.
// *
// * This LiveData class outputs a Boolean and denotes whether or not we have overall
// * internet connectivity in the device. This class is instantiated inside of an app wide singleton class
// * that way it can be injected into viewmodels, activities, use cases, etc.
// *
// * Inspired by:
// * https://github.com/AlexSheva-mason/Rick-Morty-Database/blob/master/app/src/main/java/com/shevaalex/android/rickmortydatabase/utils/networking/ConnectionLiveData.kt
// */
//class ConnectionLiveData(context: Context) : LiveData<Boolean>() {
//
//
//    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
//    private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//    private val validNetworks: MutableSet<Network> = HashSet()
//
//    // the function tells us whether we have overall connectivity
//    private fun checkValidNetworks() {
//        postValue(validNetworks.size > 0)
//    }
//
//    override fun onActive() {
//        networkCallback = createNetworkCallback()
//        val networkRequest = NetworkRequest.Builder()
//            .addCapability(NET_CAPABILITY_INTERNET)
//            .build()
//        cm.registerNetworkCallback(networkRequest, networkCallback)
//    }
//
//    override fun onInactive() {
//        cm.unregisterNetworkCallback(networkCallback)
//    }
//
//    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
//
//        /*
//          Called when a network is detected. If that network has internet, save it in the Set.
//          Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onAvailable(android.net.Network)
//         */
//        override fun onAvailable(network: Network) {
//            Log.d(TAG, "onAvailable: ${network}")
//            val networkCapabilities = cm.getNetworkCapabilities(network)
//            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
//            Log.d(TAG, "onAvailable: ${network}, $hasInternetCapability")
//            if (hasInternetCapability == true) {
//                // check if this network actually has internet
//                CoroutineScope(Dispatchers.IO).launch {
//                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
//                    if(hasInternet){
//                        withContext(Dispatchers.Main){
//                            Log.d(TAG, "onAvailable: This network has internet: ${network}")
//                            validNetworks.add(network)
//                            checkValidNetworks()
//                        }
//                    }
//                }
//            }
//        }
//
//        /*
//          If the callback was registered with registerNetworkCallback() it will be called for each network which no longer satisfies the criteria of the callback.
//          Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onLost(android.net.Network)
//         */
//        override fun onLost(network: Network) {
//            Log.d(TAG, "onLost: ${network}")
//            validNetworks.remove(network)
//            checkValidNetworks()
//        }
//
//    }
//
//}
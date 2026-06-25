package com.meet.navigationdrawerjc.worker

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.meet.navigationdrawerjc.MainActivity
import com.meet.navigationdrawerjc.R
import com.meet.navigationdrawerjc.StopTriggersReceiver
import com.meet.navigationdrawerjc.worker.NotificationUtils
import com.meet.navigationdrawerjc.data.PreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class MyBackgroundService : Service() {

    private val handler = Handler()

    // Runnable to trigger notifications
    private val logRunnable = object : Runnable {
        override fun run() {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (currentHour in 8..22) {  // 8 AM to 8:59 PM
                if (isConnectedToUniversityWifi() && isBatteryLevelSufficient() && isDeviceCharging() ) {
                    Log.d("MyBackgroundService", "Triggering notification every 10 seconds")
                    triggerNotification()
                } else {
                    Log.d("MyBackgroundService", "Conditions not met: either Wi-Fi not connected , battery level is low or charging status is not satisfied")
                }
                handler.postDelayed(this, 2 * 60 * 60 * 1000) // Schedule next run in 2 hours
            } else {
                Log.d("MyBackgroundService", "Stopping service outside 8 AM to 10 PM")
                stopSelf() // Stop the service outside the defined time range
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MyBackgroundService", "Service Created")
        NotificationUtils.createNotificationChannel(this) // Create the notification channel
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyBackgroundService", "Service Started")

        // Start the logging task with an initial delay of 10 seconds
        handler.postDelayed(logRunnable, 10 * 1000)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(logRunnable) // Clean up the handler callbacks
        Log.d("MyBackgroundService", "Service Destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun triggerNotification() {
        // Fetch motivation and ability from PreferencesDataStore
        CoroutineScope(Dispatchers.IO).launch {
            val dataStore = PreferencesDataStore(applicationContext)
            val motivation = dataStore.motivation.first()
            val ability = dataStore.ability.first()
            Log.d("MyBackgroundService", "Motivation: $motivation, Ability: $ability")
            val webpage: Uri = Uri.parse("https://www.health.harvard.edu/topics/diet-and-weight-loss")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                this@MyBackgroundService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val (title, message) = when {
                motivation == 0 && ability == 1 -> {
                    "Feeling low?" to "Take a 5-minute mindful break. You’ve got this!"
                }
                motivation == 1 && ability == 0 -> {
                    "Mindful moment" to "Just breathe with us for 2 minutes.You're doing great! "
                }
                motivation == 1 && ability == 1 -> {
                    "Take a mindful break " to "it's the perfect time to pause and reflect."
                }
                else -> {
                    "Feeling off balance?" to "Recenter with a moment of mindfulness. You choose when."
                }
            }

            // Create the intents for the notification actions
            val adjustSettingsIntent = Intent(this@MyBackgroundService, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val adjustSettingsPendingIntent: PendingIntent = PendingIntent.getActivity(
                this@MyBackgroundService, 0, adjustSettingsIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val stopTriggersIntent = Intent(this@MyBackgroundService, StopTriggersReceiver::class.java)
            val stopTriggersPendingIntent: PendingIntent = PendingIntent.getBroadcast(
                this@MyBackgroundService, 0, stopTriggersIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Build and trigger the notification
            if( motivation == 0 && ability == 1 ){
                val builder = NotificationUtils.createNotificationBuilder(this@MyBackgroundService)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "Adjust Settings", adjustSettingsPendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "Done Triggers", stopTriggersPendingIntent)
                    .setAutoCancel(true)

                with(NotificationManagerCompat.from(this@MyBackgroundService)) {
                    notify(NotificationUtils.NOTIFICATION_ID, builder.build())
                }
            } else {
                val builder = NotificationUtils.createNotificationBuilder(this@MyBackgroundService)
                    .setContentTitle(title)
                    .setContentText(message)
                    .addAction(R.drawable.ic_launcher_foreground, "Adjust Settings", adjustSettingsPendingIntent)
                    .addAction(R.drawable.ic_launcher_foreground, "Done Triggers", stopTriggersPendingIntent)
                    .setAutoCancel(true)

                with(NotificationManagerCompat.from(this@MyBackgroundService)) {
                    notify(NotificationUtils.NOTIFICATION_ID, builder.build())
                }
            }
        }
    }

//    private fun isConnectedToUniversityWifi(): Boolean {
//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val network = connectivityManager.activeNetwork ?: return false
//        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
//
//        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
//            val currentSsid = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                wifiManager.connectionInfo.ssid
//            } else {
//                val info = wifiManager.connectionInfo
//                val ssid = info.ssid
//                if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
//                    ssid.substring(1, ssid.length - 1)
//                } else {
//                    ssid
//                }
//            }
//
//            val ssidString = currentSsid?.let {
//                if (it.startsWith("\"") && it.endsWith("\"")) {
//                    it.substring(1, it.length - 1)
//                } else {
//                    it
//                }
//            } ?: "unknown"
//
//            return ssidString == "LetsPro 5G"
//        }
//        return false
//    }

    private fun isConnectedToUniversityWifi(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    private fun isBatteryLevelSufficient(): Boolean {
        val batteryIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = level / scale.toFloat() * 100
        Log.d("MyBackgroundService", "Battery percentage: $batteryPct%")

        return batteryPct > 5
    }

    private fun isDeviceCharging(): Boolean {
        val batteryIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1

        // Check if the device is charging via AC, USB, or wireless
        return status == BatteryManager.BATTERY_PLUGGED_AC ||
                status == BatteryManager.BATTERY_PLUGGED_USB ||
                status == BatteryManager.BATTERY_PLUGGED_WIRELESS
    }

}

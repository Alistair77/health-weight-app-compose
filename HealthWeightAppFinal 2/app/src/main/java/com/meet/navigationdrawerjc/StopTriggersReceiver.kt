package com.meet.navigationdrawerjc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.meet.navigationdrawerjc.data.PreferencesDataStore
import com.meet.navigationdrawerjc.worker.NotificationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StopTriggersReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Update the triggers setting to disable the service
        val dataStore = PreferencesDataStore(context)
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.updateTriggers(false)
        }

        NotificationManagerCompat.from(context).cancel(NotificationUtils.NOTIFICATION_ID)

    }
}

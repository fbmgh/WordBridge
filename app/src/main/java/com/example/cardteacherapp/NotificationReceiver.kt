package com.example.cardteacherapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            val notification = NotificationCompat.Builder(context, "DailyChannel")
                .setSmallIcon(R.drawable.book)
                .setContentTitle("Daily Reminder")
                .setContentText("It's time to learn English! You already killed the Duo bird, at least keep me safe!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            NotificationManagerCompat.from(context).notify(1, notification)
        }
    }
}

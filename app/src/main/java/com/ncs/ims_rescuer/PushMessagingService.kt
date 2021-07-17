package com.ncs.ims_rescuer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ncs.ims_rescuer.ui.login.LoginActivity

class PushMessagingService : FirebaseMessagingService() {

    lateinit var channelID : String
    lateinit var channelName : String
    lateinit var channeldescript : String

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Refresh Token", token)
    }

    override fun onMessageReceived(remoteMEssage: RemoteMessage) {
        /*super.onMessageReceived(remoteMEssage)
        createNodtificationChannel()
        if(remoteMEssage != null){
            val title = remoteMEssage.notification!!.title ?: ""
            val message = remoteMEssage.notification!!.body ?: ""
            NotificationManagerCompat.from(this).notify(0, createNotification(title, message))
        }*/
    }
    private fun createNotification(title: String, message: String):Notification{
        var intent = Intent(this, MainActivity::class.java).apply {
            putExtra("address", message)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        var notificationBuilder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText("${message}에 응급상황이 발생하였습니다.")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        return notificationBuilder.build()
    }

    private fun createNodtificationChannel(){
        channelID = applicationContext.resources.getString(R.string.default_notification_channel_id)
        channelName = applicationContext.resources.getString(R.string.default_notification_channel_name)
        channeldescript = applicationContext.resources.getString(R.string.default_notification_channel_description)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.e("channelID", channelID)
            val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }

    }

}
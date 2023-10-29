package com.example.produe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class TimerService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(new NotificationChannel("some_id", "Timer running", NotificationManager.IMPORTANCE_DEFAULT));

        // Set up the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "some_id")
                .setContentTitle("Produe Timer")
                .setSmallIcon(R.mipmap.stop_button)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, TimerFragment.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 0));

        // Set a chronometer in the notification
        builder.setUsesChronometer(true);

        startForeground(100, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        super.onStartCommand(intent, flags, startID);

        // Make a toast to make the user know that the service has been started
        Toast.makeText(TimerService.this, "Service started", Toast.LENGTH_LONG).show();

        // Make the service sticky, so that it doesn't get destroyed with the app
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Make a toast to make the user know that the service has been destroyed
        Toast.makeText(TimerService.this, "Service destroyed", Toast.LENGTH_LONG).show();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

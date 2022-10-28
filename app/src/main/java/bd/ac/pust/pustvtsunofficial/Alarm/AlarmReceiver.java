package bd.ac.pust.pustvtsunofficial.Alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Utility;
import bd.ac.pust.pustvtsunofficial.R;

import static android.content.Context.POWER_SERVICE;

public class AlarmReceiver extends BroadcastReceiver{
    Ringtone r;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(r!=null) r.stop();
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
        Toast.makeText(context, "BUS engine is on!", Toast.LENGTH_LONG).show();
        try {
            Intent i=new Intent(context,AlarmReceiver.class);
            i.putExtra("SNOOZE",true);
            PendingIntent pi=PendingIntent.getBroadcast(context,0,i,PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "PUST_VTS")
                    .setSmallIcon(R.mipmap.bus_logo_green)
                    .setContentTitle("PUST VTS")
                    .setContentText("Your bus time is now")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Get ready for the trip"))
                    .setPriority(NotificationCompat.PRIORITY_MAX).addAction(R.mipmap.bus_logo_green,"STOP",pi);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, builder.build());

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            r = RingtoneManager.getRingtone(context, notification);
            r.play();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000*120);
                        r.stop();
                    }catch (Exception e){

                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
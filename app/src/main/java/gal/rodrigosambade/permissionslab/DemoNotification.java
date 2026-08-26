package gal.rodrigosambade.permissionslab;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

final class DemoNotification {
    private static final String CHANNEL = "permission_lab";

    private DemoNotification() {}

    static void send(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null && Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(new NotificationChannel(
                    CHANNEL, context.getString(R.string.notification_channel),
                    NotificationManager.IMPORTANCE_DEFAULT));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL)
                .setSmallIcon(R.drawable.ic_permissions)
                .setContentTitle("Permiso de notificaciones operativo")
                .setContentText("Esta notificación forma parte del laboratorio.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(context).notify(1001, builder.build());
        } catch (SecurityException ignored) {
            // El caller mostrará el estado actual del permiso.
        }
    }
}

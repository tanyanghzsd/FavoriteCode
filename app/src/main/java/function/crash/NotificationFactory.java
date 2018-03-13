package function.crash;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yuanzhiwu on 16-9-6.
 */
public class NotificationFactory {
    public static Notification getNotification(Context context, int iconId, CharSequence tickerText,
                                               CharSequence title, CharSequence content, PendingIntent contentIntent,
                                               PendingIntent deleteIntent) {
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification =
                    new Notification.Builder(context).setSmallIcon(iconId).setTicker(tickerText)
                            .setWhen(System.currentTimeMillis()).setContentTitle(title)
                            .setContentText(content).setContentIntent(contentIntent)
                            .setDeleteIntent(deleteIntent).build();
        } else {
            notification = new Notification(iconId, tickerText, System.currentTimeMillis());
            try {
                Method setLatestEventInfoMethod = notification.getClass()
                        .getDeclaredMethod("setLatestEventInfo",
                                new Class[]{Context.class, CharSequence.class, CharSequence.class,
                                        PendingIntent.class});
                setLatestEventInfoMethod
                        .invoke(notification, context, title, content, contentIntent);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            notification.deleteIntent = deleteIntent;
        }
        return notification;
    }
}

package function.crash;

import android.content.Context;
import android.content.Intent;

/**
 * Created by kingyang on 2017/3/17.
 */

public class IntentInvoker {

    /**
     * @param context
     * @param intent
     * @param entrance
     * @return 是否成功
     */
    public static boolean startActivity(Context context, Intent intent, int entrance) {
        boolean success = false;
        try {
            context.startActivity(intent);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean startActivity(Context context, Intent intent) {
        return startActivity(context, intent, -1);
    }

    public static boolean startService(Context context, Intent intent, int entrance) {
        boolean success = false;
        try {
            context.startService(intent);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean startService(Context context, Intent intent) {
        return startActivity(context, intent, -1);
    }
}

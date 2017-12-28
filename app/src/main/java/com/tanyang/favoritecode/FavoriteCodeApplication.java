package com.tanyang.favoritecode;

import android.app.Application;
import android.content.Context;

/**
 * Created by tanyang on 17-12-28.
 */

public class FavoriteCodeApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    static public Context getContext(){
        return context;
    }
}

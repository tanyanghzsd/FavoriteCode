package com.tanyang.favoritecode;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import function.task.TaskLoader;

/**
 * Created by tanyang on 2017/12/6.
 */

public class MainActivity extends Activity{

    private static TaskLoader taskLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_main);

        taskLoader = TaskLoader.getInstance();

        taskLoader.loadDataTask();
    }
}

package function.task;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by tanyang on 2017/12/6.
 */

public class TaskLoader {

    private static final String TAG = "TaskLoader";

    private static TaskLoader instance;

    private Context mContext;

    private Handler workerHandler;

    private HandlerThread workerThread;

    private WorkerTask workerTask;

    private Object mTaskLock = new Object();

    public TaskLoader() {
        workerHandler = new Handler();
        workerThread = new HandlerThread("worker thread");
        workerTask = new WorkerTask();
    }

    public static TaskLoader getInstance() {
        if (instance == null) {
            synchronized (TaskLoader.class) {
                if (instance == null) {
                    instance = new TaskLoader();
                }
            }
        }
        return instance;
    }


    private class WorkerTask implements Runnable {
        private boolean mIsCanceled;

        @Override
        public void run() {
            synchronized (mTaskLock) {
                workerTask = null;
            }
            if (!mIsCanceled) {
                doSomethingWasteTime();
            }

        }

        private void doSomethingWasteTime() {
            Log.d(TAG, "do something to waste time");
        }

        public void start() {
            workerHandler.post(this);
        }

        public void cancel() {
            Log.d(TAG, "cancel task");
            workerHandler.removeCallbacks(this);
            mIsCanceled = true;
            workerTask = null;
        }
    }


    public void loadDataTask() {
        synchronized (mTaskLock) {
            if (workerTask != null) {
                workerTask.start();
            }

        }
    }


}

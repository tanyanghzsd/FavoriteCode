package function.thread;

import android.os.Build;
import android.os.Process;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tanyang on 18-3-6.
 */

public class ThreadPoolManager {
    private static HashMap<String, ThreadPoolManager> sThreadPoolManagerhHashMap = new HashMap();
    private static final int DEFAULT_COREPOOL_SIZE = 4;
    private static final int DEFAULT_MAXIMUMPOOL_SIZE = 4;
    private static final long DEFAULT_KEEPALIVE_TIME = 0L;
    private static final TimeUnit DEFAULT_TIMEUNIT;
    private ThreadPoolExecutor mWorkThreadPool;
    private Queue<Runnable> mWaitTasksQueue;
    private RejectedExecutionHandler mRejectedExecutionHandler;
    private Object mLock;
    private String mName;

    private ThreadPoolManager() {
        this(4, 4, 0L, DEFAULT_TIMEUNIT, false, (ThreadPoolManager.ITaskExecuteListener) null);
    }

    private ThreadPoolManager(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, boolean isPriority, final ThreadPoolManager.ITaskExecuteListener listener) {
        this.mWorkThreadPool = null;
        this.mWaitTasksQueue = null;
        this.mRejectedExecutionHandler = null;
        this.mLock = new Object();
        this.mWaitTasksQueue = new ConcurrentLinkedQueue();
        this.initRejectedExecutionHandler();
        BlockingQueue<Runnable> queue = (BlockingQueue) (isPriority ? new PriorityBlockingQueue(16) : new LinkedBlockingQueue(16));
        if (listener == null) {
            this.mWorkThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, this.mRejectedExecutionHandler);
        } else {
            this.mWorkThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, this.mRejectedExecutionHandler) {
                protected void beforeExecute(Thread t, Runnable r) {
                    listener.beforeExecute(t, r);
                }

                protected void afterExecute(Runnable r, Throwable t) {
                    listener.afterExecute(r, t);
                }
            };
        }

    }

    public static ThreadPoolManager getInstance(String threadPoolManagerName) {
        ThreadPoolManager threadPoolManager = null;
        if (threadPoolManagerName != null && !"".equals(threadPoolManagerName.trim())) {
            HashMap var2 = sThreadPoolManagerhHashMap;
            synchronized (sThreadPoolManagerhHashMap) {
                threadPoolManager = (ThreadPoolManager) sThreadPoolManagerhHashMap.get(threadPoolManagerName);
                if (null == threadPoolManager) {
                    threadPoolManager = new ThreadPoolManager();
                    threadPoolManager.mName = threadPoolManagerName;
                    sThreadPoolManagerhHashMap.put(threadPoolManagerName, threadPoolManager);
                }
            }
        }

        return threadPoolManager;
    }

    public static ThreadPoolManager buildInstance(String threadPoolManagerName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        return buildInstance(threadPoolManagerName, corePoolSize, maximumPoolSize, keepAliveTime, unit, false);
    }

    public static ThreadPoolManager buildInstance(String threadPoolManagerName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, boolean isPriority) {
        return buildInstance(threadPoolManagerName, corePoolSize, maximumPoolSize, keepAliveTime, unit, isPriority, (ThreadPoolManager.ITaskExecuteListener) null);
    }

    public static ThreadPoolManager buildInstance(String threadPoolManagerName, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, boolean isPriority, ThreadPoolManager.ITaskExecuteListener listener) {
        if (threadPoolManagerName != null && !"".equals(threadPoolManagerName.trim()) && corePoolSize >= 0 && maximumPoolSize > 0 && maximumPoolSize >= corePoolSize && keepAliveTime >= 0L) {
            ThreadPoolManager threadPoolManager = new ThreadPoolManager(corePoolSize, maximumPoolSize, keepAliveTime, unit, isPriority, listener);
            threadPoolManager.mName = threadPoolManagerName;
            HashMap var9 = sThreadPoolManagerhHashMap;
            synchronized (sThreadPoolManagerhHashMap) {
                sThreadPoolManagerhHashMap.put(threadPoolManagerName, threadPoolManager);
                return threadPoolManager;
            }
        } else {
            return null;
        }
    }

    private void executeWaitTask() {
        Object var1 = this.mLock;
        synchronized (this.mLock) {
            if (this.hasMoreWaitTask()) {
                Runnable runnable = (Runnable) this.mWaitTasksQueue.poll();
                if (runnable != null) {
                    this.execute(runnable);
                }
            }

        }
    }

    private void initRejectedExecutionHandler() {
        this.mRejectedExecutionHandler = new RejectedExecutionHandler() {
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                synchronized (ThreadPoolManager.this.mLock) {
                    ThreadPoolManager.this.mWaitTasksQueue.offer(r);
                }
            }
        };
    }

    public boolean hasMoreWaitTask() {
        boolean result = false;
        if (!this.mWaitTasksQueue.isEmpty()) {
            result = true;
        }

        return result;
    }

    public void execute(Runnable task) {
        if (task != null) {
            this.mWorkThreadPool.execute(task);
        }

    }

    public void cancel(Runnable task) {
        if (task != null) {
            Object var2 = this.mLock;
            synchronized (this.mLock) {
                if (this.mWaitTasksQueue.contains(task)) {
                    this.mWaitTasksQueue.remove(task);
                }
            }

            this.mWorkThreadPool.remove(task);
        }

    }

    public void removeAllTask() {
        try {
            if (!this.mWorkThreadPool.isShutdown()) {
                BlockingQueue<Runnable> tasks = this.mWorkThreadPool.getQueue();
                Iterator i$ = tasks.iterator();

                while (i$.hasNext()) {
                    Runnable task = (Runnable) i$.next();
                    this.mWorkThreadPool.remove(task);
                }
            }
        } catch (Throwable var4) {
            Log.e("ThreadPoolManager", "removeAllTask " + var4.getMessage());
        }

    }

    public boolean isShutdown() {
        return this.mWorkThreadPool.isShutdown();
    }

    private void cleanUp() {
        if (!this.mWorkThreadPool.isShutdown()) {
            try {
                this.mWorkThreadPool.shutdownNow();
            } catch (Exception var4) {
                ;
            }
        }

        this.mRejectedExecutionHandler = null;
        Object var1 = this.mLock;
        synchronized (this.mLock) {
            this.mWaitTasksQueue.clear();
        }
    }

    public void setThreadFactory(ThreadFactory factory) {
        this.mWorkThreadPool.setThreadFactory(factory);
    }

    public void allowCoreThreadTimeOut(boolean allow) {
        if (Build.VERSION.SDK_INT > 8) {
            this.mWorkThreadPool.allowCoreThreadTimeOut(allow);
        }

    }

    public String getManagerName() {
        return this.mName;
    }

    public static void destroyAll() {
        HashMap var0 = sThreadPoolManagerhHashMap;
        synchronized (sThreadPoolManagerhHashMap) {
            Set<String> keySet = sThreadPoolManagerhHashMap.keySet();
            if (keySet != null && keySet.size() > 0) {
                ThreadPoolManager threadPoolManager = null;
                Iterator i$ = keySet.iterator();

                while (i$.hasNext()) {
                    String key = (String) i$.next();
                    threadPoolManager = (ThreadPoolManager) sThreadPoolManagerhHashMap.get(key);
                    if (threadPoolManager != null) {
                        threadPoolManager.cleanUp();
                    }
                }
            }

            sThreadPoolManagerhHashMap.clear();
        }
    }

    public static void destroy(String threadPoolManagerName) {
        HashMap var1 = sThreadPoolManagerhHashMap;
        synchronized (sThreadPoolManagerhHashMap) {
            ThreadPoolManager threadPoolManager = (ThreadPoolManager) sThreadPoolManagerhHashMap.get(threadPoolManagerName);
            if (threadPoolManager != null) {
                threadPoolManager.cleanUp();
            }

        }
    }

    static {
        DEFAULT_TIMEUNIT = TimeUnit.SECONDS;
    }

    public interface ITaskExecuteListener {
        void beforeExecute(Thread var1, Runnable var2);

        void afterExecute(Runnable var1, Throwable var2);
    }

    private static class ScheduledRunnable implements Runnable {
        private ScheduledRunnable() {
        }

        public void run() {
            synchronized (ThreadPoolManager.sThreadPoolManagerhHashMap) {
                Process.setThreadPriority(10);
                Collection<ThreadPoolManager> values = ThreadPoolManager.sThreadPoolManagerhHashMap.values();
                Iterator i$ = values.iterator();

                while (i$.hasNext()) {
                    ThreadPoolManager manager = (ThreadPoolManager) i$.next();
                    manager.executeWaitTask();
                }

            }
        }
    }
}

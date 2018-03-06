package function.thread;

/**
 * Created by tanyang on 18-3-6.
 */

public abstract class AbstractThreadExecutor {
    protected ThreadPoolManager mManager;
    private byte[] mLock = new byte[0];

    public AbstractThreadExecutor() {
    }

    protected abstract ThreadPoolManager initThreadPoolManager();

    protected ThreadPoolManager.ITaskExecuteListener getTaskExecuteListener() {
        return new ThreadPoolManager.ITaskExecuteListener() {
            public void beforeExecute(Thread thread, Runnable task) {
                if (task instanceof AbstractThreadExecutor.MyTask) {
                    AbstractThreadExecutor.MyTask myTask = (AbstractThreadExecutor.MyTask) task;
                    if (myTask.mThreadName != null) {
                        thread.setName(myTask.mThreadName);
                    }

                    thread.setPriority(myTask.mPriority);
                }

            }

            public void afterExecute(Runnable task, Throwable throwable) {
            }
        };
    }

    public void execute(Runnable task) {
        if (this.mManager == null) {
            byte[] var2 = this.mLock;
            synchronized (this.mLock) {
                if (this.mManager == null) {
                    this.mManager = this.initThreadPoolManager();
                }
            }
        }

        this.mManager.execute(task);
    }

    public void execute(Runnable task, String threadName) {
        this.execute(task, threadName, Thread.currentThread().getPriority());
    }

    public void execute(Runnable task, int priority) {
        this.execute(task, (String) null, priority);
    }

    public void execute(Runnable task, String threadName, int priority) {
        AbstractThreadExecutor.MyTask myTask = new AbstractThreadExecutor.MyTask(task);
        myTask.mThreadName = threadName;
        myTask.mPriority = priority;
        this.execute(myTask);
    }

    public void cancel(Runnable task) {
        if (this.mManager != null) {
            this.mManager.cancel(task);
        }

    }

    public void destroy() {
        if (this.mManager != null) {
            ThreadPoolManager.destroy(this.mManager.getManagerName());
            this.mManager = null;
        }

    }

    public static class MyTask implements Runnable {
        public Runnable mTask;
        public int mPriority = 5;
        public String mThreadName;

        public MyTask(Runnable task) {
            this.mTask = task;
        }

        public void run() {
            this.mTask.run();
        }
    }
}

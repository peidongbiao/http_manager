package com.pei.httpmanager;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

public abstract class CallbackExecutor implements Executor {


    /**
     * Android主线程
     */
    public static Executor main() {
        return MainThreadExecutorHolder.mainThreadExecutor;
    }

    /**
     * 不切换线程，在原线程线程执行。一般为网络请求工具的线程池（如okhttp dispatcher线程池）
     */
    public static Executor none() {
        return NoneExecutorHolder.noneExecutor;
    }

    private static class MainThreadExecutorHolder {
        static MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();
    }

    private static class NoneExecutorHolder {
        static NoneExecutor noneExecutor = new NoneExecutor();
    }


    private static class MainThreadExecutor extends CallbackExecutor {

        private final Handler mainHandler;

        public MainThreadExecutor() {
            mainHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void execute(Runnable command) {
            mainHandler.post(command);
        }
    }

    private static class NoneExecutor extends CallbackExecutor {

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}
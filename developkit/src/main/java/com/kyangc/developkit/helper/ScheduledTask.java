package com.kyangc.developkit.helper;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

/**
 * Author:  kyangc
 * Email:   kyangc@gmail.com
 * Date:    2016-01-18
 * Project: Demoplus
 */
public class ScheduledTask extends TimerTask {

    public static final String TAG = "ScheduledTask";

    private Timer mTimer;

    private long mDelayInMillis = 0;

    private long mInterval = Long.MAX_VALUE;

    private long mRepeatTimes = Long.MAX_VALUE;

    private Task mTask;

    private WorkingHandler mHandler;

    private WorkingHandler.WorkingOn mWorkingThread = WorkingHandler.WorkingOn.Main;

    private long mRepeatedTimes = 0;

    @Override
    public void run() {
        if (mTask != null && mHandler != null) {
            if (mRepeatedTimes < mRepeatTimes) {
                mHandler.startTask();
                mRepeatedTimes++;
            } else {
                mHandler.finishedTask();
            }
        }
    }

    public ScheduledTask setDelayInMillis(long delayInMillis) {
        mDelayInMillis = delayInMillis;
        return this;
    }

    public ScheduledTask setInterval(long interval) {
        mInterval = interval;
        return this;
    }

    public ScheduledTask setRepeatTimes(long repeatTimes) {
        mRepeatTimes = repeatTimes;
        return this;
    }

    public ScheduledTask setTask(Task task) {
        mTask = task;
        return this;
    }

    public ScheduledTask setWorkingThread(
            WorkingHandler.WorkingOn workingThread) {
        mWorkingThread = workingThread;
        return this;
    }

    public void start() {
        //Init handler
        mHandler = WorkingHandler.getInstance(mWorkingThread);
        mHandler.setTask(mTask);
        mHandler.preStartTask();

        //Reset timer
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }

        //Start task
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(this, mDelayInMillis, mInterval);
    }

    public void stop() {
        //cancel task
        cancel();
        if (mHandler != null) {
            mHandler.cancelTask();
        }

        //Stop timer
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    public void destroy() {
        stop();
        mHandler = null;
        mTask = null;
    }

    public static class WorkingHandler extends Handler {

        public static final int MSG_TYPE_START = 0;

        public static final int MSG_TYPE_FINISHED = 1;

        public static final int MSG_TYPE_PRE_START = 2;

        public static final int MSG_TYPE_CANCELED = 3;

        public static final String MSG_TYPE = "msg_type";

        private Task mTask = null;

        public WorkingHandler(Looper looper) {
            super(looper);
        }

        public static WorkingHandler getInstance(WorkingOn thread) {
            switch (thread) {
                case Main:
                    return new WorkingHandler(Looper.getMainLooper());
                case NewThread:
                    HandlerThread workerThread = new HandlerThread("worker");
                    return new WorkingHandler(workerThread.getLooper());
                case Current:
                    return new WorkingHandler(Looper.myLooper());
                default:
                    return null;
            }
        }

        public WorkingHandler setTask(Task task) {
            mTask = task;
            return this;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if (data != null) {
                int type = data.getInt(MSG_TYPE, -1);
                switch (type) {
                    case MSG_TYPE_START:
                        if (mTask != null) {
                            mTask.onExecute();
                        }
                        break;
                    case MSG_TYPE_PRE_START:
                        if (mTask != null) {
                            mTask.onPreExecuted();
                        }
                        break;
                    case MSG_TYPE_FINISHED:
                        if (mTask != null) {
                            mTask.onFinished();
                        }
                        break;
                    case MSG_TYPE_CANCELED:
                        if (mTask != null) {
                            mTask.onStopped();
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        public void preStartTask() {
            Message msgStart = new Message();
            Bundle data = new Bundle();
            data.putInt(MSG_TYPE, MSG_TYPE_PRE_START);
            msgStart.setData(data);
            sendMessage(msgStart);
        }

        public void startTask() {
            Message msgStart = new Message();
            Bundle data = new Bundle();
            data.putInt(MSG_TYPE, MSG_TYPE_START);
            msgStart.setData(data);
            sendMessage(msgStart);
        }

        public void finishedTask() {
            Message msgStart = new Message();
            Bundle data = new Bundle();
            data.putInt(MSG_TYPE, MSG_TYPE_FINISHED);
            msgStart.setData(data);
            sendMessage(msgStart);
        }

        public void cancelTask() {
            Message msgStart = new Message();
            Bundle data = new Bundle();
            data.putInt(MSG_TYPE, MSG_TYPE_CANCELED);
            msgStart.setData(data);
            sendMessage(msgStart);
        }

        public enum WorkingOn {
            Main, NewThread, Current
        }
    }

    public static abstract class Task {

        public abstract void onExecute();

        public void onFinished() {
        }

        public void onPreExecuted() {
        }

        public void onStopped() {
        }
    }
}

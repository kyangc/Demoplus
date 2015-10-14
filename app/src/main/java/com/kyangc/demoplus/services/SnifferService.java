package com.kyangc.demoplus.services;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;

import cn.trinea.android.common.util.ShellUtils;
import timber.log.Timber;

public class SnifferService extends Service {

    public static final String TAG = "Sniffer Service";

    public static final String COMMAND_START_SNIFFER_VERBOSE
            = "tcpdump -vv -s 0 '(tcp and (not arp) and (not icmp))'";

    public static final String COMMAND_START_SNIFFER_STORAGE
            = "tcpdump -vv -s 0 '(tcp and (not arp) and (not icmp))' -w /sdcard/pcaps/";

    public static final String COMMAND_START_SNIFFER_STOP = "killall tcpdump";

    public static final int TCPDUMP_RUNNING_TYPE_VERBOSE = 0;

    public static final int TCPDUMP_RUNNING_TYPE_STORAGE = 1;

    public static final String PCAPNG_FILE_NAME = "sniffered_data_";

    public static final String PCAPNG_FILE_SUFFIX = ".pcap";

    public String[] prepareTcpCommands =
            {
                    "mount -o remount, rw /system",
                    "cp /sdcard/tcpdump /system/bin",
                    "cd /system/bin",
                    "chmod 777 tcpdump",
                    "mount -o remount,ro /system"
            };

    private SnifferService context;

    private OnTaskFinishListener onTaskFinishListener = null;

    private boolean isTcpdumpRunning = false;

    private boolean isTcpdumpPrepared = false;

    private SnifferBinder binder = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onTaskFinishListener = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null) {
            binder = new SnifferBinder();
        }

        if (!isTcpdumpPrepared) {
            try {
                prepareTcpdump();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (RootDeniedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return binder;
    }

    private String getStorageName() {
        Calendar day = Calendar.getInstance();
        String date = (day.get(Calendar.MONTH) + 1) + "-"
                + day.get(Calendar.DAY_OF_MONTH) + "_"
                + day.get(Calendar.HOUR_OF_DAY) + ":"
                + day.get(Calendar.MINUTE) + ":"
                + day.get(Calendar.SECOND);
        return PCAPNG_FILE_NAME + date + PCAPNG_FILE_SUFFIX;
    }

    private Command getPrepareTcpdumpCommand() {
        return new Command(0, prepareTcpCommands) {
            @Override
            public void commandOutput(int id, String line) {
                if (onTaskFinishListener != null) {
                    onTaskFinishListener.onCommandRunning(line);
                }
                super.commandOutput(id, line);
            }

            @Override
            public void commandCompleted(int id, int exitcode) {
                super.commandCompleted(id, exitcode);
                isTcpdumpPrepared = true;
                if (onTaskFinishListener != null) {
                    onTaskFinishListener.onSnifferPrepared(OnTaskFinishListener.STATUS_CODE_SUCCEED,
                            "exit code = " + exitcode);
                }
            }

            @Override
            public void commandTerminated(int id, String reason) {
                super.commandTerminated(id, reason);
                isTcpdumpPrepared = false;
                if (onTaskFinishListener != null) {
                    onTaskFinishListener
                            .onSnifferPrepared(OnTaskFinishListener.STATUS_CODE_FAILED, reason);
                }
            }
        };
    }

    private Command getStartSnifferCommand(int type) {
        String command = null;
        switch (type) {
            case TCPDUMP_RUNNING_TYPE_VERBOSE:
                command = COMMAND_START_SNIFFER_VERBOSE;
                break;
            case TCPDUMP_RUNNING_TYPE_STORAGE:
                command = COMMAND_START_SNIFFER_STORAGE + getStorageName();
                break;
            default:
                break;
        }

        return new Command(0, command) {
            @Override
            public void commandOutput(int id, String line) {
                if (onTaskFinishListener != null) {
                    onTaskFinishListener.onCommandRunning(line);
                } else {
                    Timber.i(line);
                }
                super.commandOutput(id, line);
            }
        };
    }

    private void prepareTcpdump() throws TimeoutException, RootDeniedException, IOException {
        RootShell.getShell(true).add(getPrepareTcpdumpCommand());
        if (onTaskFinishListener != null) {
            onTaskFinishListener.onSnifferPrepared(OnTaskFinishListener.STATUS_CODE_SUCCEED, null);
        }
    }

    private void startSniffer(int type) throws TimeoutException, RootDeniedException, IOException {
        if (!isTcpdumpRunning && isTcpdumpPrepared) {
            switch (type) {
                case TCPDUMP_RUNNING_TYPE_VERBOSE:
                    RootShell.getShell(true)
                            .add(getStartSnifferCommand(TCPDUMP_RUNNING_TYPE_VERBOSE));
                    isTcpdumpRunning = true;
                    if (onTaskFinishListener != null) {
                        onTaskFinishListener
                                .onSnifferStarted(OnTaskFinishListener.STATUS_CODE_SUCCEED, null);
                    }
                    break;
                case TCPDUMP_RUNNING_TYPE_STORAGE:
                    File file = new File(getStorageDir());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    RootShell.getShell(true)
                            .add(getStartSnifferCommand(TCPDUMP_RUNNING_TYPE_STORAGE));
                    isTcpdumpRunning = true;
                    if (onTaskFinishListener != null) {
                        onTaskFinishListener
                                .onSnifferStarted(OnTaskFinishListener.STATUS_CODE_SUCCEED, null);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void stopSniffer() {
        ShellUtils.execCommand(COMMAND_START_SNIFFER_STOP, true);
        isTcpdumpRunning = false;
        if (onTaskFinishListener != null) {
            onTaskFinishListener.onSnifferStopped(OnTaskFinishListener.STATUS_CODE_SUCCEED, null);
        }
    }

    private String getStorageDir() {
        return Environment.getExternalStorageDirectory() + java.io.File.separator + "pcaps/";
    }

    public static abstract class OnTaskFinishListener {

        static int STATUS_CODE_SUCCEED = 0;

        static int STATUS_CODE_FAILED = 1;

        WeakReference<Context> mContextWeakReference;

        public OnTaskFinishListener(Context context) {
            mContextWeakReference = new WeakReference<>(context);
        }

        public abstract void onCommandRunning(String line);

        public abstract void onSnifferPrepared(int statusCode, String errorMsg);

        public abstract void onSnifferStarted(int statusCode, String errorMsg);

        public abstract void onSnifferStopped(int statusCode, String errorMsg);
    }

    public class SnifferBinder extends Binder {

        public void prepare() throws IOException, RootDeniedException, TimeoutException {
            prepareTcpdump();
        }

        public void start(int runningType)
                throws IOException, RootDeniedException, TimeoutException {
            startSniffer(runningType);
        }

        public void stop() {
            stopSniffer();
        }

        public String getPcapFilePath() {
            return getStorageDir();
        }

        public boolean isSnifferRunning() {
            return isTcpdumpRunning;
        }

        public boolean isSnifferReady() {
            return isTcpdumpPrepared;
        }

        public void setListener(OnTaskFinishListener handler) {
            onTaskFinishListener = handler;
        }

        public void clearListener() {
            onTaskFinishListener = null;
        }
    }
}

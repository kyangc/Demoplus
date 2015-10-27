package com.kyangc.developkit.utils;

/**
 * Created by chengkangyang on 十月.27.2015
 */
public class MemoryUtils {

    public static final int STRATEGY_RECYCLE_LEV_LOW = 100;

    public static final int STRATEGY_RECYCLE_LEV_MID = 101;

    public static final int STRATEGY_RECYCLE_LEV_HEIGH = 102;

    public static long getMaxMem() {
        return (long) ((int) Runtime.getRuntime().maxMemory());
    }

    public static long getTotalMem() {
        return (long) ((int) Runtime.getRuntime().totalMemory());
    }

    public static long getAvailableMem() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        return maxMemory - totalMemory + freeMemory;
    }

    public static int getInstanceNum(boolean isMaxDeepStopNumWanted) {
        // maxDeepStopNum, max number of instances in deep stop status
        int maxDeepStopNum = 2;
        // maxInstanceNum, max number of instances alive
        int maxInstanceNum = 10;
        if (MemoryUtils.getStrategyLev() == MemoryUtils.STRATEGY_RECYCLE_LEV_HEIGH) {
            maxDeepStopNum = 1;
            maxInstanceNum = 3;
        }
        return isMaxDeepStopNumWanted ? maxDeepStopNum : maxInstanceNum;
    }

    public static int getStrategyLev() {
        int lev = STRATEGY_RECYCLE_LEV_LOW;
        long maxMemory = getMaxMem();
        if (maxMemory < 70000000) {
            lev = STRATEGY_RECYCLE_LEV_HEIGH;
        } else if (maxMemory < 120000000) {
            lev = STRATEGY_RECYCLE_LEV_MID;
        }
        return lev;
    }
}

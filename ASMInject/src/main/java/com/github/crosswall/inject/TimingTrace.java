package com.github.crosswall.inject;

import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class TimingTrace {

    private static final String TAG = "TimingTrace";

    private static Map<String,Long> startMap = new HashMap<>();

    public static void start(String name){
        startMap.put(name, SystemClock.elapsedRealtimeNanos());
    }

    public static void end(String name){
        long cost =  SystemClock.elapsedRealtimeNanos() - startMap.get(name);
        Log.d(TAG,"测试方法耗时====>>> " + name + " ---- " + cost / 1000000.f + "ms");
    }
}

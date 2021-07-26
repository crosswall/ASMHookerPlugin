package com.github.crosswall.inject;

import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;

public class TimingTrace {

    private static Map<String,Long> startMap = new HashMap<>();

    public static void start(String name){
        startMap.put(name, SystemClock.elapsedRealtimeNanos());
    }

    public static void end(String name){
        long cost =  SystemClock.elapsedRealtimeNanos() - startMap.get(name);
        InjectLog.d("TimingTrace 测试方法耗时====>>> " + name + " ---- " + cost / 1000000.f + "ms");
    }
}

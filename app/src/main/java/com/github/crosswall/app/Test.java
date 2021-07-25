package com.github.crosswall.app;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Test {

    private static Map<String,Long> startMap = new HashMap<>();

    public static void start(String name){
        startMap.put(name,System.currentTimeMillis());
    }

    public static void end(String name){
        long cost =  System.currentTimeMillis() - startMap.get(name);
        Log.d("Test","测试方法耗时====>>> " + name + " ---- " + cost + "ms");
    }
}

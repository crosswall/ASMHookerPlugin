package com.github.crosswall.app;

import android.util.Log;

public class Test {

    public static void start(String name){
        Log.d("Test","enter: " + name + " - " + System.currentTimeMillis());
    }

    public static void end(String name){
        Log.d("Test","exit: " + name + " - " + System.currentTimeMillis());
    }
}

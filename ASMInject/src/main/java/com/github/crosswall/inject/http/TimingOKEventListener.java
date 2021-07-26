package com.github.crosswall.inject.http;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.Call;
import okhttp3.EventListener;

public class TimingOKEventListener extends EventListener{

    private static final String TAG = "TimingTrace";

    public static final EventListener.Factory FACTORY =  new EventListener.Factory() {

        AtomicLong nextCallId = new AtomicLong(1L);

        @Override
        public EventListener create(Call call) {
            long callId = nextCallId.getAndIncrement();
            return new TimingOKEventListener(callId);
        }

    };

    private long callId;
    private long callStartNanos = 0L;
    private boolean isNewConnection = false;

    public TimingOKEventListener(long callId) {
        this.callId = callId;
    }

    @Override
    public void callStart(Call call) {
        callStartNanos = SystemClock.elapsedRealtimeNanos();
    }

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        isNewConnection = true;
    }

    @Override
    public void callEnd(Call call) {
        printResult(true, call);
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        printResult(false, call);
    }

    private void printResult(boolean success, Call call) {
        float elapsed = (SystemClock.elapsedRealtimeNanos() - callStartNanos) / 1000000.f;
        String from = isNewConnection ? "newest connection" : "pooled connection";
        String url = call.request().url().toString();
        String result = String.format("%04d %s Call From %s costs %.3f ms, url %s", callId, success ? "Success" : "Fail", from, elapsed, url);
        Log.i(TAG, "测试请求耗时====>>>" + result);
    }

}

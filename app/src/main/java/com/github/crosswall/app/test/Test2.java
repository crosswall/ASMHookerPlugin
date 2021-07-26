package com.github.crosswall.app.test;

import android.os.SystemClock;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;


public class Test2 {

    public void hello(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                Request request = new Request.Builder().url("https://api-one.wallstcn.com/apiv1/content/carousel/information-flow?channel=global&limit=5").build();
                OkHttpClient client = createClient();
                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void world() {

        for (int i = 0; i < 1000000; i++) {

        }
        SystemClock.sleep(20);
    }


    private OkHttpClient createClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder.build();
    }
}

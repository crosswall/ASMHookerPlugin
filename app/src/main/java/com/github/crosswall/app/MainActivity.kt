package com.github.crosswall.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.github.crosswall.app.test.Test2
import com.github.crosswall.inject.OkHttpHooker
import com.github.crosswall.inject.http.TimingOKEventListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_hello).setOnClickListener {
            Test2().also {
                it.hello()
                it.world()
            }
        }
    }
}
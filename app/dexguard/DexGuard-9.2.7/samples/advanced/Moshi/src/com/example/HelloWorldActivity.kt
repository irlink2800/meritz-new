/*
 * Sample application to illustrate processing with DexGuard.
 *
 * Copyright (c) 2012-2020 Guardsquare NV
 */
package com.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.squareup.moshi.Moshi

/**
 * Sample activity that displays "Hello world!".
 */
class HelloWorldActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val response = Response("a", "my_val", "c")
        val moshi: Moshi = Moshi.Builder().build()

        // Make sure serialization is working
        val gson = moshi.adapter(Response::class.java).toJson(response)
        val textView = findViewById<TextView>(R.id.GsonTextView)
        textView.text = gson

        // Make sure deserialization is working
        val adapter = moshi.adapter(Response::class.java)
        val response2 = adapter.fromJson(gson)
    }
}

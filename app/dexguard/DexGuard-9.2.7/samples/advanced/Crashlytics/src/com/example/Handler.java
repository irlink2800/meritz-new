package com.example;

import android.view.View;

public class Handler {

    public static void forceJavaCrash(View view)
    {
        throw new RuntimeException("This is a crash from DexGuard");
    }

}

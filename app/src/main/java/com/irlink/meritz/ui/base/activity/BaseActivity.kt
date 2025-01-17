package com.irlink.meritz.ui.base.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import org.koin.androidx.fragment.android.setupKoinFragmentFactory

@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {

//    protected val windowUtil: WindowUtil by inject()

    protected val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupKoinFragmentFactory()
        super.onCreate(savedInstanceState)
//        windowUtil.keepScreenOn(this)
    }

    /**
     * 새로운 인텐트 설정.
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}

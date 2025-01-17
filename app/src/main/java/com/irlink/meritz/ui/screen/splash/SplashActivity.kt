package com.irlink.meritz.ui.screen.splash;


import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import com.irlink.meritz.R
import com.irlink.meritz.databinding.ActivitySplashBinding
import com.irlink.meritz.ui.base.activity.BaseActivity
import com.irlink.meritz.ui.screen.login.LoginActivity
import com.irlink.meritz.util.base.livedata.EventObserver
import org.koin.android.ext.android.inject


class SplashActivity : BaseActivity() {

    companion object {
        const val TAG: String = "SplashActivity"

        fun createIntent(context: Context): Intent =
            Intent(context, SplashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }

        fun intent(context: Context) = createIntent(context).let {
            context.startActivity(it)
        }
    }

    private val binding: ActivitySplashBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_splash)
    }

    private val viewModel: SplashViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initViewModel()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initViewModel() {
        viewModel.onInitialized.observe(this, EventObserver {
            intentLogin()
        })
        viewModel.init()
    }

    /**
     * 로그인 화면으로 이동.
     */
    private fun intentLogin() {
        LoginActivity.intent(this)
    }

}
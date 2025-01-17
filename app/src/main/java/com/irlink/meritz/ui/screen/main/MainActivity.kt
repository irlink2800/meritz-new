package com.irlink.meritz.ui.screen.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.irlink.meritz.R
import com.irlink.meritz.databinding.ActivityMainBinding
import com.irlink.meritz.service.MeritzService
import com.irlink.meritz.ui.base.activity.BaseActivity
import com.irlink.meritz.util.PermissionUtil
import com.irlink.meritz.util.base.livedata.EventObserver
import org.koin.android.ext.android.inject
import java.util.*

class MainActivity : BaseActivity() {

    companion object {
        const val TAG: String = "LoginActivity"

        fun createIntent(
            context: Context,
            isNewTask: Boolean = false
        ): Intent = Intent(context, MainActivity::class.java).apply {
            when (isNewTask) {
                true -> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                false -> {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            }
        }

        fun intent(
            context: Context,
            isNewTask: Boolean = false
        ) = createIntent(context, isNewTask).also { intent ->
            context.startActivity(intent)
        }
    }

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private val viewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initViewModel()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.calendarView.date = Date().time
    }

    private fun initViewModel() {

    }

    override fun onBackPressed() {
        return
//        super.onBackPressed()
    }
}
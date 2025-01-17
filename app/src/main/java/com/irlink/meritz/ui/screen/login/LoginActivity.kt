package com.irlink.meritz.ui.screen.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.databinding.DataBindingUtil
import com.irlink.meritz.App
import com.irlink.meritz.R
import com.irlink.meritz.databinding.ActivityLoginBinding
import com.irlink.meritz.service.MeritzService
import com.irlink.meritz.ui.base.activity.BaseActivity
import com.irlink.meritz.ui.screen.main.MainActivity
import com.irlink.meritz.util.PermissionUtil
import com.irlink.meritz.util.base.livedata.EventObserver
import org.koin.android.ext.android.inject
import kotlin.math.ceil

class LoginActivity : BaseActivity() {

    companion object {
        const val TAG: String = "LoginActivity"

        fun createIntent(
            context: Context,
            isNewTask: Boolean = false
        ): Intent = Intent(context, LoginActivity::class.java).apply {
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

    private val binding: ActivityLoginBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_login)
    }

    private val viewModel: LoginViewModel by inject()

    private val permissionUtil: PermissionUtil by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initViewModel()

        permissionUtil.checkPermission(this) {
            viewModel.init()
        }
    }

    override fun onDestroy() {
        viewModel.stopService()
        super.onDestroy()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initViewModel() {
        viewModel.startService.observe(this, EventObserver {
            MeritzService.start(applicationContext)
        })
        viewModel.stopService.observe(this, EventObserver {
            MeritzService.stop(applicationContext)
        })
        viewModel.showInputUserIdDialog.observe(this, EventObserver {
            showInputUserIdDialog()
        })
        viewModel.showInputPasswordDialog.observe(this, EventObserver {
            showInputPasswordDialog()
        })
        viewModel.showInputBirthDialog.observe(this, EventObserver {
            showInputBirthDialog()
        })
        viewModel.showBootSleepDialog.observe(this, EventObserver {
            showBootSleepDialog()
        })
        viewModel.isLogin.observe(this) {
            if (it) MainActivity.intent(this)
        }
    }

    /**
     * 사번 입력 요청 다이얼로그.
     */
    private fun showInputUserIdDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(App.context.getString(R.string.warning))
            .setMessage(App.context.getString(R.string.plz_input_sabun))
            .setCancelable(false)
            .setPositiveButton(R.string.common_button_ok) { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
        binding.valueNum.requestFocus()
    }

    /**
     * 비밀번호 입력 요청 다이얼로그.
     */
    private fun showInputPasswordDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(App.context.getString(R.string.warning))
            .setMessage(App.context.getString(R.string.plz_input_password))
            .setCancelable(false)
            .setPositiveButton(R.string.common_button_ok) { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
        binding.valuePass.requestFocus()
    }

    /**
     * 생년월일 입력 요청 다이얼로그.
     */
    private fun showInputBirthDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(App.context.getString(R.string.warning))
            .setMessage(App.context.getString(R.string.plz_input_birth_date))
            .setCancelable(false)
            .setPositiveButton(R.string.common_button_ok) { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
        binding.valueBirth.requestFocus()
    }

    /**
     * 부팅 후 200초 이내에 로그인 시도했을 때 표시하는 다이얼로그.
     */
    private fun showBootSleepDialog() {
        val bootSleepTime = 200_000L
        val sleepTime = bootSleepTime - SystemClock.elapsedRealtime()
        val remainSecond = ceil((sleepTime / 1_000).toDouble()).toInt()
        val dialog = AlertDialog.Builder(this)
            .setTitle(App.context.getString(R.string.common_dialog_title_notice))
            .setMessage(App.context.getString(R.string.boot_sleep_200s_message, remainSecond))
            .setCancelable(false)
            .setPositiveButton(R.string.common_button_ok) { dialog, _ ->
                dialog.dismiss()
            }
        dialog.show()
    }

}

package com.irlink.meritz.ui.screen.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irlink.meritz.ui.base.viewmodel.BaseViewModel
import com.irlink.meritz.util.base.livedata.EmptyEvent
import com.irlink.meritz.util.extension.notify
import com.irlink.meritz.util.extension.timer

class SplashViewModel : BaseViewModel() {

    companion object {
        const val TAG: String = "SplashViewModel"
        private const val SPLASH_DELAY = 1000 * 2L
    }

    /**
     * 초기화 완료 이벤트.
     */
    private val _onInitialized: MutableLiveData<EmptyEvent> = MutableLiveData()
    val onInitialized: LiveData<EmptyEvent> = _onInitialized

    /**
     * 초기화.
     */
    fun init() {
        this.compositeDisposable.add(timer(SPLASH_DELAY) {
            _onInitialized.notify()
        })
    }
}
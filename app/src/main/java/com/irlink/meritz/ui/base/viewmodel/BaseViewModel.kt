package com.irlink.meritz.ui.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irlink.meritz.util.base.livedata.EmptyEvent
import com.irlink.meritz.util.base.livedata.Event
import com.irlink.meritz.util.extension.notify
import com.irlink.meritz.util.extension.postNotify
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {

    private val _showToast: MutableLiveData<Event<String>> = MutableLiveData()
    val showToast: LiveData<Event<String>> = _showToast

    private val _showProgress: MutableLiveData<EmptyEvent> = MutableLiveData()
    val showProgress: LiveData<EmptyEvent> = _showProgress

    private val _dismissProgress: MutableLiveData<EmptyEvent> = MutableLiveData()
    val dismissProgress: LiveData<EmptyEvent> = _dismissProgress

    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @JvmOverloads
    open fun showToast(message: String?, isPost: Boolean = false) = when (isPost) {
        true -> _showToast.postNotify = message
        else -> _showToast.notify = message
    }

    @JvmOverloads
    open fun showProgress(isPost: Boolean = false) = when (isPost) {
        true -> _showProgress.postNotify()
        else -> _showProgress.notify()
    }

    @JvmOverloads
    open fun dismissProgress(isPost: Boolean = false) = when (isPost) {
        true -> _dismissProgress.postNotify()
        else -> _dismissProgress.notify()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}
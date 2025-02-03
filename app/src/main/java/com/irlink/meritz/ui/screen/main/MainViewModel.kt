package com.irlink.meritz.ui.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.irlink.meritz.ui.base.viewmodel.BaseViewModel
import com.irlink.meritz.util.LogUtil

class MainViewModel : BaseViewModel() {

    companion object {
        const val TAG: String = "MainViewModel"
    }

    /**
     * 선택된 탭 아이디.
     */
    private val _selectedTapId: MutableLiveData<Int> = MutableLiveData()
    val selectedTapId: LiveData<Int> = _selectedTapId

    /**
     * 검색 날짜.
     */
    val searchDate: MutableLiveData<String> = MutableLiveData()

    /**
     * 데이터 존재 여부.
     */
    val isNoData: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * 탭 아이디 선택.
     */
    fun setSelectedTapId(id: Int) {
        _selectedTapId.value = id
    }

}
package com.apextech.bexatobotuz.viewModel.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.useCase.HistoryUseCase
import com.apextech.bexatobotuz.utils.Constants.TAG
import com.apextech.bexatobotuz.viewModel.HistoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModelImpl @Inject constructor(
    private val useCase: HistoryUseCase,
) : ViewModel(), HistoryViewModel {

    private var _stateStatus = MutableStateFlow<List<HistoryEntity>>(emptyList())
    val stateStatus = _stateStatus.asStateFlow()

    init {
        fetchHistories()
    }

    override fun fetchHistories() {
        useCase.getHistoriesByDatabase().onEach {
            _stateStatus.emit(it)
            deleteAll(it)
        }.launchIn(viewModelScope)
    }

    override fun deleteAll(list: List<HistoryEntity>) {
        viewModelScope.launch {
            val deleteList = mutableListOf<HistoryEntity>()
            for (it in list) {
                val isStartWith = isStartWith(list, it)
                if (isStartWith) {
                    deleteList.add(it)
                    break
                }
            }
            if (list.size > 100)
                deleteList.addAll(list.subList(0, list.size - 100))
            useCase.deleteAll(deleteList)
        }
    }

    private fun isStartWith(list: List<HistoryEntity>, historyEntity: HistoryEntity): Boolean {
        var first = false
        for (it in list) {
            if (it == historyEntity && !first) {
                first = true
                continue
            }
            if (historyEntity.cyrill.length >= 10 && it.cyrill.length >= 10) {
                if (historyEntity.cyrill.substring(0, 10) == it.cyrill.substring(0, 10))
                    return true
            } else {
                if (historyEntity.cyrill == it.cyrill)
                    return true
            }
        }
        return false
    }
}

package com.apextech.bexatobotuz.viewModel.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.useCase.HistoryUseCase
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
            if (list.size > 100) useCase.deleteAll(list.subList(0, list.size - 100))
        }
    }
}

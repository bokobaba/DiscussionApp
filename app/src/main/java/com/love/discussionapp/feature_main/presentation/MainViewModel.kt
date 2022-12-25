package com.love.discussionapp.feature_main.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.love.discussionapp.core.auth.IAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val _auth: IAuth,
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()

    val auth: IAuth
        get() = _auth

    val eventFlow: MutableSharedFlow<UiEvent>
        get() = _eventFlow

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.Login -> viewModelScope.launch {
                Log.d("MainViewModel", "emit login")
                _eventFlow.emit(UiEvent.Login)
            }
            is MainScreenEvent.Logout -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.Logout)
            }
        }
    }

    sealed class UiEvent {
        object Login : UiEvent()
        object Logout: UiEvent()
    }
}
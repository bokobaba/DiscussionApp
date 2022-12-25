package com.love.discussionapp.feature_account.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.love.discussionapp.core.auth.IAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val _auth: IAuth
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    var init by mutableStateOf(false)

    val eventFlow: MutableSharedFlow<UiEvent>
        get() = _eventFlow

    val auth: IAuth
        get() = _auth

    init {
        init = true
    }

    fun onEvent(event: AccountScreenEvent) {
        Log.d("AccountViewModel", "onEvent")
        when (event) {
            is AccountScreenEvent.CreateAccount -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.CreateAccount(event.email, event.password))
            }
            is AccountScreenEvent.Login -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.Login(event.email, event.password))
            }
            is AccountScreenEvent.Back -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.Back)
            }
        }
    }

    sealed class UiEvent {
        data class Login(val email: String, val password: String) : UiEvent()
        data class CreateAccount(val email: String, val password: String): UiEvent()
        object Back: UiEvent()
    }
}
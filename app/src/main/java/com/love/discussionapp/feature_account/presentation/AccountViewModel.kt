package com.love.discussionapp.feature_account.presentation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.love.discussionapp.GetUserByNameQuery
import com.love.discussionapp.core.auth.AuthEvent
import com.love.discussionapp.core.auth.IAuth
import com.love.discussionapp.core.data.repository.ApiRepository
import com.love.discussionapp.core.util.Resource
import com.love.discussionapp.core.util.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val _auth: IAuth,
    private val _repository: ApiRepository,
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()

    val eventFlow: MutableSharedFlow<UiEvent>
        get() = _eventFlow

    fun onEvent(event: AccountScreenEvent) {
        Log.d("AccountViewModel", "onEvent")
        when (event) {
            is AccountScreenEvent.CreateAccount -> viewModelScope.launch {
                Log.d(this.TAG(), "username = [${event.username}]")
                val resource: Resource<GetUserByNameQuery.Data?> =
                    _repository.getUsersByName(event.username)
                when (resource) {
                    is Resource.Success -> {
                        val count: Int = resource.data?.users?.totalCount ?: 1
                        if (count > 0)
                            _eventFlow.emit(UiEvent.UsernameAlreadyExists)
                        else
                            _eventFlow.emit(
                                UiEvent.CreateAccount(
                                    event.username,
                                    event.email,
                                    event.password
                                )
                            )
                    }
                    else -> {
                        Log.d(this.TAG(), resource.message)
                        _eventFlow.emit(UiEvent.Error(resource.message))
                    }
                }
            }
            is AccountScreenEvent.Login -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.Login(event.email, event.password))
            }
            is AccountScreenEvent.Back -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.Back)
            }
        }
    }

    fun createAccount(
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFail: (message: String) -> Unit,
    ) {
        _auth.createAccount(email, password) {
            when (it) {
                is AuthEvent.Fail -> onFail(it.message)
                else -> {
                    viewModelScope.launch {
                        _repository.createUser(username)
                        _auth.updateDisplayName(
                            name = username,
                            onSuccess = onSuccess,
                            onFail = { onFail("Error updating profile name") },
                        )
                    }
                }
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFail: (message: String) -> Unit,
    ) {
        _auth.signIn(email, password) {
            when (it) {
                is AuthEvent.Fail -> onFail(it.message)
                else -> onSuccess()
            }
        }
    }

    sealed class UiEvent {
        data class Error(val message: String) : UiEvent()
        data class Login(val email: String, val password: String) : UiEvent()
        data class CreateAccount(val username: String, val email: String, val password: String) :
            UiEvent()

        object Back : UiEvent()
        object UsernameAlreadyExists : UiEvent()
    }
}
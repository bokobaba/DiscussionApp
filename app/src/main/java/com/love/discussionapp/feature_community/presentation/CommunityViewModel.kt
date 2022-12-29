package com.love.discussionapp.feature_community.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.love.discussionapp.GetCommunitiesQuery
import com.love.discussionapp.GetCommunityQuery
import com.love.discussionapp.SubscribeToCommunityMutation
import com.love.discussionapp.core.auth.IAuth
import com.love.discussionapp.core.data.repository.ApiRepository
import com.love.discussionapp.core.util.Resource
import com.love.discussionapp.core.util.TAG
import com.love.discussionapp.feature_home.presentation.HomeViewModel
import com.love.discussionapp.navigation.COMMUNITY_NAME_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val _auth: IAuth,
    private val _repository: ApiRepository,
    private val _state: ICommunityState,
    private val _savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    private val _loading = mutableStateOf(false)

    val eventFlow: MutableSharedFlow<UiEvent>
        get() = _eventFlow
    val state: ICommunityViewState
        get() = _state
    val loading: State<Boolean>
        get() = _loading

    init {
        _loading.value = true
        _savedStateHandle.get<String>(COMMUNITY_NAME_ARG)?.let { name ->
            if (name != "") {
                viewModelScope.launch {
                    fetchData(name)
                    _loading.value = false
                }
            } else {
                _loading.value = false
            }
        }
    }

    fun onEvent(event: CommunityScreenEvent) {
        when (event) {
            is CommunityScreenEvent.Back -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.Back)
            }
            is CommunityScreenEvent.SelectPost -> viewModelScope.launch {
                _eventFlow.emit(UiEvent.PostSelected(event.id))
            }
            is CommunityScreenEvent.SubscribeButtonClick -> viewModelScope.launch {
                _state.community.value?.let {
                    onSubscribe(_repository.subscribeToCommunity(it.name))
                }
            }
        }
    }

    private suspend fun fetchData(name: String) {
        when(val resource: Resource<GetCommunityQuery.Data?> =
            _repository.getCommunity(name)) {
            is Resource.Success -> {
                val data: GetCommunityQuery.Data? = resource.data
                Log.d(this.TAG(), "data: ${Gson().toJson(data)}")
                parseData(resource.data)
            }
            is Resource.Error -> {
                _eventFlow.emit(UiEvent.FetchDataError(resource.message))
            }
            else -> {}
        }
    }

    private fun parseData(data: GetCommunityQuery.Data?) {
        if (data != null) {
            _state.setCommunityData(data.community[0], _auth.user?.uid ?: "")
            _state.setPosts(data.community[0].posts)
        }
    }

    private suspend fun onSubscribe(data: Resource<SubscribeToCommunityMutation.Data?>) {
        when (data) {
            is Resource.Success -> {
                _state.subscribe()
            }
            is Resource.Error -> {
                _eventFlow.emit(UiEvent.FetchDataError(data.message))
            }
            else -> {}
        }
    }

    sealed class UiEvent {
        data class FetchDataError(val message: String): UiEvent()
        object Back: UiEvent()
        data class PostSelected(val id: UUID): UiEvent()
    }
}
package com.love.discussionapp.feature_community.presentation

import java.util.UUID

sealed class CommunityScreenEvent {
    object Back: CommunityScreenEvent()
    data class SelectPost(val id: UUID): CommunityScreenEvent()
    object SubscribeButtonClick: CommunityScreenEvent()
}
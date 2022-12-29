package com.love.discussionapp.feature_community.domain.model

import java.time.LocalDateTime
import java.util.UUID

data class Post(
    val id: UUID,
    val creatorId: String,
    val creatorName: String,
    val title: String,
    val likes: Int,
    val comments: Int,
    val timestamp: LocalDateTime,
)
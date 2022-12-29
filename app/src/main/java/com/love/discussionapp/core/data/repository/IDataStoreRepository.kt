package com.love.discussionapp.core.data.repository

interface IDataStoreRepository {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun clearPreferences(key: String)
}
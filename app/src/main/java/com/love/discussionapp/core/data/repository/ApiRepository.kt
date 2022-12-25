package com.love.discussionapp.core.data.repository

import android.util.Log
import com.love.discussionapp.GetCommunitiesQuery
import com.love.discussionapp.core.data.remote.Api
import com.love.discussionapp.core.util.Resource
import javax.inject.Inject

class ApiRepository @Inject constructor(
    val api: Api
){

    suspend fun getCommunities(): Resource<GetCommunitiesQuery.Data?> {
        Log.d("ApiRepository", "getCommunities")
        val response = try {
            api.getCommunities()
        } catch (e : java.lang.Exception) {
            e.printStackTrace()
            return Resource.Error("An error occurred loading Communities")
        }

        Log.d("ApiRepository", "Success ${response.data}")

        return Resource.Success(response.data)
    }
}
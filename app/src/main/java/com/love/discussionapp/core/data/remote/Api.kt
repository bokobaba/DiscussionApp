package com.love.discussionapp.core.data.remote

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.love.discussionapp.GetCommunitiesQuery
import javax.inject.Inject


class Api @Inject constructor(
    private val apolloClient: ApolloClient
) {

    suspend fun getCommunities(): ApolloResponse<GetCommunitiesQuery.Data> {
        return apolloClient.query(GetCommunitiesQuery()).execute()
    }
}
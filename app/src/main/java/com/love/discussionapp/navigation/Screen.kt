package com.love.discussionapp.navigation

import android.util.Log
import java.util.UUID

const val COMMUNITY_NAME_ARG = "communityName"
const val POST_ID_ARG = "postId"

const val ROOT_GRAPH_ROUTE = "root"

const val HOME_GRAPH_ROUTE = "home_root"
const val HOME_ROUTE = "Home"

const val ACCOUNT_GRAPH_ROUTE = "account_root"
const val ACCOUNT_ROUTE = "Account"

const val COMMUNITY_ROUTE = "Community?name={$COMMUNITY_NAME_ARG}"

const val POST_ROUTE = "Post?id={$POST_ID_ARG}"

sealed class Screen(val route: String) {
    object Home: Screen(route = HOME_ROUTE)

    object Account: Screen(route = ACCOUNT_ROUTE)

    object Community: Screen(route = COMMUNITY_ROUTE) {
        fun passName(name: String): String {
            Log.d("CommunityScreen", "name = $name")
            return this.route.replace(oldValue = "{$COMMUNITY_NAME_ARG}", newValue = name)
        }
    }

    object Post: Screen(route = POST_ROUTE) {
        fun passName(id: UUID): String {
            Log.d("PostScreen", "id = $id")
            return this.route.replace(oldValue = "{$POST_ID_ARG}", newValue = "$id")
        }
    }
}
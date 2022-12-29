package com.love.discussionapp.core.util

fun <T: Any> T.TAG(): String {
    return "${javaClass.kotlin.simpleName}"
}
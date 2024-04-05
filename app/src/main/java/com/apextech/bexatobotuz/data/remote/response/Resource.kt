package com.apextech.bexatobotuz.data.remote.response

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    class Success<T : Any>(val data: T) : Resource<T>()
    class Error(val e: Throwable) : Resource<Nothing>()
}
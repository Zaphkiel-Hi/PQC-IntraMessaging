package org.niklasunrau.pqcmessenger.domain.util

sealed class Status<T>(val data: T? = null, val error: Throwable? = null) {
    class Success<T>(data: T): Status<T>(data)
    class Error<T>(error: Throwable, data: T? = null) : Status<T>(data, error)
    class Loading<T>(data: T? = null) : Status<T>(data)
}
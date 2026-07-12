package com.adrien.superapp.core.common

/**
 * Typed business errors. Never let a raw exception message reach the UI layer —
 * repositories/use cases map failures onto this hierarchy instead.
 */
sealed interface AppError {
    data object NetworkError : AppError
    data object NoConnection : AppError
    data object SessionExpired : AppError
    data object PermissionDenied : AppError
    data object NotFound : AppError
    data object Forbidden : AppError
    data class SyncConflict(val entityType: String, val entityId: String) : AppError
    data class Unknown(val cause: Throwable? = null) : AppError
}

/** Result of an operation that can fail with a typed [AppError] instead of throwing. */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: AppError) : AppResult<Nothing>
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onFailure(action: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(error)
    return this
}

package dev.finio.auth.domain.model

sealed class AuthError{
    object InvalidCredentials: AuthError()
    object NetworkError: AuthError()
    data class Unknown(val message: String): AuthError()
}
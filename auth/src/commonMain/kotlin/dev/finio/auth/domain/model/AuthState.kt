package dev.finio.auth.domain.model

sealed class AuthState{
    object Idle: AuthState()
    object Loading: AuthState()
    data class Authenticated(val user: User): AuthState()
    data class Error(val error: AuthError): AuthState()
    object Unauthenticated: AuthState()
}
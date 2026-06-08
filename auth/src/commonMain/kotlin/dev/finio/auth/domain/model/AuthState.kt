package dev.finio.auth.domain.model

sealed class AuthState{
    object Idle: AuthState()
    object Loading: AuthState()
    data class Authenticated(val user: User): AuthState()
    data class Error(val message: String): AuthState()
    object Unauthenticated: AuthState()
}
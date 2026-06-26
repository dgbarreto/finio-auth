package dev.finio.auth.domain.repository

import dev.finio.auth.domain.model.AuthResult
import dev.finio.auth.domain.model.User

interface AuthRepository{
    suspend fun register(name: String, email: String, password: String): AuthResult
    suspend fun login(email: String, password: String): AuthResult
    suspend fun getProfile(): User?
    fun logout()
    fun isLoggedIn(): Boolean
    suspend fun saveFcmToken(token: String): Boolean
}
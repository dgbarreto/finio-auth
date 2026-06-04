package dev.finio.auth.domain.repository

import dev.finio.auth.data.dto.LoginRequestDto
import dev.finio.auth.data.dto.RegisterRequestDto
import dev.finio.auth.data.mapper.toDomain
import dev.finio.auth.data.remote.AuthRemoteDataSource
import dev.finio.auth.domain.model.AuthResult
import dev.finio.auth.domain.model.User
import dev.finio.auth.storage.TokenStorage
import io.ktor.http.content.PartData

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val tokenStorage: TokenStorage
): AuthRepository {
    override suspend fun register(name: String, email: String, password: String): AuthResult {
        return try{
            val response = remoteDataSource.register(
                RegisterRequestDto(name = name, email = email, password = password)
            )
            tokenStorage.saveToken(response.token)
            AuthResult.Success(response.toDomain())
        } catch (e: Exception){
            AuthResult.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return try{
            val response = remoteDataSource.login(
                LoginRequestDto(email = email, password = password)
            )
            tokenStorage.saveToken(response.token)
            AuthResult.Success(response.toDomain())
        } catch (e: Exception){
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun getProfile(): User? {
        return try{
            val token = tokenStorage.getToken() ?: return null
            val response = remoteDataSource.getProfile(token)
            response.toDomain()
        } catch (e: Exception){
            null
        }
    }

    override fun logout() {
        tokenStorage.clearToken()
    }

    override fun isLoggedIn(): Boolean {
        return tokenStorage.getToken() != null
    }
}
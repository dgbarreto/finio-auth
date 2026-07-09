package dev.finio.auth.fake

import dev.finio.auth.domain.model.AuthResult
import dev.finio.auth.domain.model.User
import dev.finio.auth.domain.repository.AuthRepository

class FakeAuthRepository: AuthRepository{
    var loginResult: AuthResult = AuthResult.Success(User("1", "Test User", "test@test.com"))
    var registerResult: AuthResult = AuthResult.Success(User("1", "Test User", "test@test.com"))
    var profileResult: User? = User("1", "Test user", "test@test.com")
    var isLoggedInResult: Boolean = false
    var fcmTokenSaved: String? = null

    override suspend fun login(email: String, password: String): AuthResult = loginResult
    override suspend fun getProfile(): User? = profileResult
    override fun logout() {
        isLoggedInResult = false
    }
    override fun isLoggedIn(): Boolean = isLoggedInResult

    override suspend fun saveFcmToken(token: String): Boolean {
        fcmTokenSaved = token
        return true
    }
    override suspend fun register(name: String, email: String, password: String): AuthResult = registerResult
}
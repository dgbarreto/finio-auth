package dev.finio.auth.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponseDto(
    val token: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    @SerialName("_id")
    val id: String,
    val name: String,
    val email: String
)

@Serializable
data class UpdateFcmTokenDto(
    val fcmToken: String
)
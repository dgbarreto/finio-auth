package dev.finio.auth.data.remote

import dev.finio.auth.data.dto.AuthResponseDto
import dev.finio.auth.data.dto.LoginRequestDto
import dev.finio.auth.data.dto.RegisterRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRemoteDataSource(
    private val client: HttpClient,
    private val baseUrl: String
){
    suspend fun register(request: RegisterRequestDto): AuthResponseDto =
        client.post("$baseUrl/auth/register"){
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun login(request: LoginRequestDto): AuthResponseDto =
        client.post("$baseUrl/auth/login"){
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
package dev.finio.auth.data.remote

import dev.finio.auth.event.AuthEvent
import dev.finio.auth.event.AuthEventBus
import dev.finio.auth.network.createAuthPlugin
import dev.finio.auth.storage.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.coroutines.EmptyCoroutineContext.get

fun createHttpClient(
    tokenStorage: TokenStorage,
    authEventBus: AuthEventBus
): HttpClient = HttpClient{
    install(ContentNegotiation){
        json(Json{
            ignoreUnknownKeys = true
            isLenient = true
        })
    }

    install(Logging){
        level = LogLevel.BODY
        logger = object : Logger{
            override fun log(message: String) {
                println("[Finio HTTP] $message")
            }
        }
    }

    install(createAuthPlugin(tokenStorage, authEventBus))
}
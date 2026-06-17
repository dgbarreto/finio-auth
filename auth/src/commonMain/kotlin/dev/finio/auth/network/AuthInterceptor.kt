package dev.finio.auth.network

import dev.finio.auth.event.AuthEvent
import dev.finio.auth.event.AuthEventBus
import dev.finio.auth.storage.TokenStorage
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpStatusCode

fun createAuthPlugin(
    tokenStorage: TokenStorage,
    authEventBus: AuthEventBus
) = createClientPlugin("AuthInterceptor"){
    onResponse { response ->
        if(response.status == HttpStatusCode.Unauthorized){
            tokenStorage.clearToken()
            authEventBus.emit(AuthEvent.SessionExpired)
        }
    }
}
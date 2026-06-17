package dev.finio.auth.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class AuthEvent{
    object SessionExpired: AuthEvent()
}

class AuthEventBus {
    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    suspend fun emit(event: AuthEvent){
        _events.emit(event)
    }
}
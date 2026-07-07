package dev.finio.auth.presentation

import dev.finio.auth.domain.model.AuthResult
import dev.finio.auth.domain.model.AuthState
import dev.finio.auth.domain.repository.AuthRepository
import dev.finio.auth.event.AuthEvent
import dev.finio.auth.event.AuthEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val authEventBus: AuthEventBus
){
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init{
        checkAuthStatus()
    }

    private fun checkAuthStatus(){
        viewModelScope.launch {
            if(repository.isLoggedIn()){
                val user = repository.getProfile()
                if(user != null){
                    _state.value = AuthState.Authenticated(user)
                }
                else{
                    _state.value = AuthState.Unauthenticated
                }
            }
            else{
                _state.value = AuthState.Unauthenticated
            }
        }
    }

    fun login(email: String, password: String){
        viewModelScope.launch {
            _state.value = AuthState.Loading

            when(val result = repository.login(email, password)){
                is AuthResult.Success -> _state.value = AuthState.Authenticated(result.user)
                is AuthResult.Error -> _state.value = AuthState.Error(result.error)
            }
        }
    }

    fun register(name: String, email: String, password: String){
        viewModelScope.launch {
            _state.value = AuthState.Loading

            when(val result = repository.register(name, email, password)){
                is AuthResult.Success -> _state.value = AuthState.Authenticated(result.user)
                is AuthResult.Error -> _state.value = AuthState.Error(result.error)
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
            _state.value = AuthState.Unauthenticated
            authEventBus.emit(AuthEvent.SessionExpired)
        }
    }

    fun loadProfile(){
        viewModelScope.launch {
            _state.value = AuthState.Loading

            val user = repository.getProfile()

            _state.value = if (user != null) AuthState.Authenticated(user) else AuthState.Unauthenticated
        }
    }
}
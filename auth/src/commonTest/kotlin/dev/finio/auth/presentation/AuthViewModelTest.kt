package dev.finio.auth.presentation

import dev.finio.auth.domain.model.AuthError
import dev.finio.auth.domain.model.AuthResult
import dev.finio.auth.domain.model.AuthState
import dev.finio.auth.domain.model.User
import dev.finio.auth.event.AuthEventBus
import dev.finio.auth.fake.FakeAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.coroutines.coroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest{
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeAuthRepository
    private lateinit var authEventBus: AuthEventBus
    private lateinit var viewModel: AuthViewModel

    @BeforeTest
    fun setup(){
        Dispatchers.setMain(testDispatcher)
        repository = FakeAuthRepository()
        authEventBus = AuthEventBus()
        viewModel = AuthViewModel(repository = repository, authEventBus = authEventBus, coroutineDispatcher = testDispatcher)
    }

    @AfterTest
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `when not logged in state is Unauthenticated`() = runTest {
        repository.isLoggedInResult = false
        val viewModel = viewModel
        advanceUntilIdle()
        assertIs<AuthState.Unauthenticated>(viewModel.state.value)
    }

    @Test
    fun `when logged in and profile exists state is Authenticated`() = runTest {
        repository.isLoggedInResult = true
        repository.profileResult = User("1", "Test user", "test@test.com")
        val vm = AuthViewModel(repository, authEventBus, testDispatcher)
        advanceUntilIdle()
        assertIs<AuthState.Authenticated>(vm.state.value)
    }

    @Test
    fun `when logged in but profile is null state is Unauthenticated`() = runTest {
        repository.isLoggedInResult = true
        repository.profileResult = null
        val viewModel = viewModel
        advanceUntilIdle()
        assertIs<AuthState.Unauthenticated>(viewModel.state.value)
    }


    @Test
    fun `login success sets Authenticated state`() = runTest {
        repository.isLoggedInResult = false
        repository.loginResult = AuthResult.Success(User("1", "Test User", "test@test.com"))
        val viewModel = viewModel
        advanceUntilIdle()

        viewModel.login("test@test.com", "password")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertIs<AuthState.Authenticated>(state)
        assertEquals("Test User", state.user.name)
    }

    @Test
    fun `login with invalid credentials sets Error with InvalidCredentials`() = runTest {
        repository.isLoggedInResult = false
        repository.loginResult = AuthResult.Error(AuthError.InvalidCredentials)
        val viewModel = viewModel
        advanceUntilIdle()

        viewModel.login("wrong@test.com", "wrongpass")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertIs<AuthState.Error>(state)
        assertIs<AuthError.InvalidCredentials>(state.error)
    }

    @Test
    fun `login network error sets Error with NetworkError`() = runTest {
        repository.isLoggedInResult = false
        repository.loginResult = AuthResult.Error(AuthError.NetworkError)
        val viewModel = viewModel
        advanceUntilIdle()

        viewModel.login("test@test.com", "password")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertIs<AuthState.Error>(state)
        assertIs<AuthError.NetworkError>(state.error)
    }

    @Test
    fun `register success sets Authenticated state`() = runTest {
        repository.isLoggedInResult = false
        repository.registerResult = AuthResult.Success(User("1", "New User", "new@test.com"))
        val viewModel = viewModel
        advanceUntilIdle()

        viewModel.register("New User", "new@test.com", "password")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertIs<AuthState.Authenticated>(state)
        assertEquals("New User", state.user.name)
    }

    @Test
    fun `register error sets Error state`() = runTest {
        repository.isLoggedInResult = false
        repository.registerResult = AuthResult.Error(AuthError.Unknown("Email already exists"))
        val viewModel = viewModel
        advanceUntilIdle()

        viewModel.register("Test", "existing@test.com", "password")
        advanceUntilIdle()

        assertIs<AuthState.Error>(viewModel.state.value)
    }

    @Test
    fun `logout sets Unauthenticated state`() = runTest {
        repository.isLoggedInResult = true
        repository.profileResult = User("1", "Test User", "test@test.com")
        val viewModel = viewModel
        advanceUntilIdle()

        viewModel.logout()
        advanceUntilIdle()

        assertIs<AuthState.Unauthenticated>(viewModel.state.value)
    }

    @Test
    fun `saveFcmToken calls repository with correct token`() = runTest {
        repository.isLoggedInResult = false
        val viewModel = viewModel
        advanceUntilIdle()

        viewModel.saveFcmToken("fcm-token-123")
        advanceUntilIdle()

        assertEquals("fcm-token-123", repository.fcmTokenSaved)
    }

    @Test
    fun `loadProfile with valid user sets Authenticated state`() = runTest {
        repository.isLoggedInResult = false
        val viewModel = viewModel
        advanceUntilIdle()

        repository.profileResult = User("1", "Test User", "test@test.com")
        viewModel.loadProfile()
        advanceUntilIdle()

        assertIs<AuthState.Authenticated>(viewModel.state.value)
    }

    @Test
    fun `loadProfile with null user sets Unauthenticated state`() = runTest {
        repository.isLoggedInResult = false
        val viewModel = viewModel
        advanceUntilIdle()

        repository.profileResult = null
        viewModel.loadProfile()
        advanceUntilIdle()

        assertIs<AuthState.Unauthenticated>(viewModel.state.value)
    }
}
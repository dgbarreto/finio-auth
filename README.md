# finio-auth

Kotlin Multiplatform authentication module for the Finio platform. Encapsulates all authentication logic — API calls, secure token storage, typed error handling, FCM token registration, and session state — published to GitHub Packages (Maven) for consumption by `finio-app`.

## Stack

- **Language**: Kotlin Multiplatform
- **HTTP**: Ktor Client
- **Serialization**: kotlinx.serialization
- **Coroutines**: kotlinx.coroutines
- **Secure storage**: EncryptedSharedPreferences (Android) / NSUserDefaults (iOS)
- **DI**: Koin
- **CI/CD**: Bitrise
- **Publication**: GitHub Packages (Maven)

## Targets

| Target | Status |
|--------|--------|
| Android | ✅ |
| iOS Arm64 | ✅ |
| iOS Simulator Arm64 | ✅ |

## Module structure
auth/src/
commonMain/kotlin/dev/finio/auth/
data/
dto/                        ← API request/response DTOs
mapper/                     ← DTO → domain model mappers
remote/
AuthRemoteDataSource.kt   ← Ktor API calls
AuthHttpClient.kt         ← Ktor client with AuthInterceptor plugin
AuthInterceptor.kt        ← 401 interceptor: clears token + emits SessionExpired
di/
AuthModule.kt               ← Koin module (authModule(baseUrl))
domain/
model/
User.kt                   ← User domain model (id, name, email)
AuthResult.kt             ← sealed class: Success | Error(AuthError)
AuthState.kt              ← sealed class: Idle | Loading | Authenticated | Error | Unauthenticated
AuthError.kt              ← sealed class: InvalidCredentials | NetworkError | Unknown
repository/
AuthRepository.kt         ← interface
AuthRepositoryImpl.kt     ← implementation
event/
AuthEventBus.kt             ← SharedFlow emitting AuthEvent (SessionExpired)
presentation/
AuthViewModel.kt            ← StateFlow<AuthState>, injectable coroutineDispatcher
storage/
TokenStorage.kt             ← interface
createTokenStorage.kt       ← expect fun (platform-specific factory)
androidMain/kotlin/dev/finio/auth/
data/remote/
HttpClientFactory.android.kt
storage/
TokenStorage.android.kt     ← EncryptedSharedPreferences
iosMain/kotlin/dev/finio/auth/
data/remote/
HttpClientFactory.ios.kt
storage/
TokenStorage.ios.kt         ← NSUserDefaults
commonTest/kotlin/dev/finio/auth/
fake/
FakeAuthRepository.kt       ← test double
presentation/
AuthViewModelTest.kt        ← 11 unit tests

## Domain models

```kotlin
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val error: AuthError) : AuthState()
    object Unauthenticated : AuthState()
}

sealed class AuthError {
    object InvalidCredentials : AuthError()
    object NetworkError : AuthError()
    data class Unknown(val message: String) : AuthError()
}
```

## API endpoints

All endpoints served by `finio-api` deployed on Railway.

| Method | Route | Description | Auth |
|--------|-------|-------------|------|
| POST | `/auth/register` | User registration | ✗ |
| POST | `/auth/login` | Login | ✗ |
| GET | `/auth/profile` | Authenticated user data | ✓ |
| POST | `/auth/fcm-token` | Save device FCM token | ✓ |

## Koin setup

```kotlin
// In Application.onCreate() (Android) or iOSApp init (iOS)
startKoin {
    modules(
        authModule(baseUrl = "https://finio-api-production.up.railway.app")
    )
}
```

The module registers:
- `AuthEventBus` — singleton
- `TokenStorage` — singleton (platform-specific)
- `HttpClient` — singleton with `AuthInterceptor`
- `AuthRemoteDataSource` — singleton
- `AuthRepository` — singleton
- `AuthViewModel` — singleton

## Session expiry

The `AuthInterceptor` Ktor plugin intercepts every 401 response, clears the token and emits `AuthEvent.SessionExpired` via `AuthEventBus`. The app shell observes this event and redirects to the login screen.

## FCM token

`AuthViewModel.saveFcmToken(token)` sends the device FCM token to `POST /auth/fcm-token`. Call this after obtaining the token from `FirebaseMessaging`.

## Testing

```bash
./gradlew :auth:allTests
```

Tests run on iOS Simulator via Kotlin/Native. `AuthViewModel` accepts an injectable `coroutineDispatcher` for test control:

```kotlin
val viewModel = AuthViewModel(
    repository = FakeAuthRepository(),
    authEventBus = AuthEventBus(),
    coroutineDispatcher = StandardTestDispatcher()
)
```

## Published artifacts

| Artifact | Description |
|----------|-------------|
| `dev.finio:auth-android` | Android AAR |
| `dev.finio:auth-iosarm64` | iOS Arm64 framework |
| `dev.finio:auth-iossimulatorarm64` | iOS Simulator framework |
| `dev.finio:auth-kmp` | KMP metadata |

## Publishing

```bash
# Tag triggers Bitrise release workflow → publishes to GitHub Packages
git tag 1.8.0
git push origin 1.8.0
```

Local publish (requires `local.properties`):
```properties
version=1.8.0
github.actor=your_username
github.token=your_token
```

## Known issues

- Module internal name is still `shared` instead of `auth` (issue #2) — affects iOS artifact ID generation. Workaround in place via Gradle configuration.

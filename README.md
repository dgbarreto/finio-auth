# finio-auth

KMP authentication module for the Finio platform. Encapsulates all authentication logic — API calls, secure token storage, and authenticated user state — published to Maven for consumption by `finio-app`.

## Stack

- **Language**: Kotlin Multiplatform
- **HTTP**: Ktor Client 3.1.3
- **Serialization**: kotlinx.serialization 1.8.1
- **Coroutines**: kotlinx.coroutines 1.10.2
- **Secure storage**: EncryptedSharedPreferences (Android) / NSUserDefaults (iOS)
- **DI**: Koin 4.0.0
- **Publication**: GitHub Packages (Maven)
- **CI/CD**: Bitrise

## Targets

| Target | Status |
|--------|--------|
| Android | ✅ |
| iOS Arm64 | ✅ |
| iOS Simulator Arm64 | ✅ |

## Module structure

```
shared/src/
  commonMain/
    kotlin/dev/finio/auth/
      data/
        dto/
          AuthDtos.kt                  ← API request and response DTOs
        mapper/
          AuthMapper.kt                ← DTO → domain model mappers
        remote/
          AuthHttpClient.kt            ← Ktor Client configuration
          AuthRemoteDataSource.kt      ← API calls
        repository/
          AuthRepositoryImpl.kt        ← Repository implementation
      di/
        AuthModule.kt                  ← Koin module definition
        AuthDI.kt                      ← initAuth() entry point
      domain/
        model/
          User.kt                      ← User domain model
          AuthResult.kt                ← sealed class: Success | Error
          AuthState.kt                 ← sealed class: Idle | Loading | Authenticated | Error | Unauthenticated
        repository/
          AuthRepository.kt            ← Repository interface
      presentation/
        AuthViewModel.kt               ← ViewModel with StateFlow
      storage/
        TokenStorage.kt                ← Storage interface
        TokenStorageFactory.kt         ← expect fun createTokenStorage()
  androidMain/
    kotlin/dev/finio/auth/
      storage/
        TokenStorageFactory.android.kt ← EncryptedSharedPreferences
  iosMain/
    kotlin/dev/finio/auth/
      storage/
        TokenStorageFactory.ios.kt     ← NSUserDefaults
```

## API endpoints

All endpoints are served by `finio-api` deployed on Railway.

| Method | Route | Description | Auth |
|--------|-------|-------------|------|
| POST | `/auth/register` | User registration | ✗ |
| POST | `/auth/login` | Login | ✗ |
| GET | `/auth/profile` | Authenticated user data | ✓ |

## DI usage

Initialize Koin from your app shell (not from this module):

**Android** — inside `Application.onCreate()`:
```kotlin
AndroidTokenStorage.init(this)
initAuth(baseUrl = "https://your-api.railway.app")
```

**iOS** — inside `AppDelegate` or `@main`:
```swift
AuthDIKt.initAuth(baseUrl: "https://your-api.railway.app")
```

Then resolve the ViewModel via Koin:
```kotlin
val viewModel: AuthViewModel = get()
```

## ViewModel state

```kotlin
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    object Unauthenticated : AuthState()
}
```

## Maven artifacts

Published to GitHub Packages under `dev.finio` group:

| Artifact | Description |
|----------|-------------|
| `finio-auth-android` | Android AAR |
| `finio-auth-iosarm64` | iOS Arm64 klib |
| `finio-auth-iossimulatorarm64` | iOS Simulator Arm64 klib |
| `finio-auth-kmp` | KMP metadata |

## CI/CD

| Trigger | Workflow | Action |
|---------|----------|--------|
| Push to `main` | `ci` | Compiles Android AAR + iOS Arm64 |
| Any tag (e.g. `1.0.0`) | `release` | Publishes all artifacts to GitHub Packages |

## Build

```bash
# Compile all targets
./gradlew :shared:assemble

# Publish to local Maven (~/.m2)
./gradlew :shared:publishToMavenLocal

# Publish to GitHub Packages (requires GITHUB_ACTOR and GITHUB_TOKEN)
./gradlew :shared:publish
```

## Key versions

```toml
kotlin = "2.3.21"
agp = "9.0.1"
ktor = "3.1.3"
koin = "4.0.0"
kotlinx-coroutines = "1.10.2"
kotlinx-serialization = "1.8.1"
```
# finio-auth

Módulo KMP de autenticação da plataforma Finio. Encapsula toda a lógica de autenticação — chamadas à API, armazenamento seguro do token e estado do usuário autenticado — publicado no Maven para ser consumido pelo `finio-app`.

## Stack

- **Linguagem**: Kotlin Multiplatform
- **HTTP**: Ktor Client
- **Serialização**: kotlinx.serialization
- **Armazenamento seguro**: EncryptedSharedPreferences (Android) / NSUserDefaults (iOS)
- **DI**: Koin *(planejado — Dia 29)*
- **Publicação**: GitHub Packages (Maven)

## Targets

| Target | Status |
|--------|--------|
| Android | ✅ |
| iOS Arm64 | ✅ |
| iOS Simulator Arm64 | ✅ |

## Estrutura

```
shared/src/
  commonMain/
    kotlin/dev/finio/auth/
      data/
        dto/
          AuthDtos.kt             ← DTOs de request e response
        remote/
          AuthHttpClient.kt       ← configuração do Ktor Client
          AuthRemoteDataSource.kt ← chamadas à API
      storage/
        TokenStorage.kt           ← interface de armazenamento
        TokenStorageFactory.kt    ← expect fun createTokenStorage()
  androidMain/
    kotlin/dev/finio/auth/
      storage/
        TokenStorageFactory.android.kt ← EncryptedSharedPreferences
  iosMain/
    kotlin/dev/finio/auth/
      storage/
        TokenStorageFactory.ios.kt ← NSUserDefaults
```

## Endpoints consumidos

Todos os endpoints são do `finio-api` em produção no Railway.

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/auth/register` | Cadastro de usuário |
| POST | `/auth/login` | Login |
| GET | `/auth/profile` | Dados do usuário autenticado |

## Módulos planejados

- **AuthRepository** — orquestra remote + storage (Dia 28)
- **AuthViewModel** — MVVM com Koin (Dia 29)
- **Publicação Maven** — GitHub Packages via Bitrise (Dia 30+)

## Versões principais

```toml
kotlin = "2.3.21"
ktor = "3.1.3"
kotlinx-coroutines = "1.10.2"
kotlinx-serialization = "1.8.1"
```

## Build

```bash
./gradlew :shared:assemble
```
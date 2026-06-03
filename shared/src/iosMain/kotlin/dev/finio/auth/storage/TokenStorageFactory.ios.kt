package dev.finio.auth.storage

import platform.Foundation.NSUserDefaults

actual fun createTokenStorage(): TokenStorage = IosTokenStorage()

class IosTokenStorage: TokenStorage{
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun saveToken(token: String) {
        defaults.setObject(token, forKey = "auth_token")
    }

    override fun getToken(): String? {
        return defaults.stringForKey("auth_token")
    }

    override fun clearToken() {
        defaults.removeObjectForKey("auth_token")
    }
}
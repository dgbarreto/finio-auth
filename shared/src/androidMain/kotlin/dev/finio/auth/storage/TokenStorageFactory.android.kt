package dev.finio.auth.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual fun createTokenStorage(): TokenStorage = AndroidTokenStorage()

class AndroidTokenStorage: TokenStorage{
    companion object{
        private lateinit var context: Context

        fun init(ctx: Context){
            context = ctx.applicationContext
        }
    }

    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "finio_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    override fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    override fun clearToken() {
        prefs.edit().remove("auth_token").apply()
    }
}
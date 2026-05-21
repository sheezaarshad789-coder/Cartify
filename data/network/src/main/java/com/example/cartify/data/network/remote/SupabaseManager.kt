package com.example.cartify.data.network.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

object SupabaseManager {
    private const val SUPABASE_URL = "https://gbdsudmuoiiwkoceakum.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdiZHN1ZG11b2lpd2tvY2Vha3VtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzc0NTkyMDcsImV4cCI6MjA5MzAzNTIwN30.3bcaI52yxYXJz4ySQDpTkv8cLZZxgfwjWA4aT0mgSmA"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Auth)
        install(Realtime)
        install(Storage)
    }

    /**
     * Restores the session using the saved session JSON string.
     * In Supabase Kotlin 2.x, importSession usually takes a UserSession object.
     * If a string is provided, it should be the serialized UserSession.
     */
    suspend fun restoreSession(sessionJson: String) {
        try {
            // Attempt to deserialize the string into a UserSession object
            // This assumes the saved string is a full JSON representation of the session
            val session = Json.decodeFromString<io.github.jan.supabase.gotrue.user.UserSession>(sessionJson)
            client.auth.importSession(session)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

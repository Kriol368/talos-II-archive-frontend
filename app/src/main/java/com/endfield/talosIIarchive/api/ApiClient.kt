package com.endfield.talosIIarchive.api

import com.google.android.gms.auth.api.Auth
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val client = HttpClient(OkHttp) {
    // 1. Configuración de JSON
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        })
    }

    // 2. Plugin de Autenticación (Spring Security)
    install(Auth) {
        basic {
            credentials {
                BasicAuthCredentials(
                    username = "kriol",
                    password = "talos2026"
                )
            }
            // Esto envía la contraseña inmediatamente sin esperar un error 401
            sendWithoutRequest { request ->
                request.url.host == "158.179.216.16"
            }
        }
    }

    // 3. Configuración de la URL de Oracle
    defaultRequest {
        // IMPORTANTE: Cambiamos 10.0.2.2 por tu IP pública de Oracle
        url("http://158.179.216.16:8080/")
    }
}
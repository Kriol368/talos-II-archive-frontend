package com.endfield.talosIIarchive.api

import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// src/commonMain/kotlin/com/endfield/talosIIarchive/api/ApiClient.kt
val client = HttpClient(OkHttp) { // Usamos OkHttp que agregamos al build.gradle
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true // Importante: el backend envía muchos campos (will, intellect, etc.)
            isLenient = true
            coerceInputValues = true // Ayuda con valores nulos o tipos ligeramente distintos
              })
    }
    defaultRequest {
        // IP para emulador Android conectando al localhost de tu PC
        url("http://127.0.0.1:8080/endfield/")
    }
}
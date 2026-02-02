package com.endfield.talosIIarchive.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// Definimos el cliente aquí
val client = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        })
    }
    defaultRequest {
        // Ajusta esta URL a la de tu backend Spring Boot
        url("http://localhost:8080/endfield/")
    }
}
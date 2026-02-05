package com.endfield.talosIIarchive.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val client = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        })
    }

    install(Auth) {
        basic {
            credentials {
                BasicAuthCredentials(
                    username = "kriol",
                    password = "talos2026"
                )
            }
            sendWithoutRequest { request ->
                request.url.host == "158.179.216.16"
            }
        }
    }

    defaultRequest {
        url("http://158.179.216.16:8080/")
    }
}
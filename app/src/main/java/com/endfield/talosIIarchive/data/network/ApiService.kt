package com.endfield.talosIIarchive.data.network

import com.endfield.talosIIarchive.data.model.Operator
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("endfield/operators")
    suspend fun getOperators(): List<Operator>

    @GET("endfield/operators/{id}")
    suspend fun getOperatorById(@Path("id") id: Long): Operator
}
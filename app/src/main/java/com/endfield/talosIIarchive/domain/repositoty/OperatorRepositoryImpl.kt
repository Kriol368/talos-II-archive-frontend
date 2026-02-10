package com.endfield.talosIIarchive.domain.repositoty

import android.util.Log
import com.endfield.talosIIarchive.api.client
import com.endfield.talosIIarchive.domain.models.Operator
import io.ktor.client.call.body
import io.ktor.client.request.get

class OperatorRepositoryImpl : OperatorRepository {


    override suspend fun getAllOperators(): List<Operator> {
        return try {
            client.get("endfield/operators").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error lista: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getOperatorById(id: Int): Operator? {
        return try {
            client.get("endfield/operators/$id").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error detalle ID $id: ${e.message}")
            null
        }
    }

}
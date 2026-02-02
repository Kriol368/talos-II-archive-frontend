package com.endfield.talosIIarchive.domain.repositoty
import com.endfield.talosIIarchive.api.client
import com.endfield.talosIIarchive.domain.models.Operator
import io.ktor.client.call.body
import io.ktor.client.request.get

class OperatorRepositoryImpl : OperatorRepository {


    override suspend fun getAllOperators(): List<Operator> {
        return try {
            // El endpoint es /endfield/operators según tu backend
            client.get("operators").body()
        } catch (e: Exception) {
            println("Error en la petición: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getOperatorById(id: Long): Operator? {
        return try {
            client.get("operators/$id").body()
        } catch (e: Exception) {
            null
        }
    }
}
package com.endfield.talosIIarchive.domain.repositoty
import com.endfield.talosIIarchive.api.client
import com.endfield.talosIIarchive.domain.models.Operator
import io.ktor.client.call.body
import io.ktor.client.request.get
import android.util.Log // 👈 Usaremos Log.e para que salga en rojo y se vea fácil
import com.endfield.talosIIarchive.domain.models.Weapon

class OperatorRepositoryImpl : OperatorRepository {


    // OperatorRepositoryImpl.kt
    override suspend fun getAllOperators(): List<Operator> {
        return try {
            // Al usar "operators" sin el http delante,
            // Ktor le pega la URL base que pusimos arriba (10.0.2.2)
            val response = client.get("operators")
            response.body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "ERROR: ${e.message}")
            emptyList()
        }
    }
    override suspend fun getOperatorById(id: Int): Operator? {
        return try {
            // Esto llamará a http://10.0.2.2:8080/endfield/operators/1 (por ejemplo)
            val response = client.get("operators/$id")
            if (response.status.value in 200..299) {
                response.body<Operator>()
            } else null
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error al obtener detalle: ${e.message}")
            null
        }
    }

    override suspend fun getAllWeapons(): List<Weapon> {
        return try {
            // Usamos la misma lógica que con los operadores
            // Ktor usará la baseUrl http://10.0.2.2:8080/endfield/weapons
            val response = client.get("weapons")
            Log.d("TALOS_DEBUG", "Armas recibidas: ${response.status}")
            response.body<List<Weapon>>()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "ERROR cargando armas: ${e.message}")
            emptyList() // Si falla, devolvemos lista vacía para que no pete la app
        }
    }
}
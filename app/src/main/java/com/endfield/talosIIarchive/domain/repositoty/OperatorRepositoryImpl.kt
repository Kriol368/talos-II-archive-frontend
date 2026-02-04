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
            client.get("http://10.0.2.2:8080/endfield/operators").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error lista: ${e.message}")
            emptyList()
        }
    }

    // Para la ficha técnica (Endpoint con ID)
    override suspend fun getOperatorById(id: Int): Operator? {
        return try {
            // Importante: que la URL termine en /id
            client.get("http://10.0.2.2:8080/endfield/operators/$id").body()
        } catch (e: Exception) {
            Log.e("TALOS_DEBUG", "Error detalle ID $id: ${e.message}")
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
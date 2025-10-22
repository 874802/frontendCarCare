package eina.unizar.frontend.viewmodels

// ViewModel para manejar la lógica de la API
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.VehiculoDTO
import eina.unizar.frontend.models.VehiculoResponse
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel para la pantalla principal (Home) de la aplicación.
 * 
 * Gestiona la obtención y almacenamiento de:
 * - Nombre del usuario autenticado
 * - Lista de vehículos disponibles para el usuario
 * 
 * Utiliza coroutines para operaciones asíncronas y StateFlow/State
 * para exponer datos reactivos a la UI.
 */
class HomeViewModel : ViewModel() {

    /**
     * Nombre del usuario actual.
     * 
     * Utiliza mutableStateOf para que los cambios sean observables
     * por la UI de Compose. Inicializado con texto de carga.
     */
    var userName by mutableStateOf("Cargando...")
        private set

    /**
     * Flow privado mutable para la lista de vehículos.
     */
    private val _vehiculos = MutableStateFlow<List<VehiculoDTO>>(emptyList())

    /**
     * StateFlow público con la lista de vehículos del usuario.
     * La UI puede colectar este flow para actualizar la lista de vehículos.
     */
    val vehiculos: StateFlow<List<VehiculoDTO>> = _vehiculos

    /**
     * Obtiene el nombre del usuario desde el backend.
     * 
     * Realiza una petición asíncrona para recuperar el nombre del usuario
     * utilizando su ID y token de autenticación. Actualiza userName con
     * el resultado o con un mensaje de error si falla.
     * 
     * @param userId Identificador del usuario
     * @param token Token JWT de autenticación (sin el prefijo "Bearer")
     */
    fun fetchUserName(userId: String, token: String) {
        viewModelScope.launch {
            try {
                // Añade logs para depuración
                Log.d("HomeViewModel", "Obteniendo nombre de usuario para ID: $userId")

                // Usar await() en lugar de execute()
                val response = RetrofitClient.instance.obtenerNombreUsuario(userId, "Bearer $token")
                    .await()

                userName = response.nombre ?: "Usuario no encontrado"
                Log.d("HomeViewModel", "Nombre obtenido: $userName")
            } catch (e: HttpException) {
                Log.e("HomeViewModel", "Error HTTP: ${e.code()} ${e.message()}")
                userName = "Error de red: ${e.code()}"
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al obtener nombre: ${e.message}", e)
                userName = "Error: ${e.message?.take(30) ?: "desconocido"}"
            }
        }
    }

    /**
     * Obtiene la lista de vehículos asociados al usuario.
     * 
     * Realiza una petición al backend para recuperar todos los vehículos
     * vinculados al usuario. Actualiza el StateFlow de vehículos con los
     * resultados o registra un error si falla la petición.
     * 
     * Utiliza callbacks de Retrofit en lugar de coroutines para manejar
     * la respuesta de forma asíncrona.
     * 
     * @param userId Identificador del usuario
     * @param token Token JWT de autenticación (sin el prefijo "Bearer")
     */
    fun fetchVehiculos(userId: String, token: String) {
        Log.d("HomeViewModel", "Iniciando fetchVehiculos para userId: $userId")
        RetrofitClient.instance.obtenerVehiculos(userId, "Bearer $token")
            .enqueue(object : Callback<VehiculoResponse> {
                override fun onResponse(
                    call: Call<VehiculoResponse>,
                    response: Response<VehiculoResponse>
                ) {
                    Log.d("HomeViewModel", "Respuesta recibida: ${response.code()}")
                    if (response.isSuccessful) {
                        val vehiculos = response.body()?.vehiculos ?: emptyList()
                        Log.d("HomeViewModel", "Vehículos obtenidos: ${vehiculos.size}")
                        _vehiculos.value = vehiculos
                    } else {
                        Log.e("HomeViewModel", "Error en la respuesta: ${response.errorBody()?.string()}")
                    }
                }
                override fun onFailure(call: Call<VehiculoResponse>, t: Throwable) {
                    Log.e("HomeViewModel", "Error en fetchVehiculos: ${t.message}", t)
                }
            })
    }
}
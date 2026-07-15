package com.example.gastosapp.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.gastosapp.domain.model.Categoria
import com.example.gastosapp.domain.model.Gasto
import com.example.gastosapp.domain.model.Ingreso
import com.example.gastosapp.domain.model.Meta
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BackendClient {
    private const val BASE_URL = "http://192.168.1.4:8080/v1"
    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private var jwtToken: String? = null
    private var nombreUsuario: String? = null
    private var correoUsuario: String? = null
    private var nombreRealUsuario: String? = null
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("gastosapp_prefs", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwt_token", null)
        nombreUsuario = prefs.getString("nombre_usuario", null)
        correoUsuario = prefs.getString("correo_usuario", null)
        nombreRealUsuario = prefs.getString("nombre_real_usuario", null)
    }

    fun saveAuth(token: String, username: String, email: String, name: String) {
        jwtToken = token
        nombreUsuario = username
        correoUsuario = email
        nombreRealUsuario = name
        prefs.edit()
            .putString("jwt_token", token)
            .putString("nombre_usuario", username)
            .putString("correo_usuario", email)
            .putString("nombre_real_usuario", name)
            .apply()
    }

    fun getNombreUsuario(): String? = nombreUsuario
    fun getCorreoUsuario(): String? = correoUsuario
    fun getNombreRealUsuario(): String? = nombreRealUsuario

    fun clearAuth() {
        jwtToken = null
        nombreUsuario = null
        correoUsuario = null
        nombreRealUsuario = null
        prefs.edit().clear().apply()
    }

    fun hasToken(): Boolean = jwtToken != null

    private fun getAuthHeader(): String? {
        return jwtToken?.let { "Bearer $it" }
    }

    // DTO structures to parse/send
    data class LoginRequest(val contrasena: String, val nombreUsuario: String? = null, val correo: String? = null)
    data class RegisterRequest(val nombreUsuario: String, val nombre: String, val correo: String, val contrasena: String)
    data class LoginResponse(val token: String, val usuario: UserDto)
    data class UserDto(val nombreUsuario: String, val nombre: String, val correo: String)

    data class CategoryDto(val nombre: String, val tipo: String, val esDeMeta: Boolean)
    data class ExpenseDto(val codigoGasto: Int, val monto: Int, val detalle: String, val nombreDeUsuario: String, val nombreCategoria: String, val fecha: Long)
    data class IncomeDto(val codigoIngreso: Int, val monto: Int, val detalle: String, val nombreDeUsuario: String, val nombreCategoria: String, val fecha: Long)
    data class GoalDto(val codigoMeta: Int, val monto: Int, val nombreDeUsuario: String, val nombreCategoria: String, val fechaLimite: Long, val activa: Boolean)

    data class ExpenseRequest(val monto: Int, val detalle: String, val nombreCategoria: String, val fecha: Long)
    data class IncomeRequest(val monto: Int, val detalle: String, val nombreCategoria: String, val fecha: Long)
    data class GoalRequest(val monto: Int, val nombreCategoria: String, val fechaLimite: Long, val activa: Boolean)

    // Auth Calls
    suspend fun login(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val requestBody = gson.toJson(LoginRequest(contrasena = password, correo = email)).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/auth/login")
            .post(requestBody)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val loginRes = gson.fromJson(body, LoginResponse::class.java)
                    saveAuth(loginRes.token, loginRes.usuario.nombreUsuario, loginRes.usuario.correo, loginRes.usuario.nombre)
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("BackendClient", "Login error", e)
            false
        }
    }

    suspend fun register(username: String, name: String, email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val requestBody = gson.toJson(RegisterRequest(username, name, email, password)).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/auth/register")
            .post(requestBody)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val loginRes = gson.fromJson(body, LoginResponse::class.java)
                    saveAuth(loginRes.token, loginRes.usuario.nombreUsuario, loginRes.usuario.correo, loginRes.usuario.nombre)
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("BackendClient", "Register error", e)
            false
        }
    }

    // Category Sync
    suspend fun getCategorias(): List<Categoria> = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext emptyList()
        val request = Request.Builder()
            .url("$BASE_URL/categorias")
            .addHeader("Authorization", authHeader)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val listType = object : TypeToken<List<CategoryDto>>() {}.type
                    val dtoList: List<CategoryDto> = gson.fromJson(body, listType)
                    dtoList.map {
                        val parsedTipo = try {
                            com.example.gastosapp.domain.model.TipoCategoria.valueOf(it.tipo)
                        } catch (e: Exception) {
                            com.example.gastosapp.domain.model.TipoCategoria.GASTO
                        }
                        Categoria(it.nombre, parsedTipo, it.esDeMeta)
                    }
                } else emptyList()
            }
        } catch (e: Exception) {
            Log.e("BackendClient", "getCategorias error", e)
            emptyList()
        }
    }

    suspend fun insertCategoria(categoria: Categoria): Boolean = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext false
        val requestBody = gson.toJson(CategoryDto(categoria.nombre, categoria.tipo.name, categoria.esDeMeta)).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/categorias")
            .addHeader("Authorization", authHeader)
            .post(requestBody)
            .build()
        try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteCategoria(nombre: String, tipo: String): Boolean = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext false
        val request = Request.Builder()
            .url("$BASE_URL/categorias/$nombre/$tipo")
            .addHeader("Authorization", authHeader)
            .delete()
            .build()
        try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    // Expense Sync
    suspend fun getGastos(): List<Gasto> = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext emptyList()
        val request = Request.Builder()
            .url("$BASE_URL/gastos")
            .addHeader("Authorization", authHeader)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val listType = object : TypeToken<List<ExpenseDto>>() {}.type
                    val dtoList: List<ExpenseDto> = gson.fromJson(body, listType)
                    dtoList.map { Gasto(it.codigoGasto, it.monto, it.detalle, it.nombreDeUsuario, it.nombreCategoria, Date(it.fecha)) }
                } else emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun insertGasto(gasto: Gasto): Int? = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext null
        val payload = ExpenseRequest(gasto.monto, gasto.detalle, gasto.nombreCategoria, gasto.fecha.time)
        val requestBody = gson.toJson(payload).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/gastos")
            .addHeader("Authorization", authHeader)
            .post(requestBody)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val dto = gson.fromJson(body, ExpenseDto::class.java)
                    dto.codigoGasto
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteGasto(codigoGasto: Int): Boolean = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext false
        val request = Request.Builder()
            .url("$BASE_URL/gastos/$codigoGasto")
            .addHeader("Authorization", authHeader)
            .delete()
            .build()
        try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    // Income Sync
    suspend fun getIngresos(): List<Ingreso> = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext emptyList()
        val request = Request.Builder()
            .url("$BASE_URL/ingresos")
            .addHeader("Authorization", authHeader)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val listType = object : TypeToken<List<IncomeDto>>() {}.type
                    val dtoList: List<IncomeDto> = gson.fromJson(body, listType)
                    dtoList.map { Ingreso(it.codigoIngreso, it.monto, it.detalle, it.nombreDeUsuario, it.nombreCategoria, Date(it.fecha)) }
                } else emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun insertIngreso(ingreso: Ingreso): Int? = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext null
        val payload = IncomeRequest(ingreso.monto, ingreso.detalle, ingreso.nombreCategoria, ingreso.fecha.time)
        val requestBody = gson.toJson(payload).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/ingresos")
            .addHeader("Authorization", authHeader)
            .post(requestBody)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val dto = gson.fromJson(body, IncomeDto::class.java)
                    dto.codigoIngreso
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteIngreso(codigoIngreso: Int): Boolean = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext false
        val request = Request.Builder()
            .url("$BASE_URL/ingresos/$codigoIngreso")
            .addHeader("Authorization", authHeader)
            .delete()
            .build()
        try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    // Goal Sync
    suspend fun getMetas(): List<Meta> = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext emptyList()
        val request = Request.Builder()
            .url("$BASE_URL/metas")
            .addHeader("Authorization", authHeader)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val listType = object : TypeToken<List<GoalDto>>() {}.type
                    val dtoList: List<GoalDto> = gson.fromJson(body, listType)
                    dtoList.map { Meta(it.codigoMeta, it.monto, it.nombreDeUsuario, it.nombreCategoria, Date(it.fechaLimite), it.activa) }
                } else emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun insertMeta(meta: Meta): Int? = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext null
        val payload = GoalRequest(meta.monto, meta.nombreCategoria, meta.fechaLimite.time, meta.activa)
        val requestBody = gson.toJson(payload).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/metas")
            .addHeader("Authorization", authHeader)
            .post(requestBody)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val dto = gson.fromJson(body, GoalDto::class.java)
                    dto.codigoMeta
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateMeta(meta: Meta): Boolean = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext false
        val payload = GoalRequest(meta.monto, meta.nombreCategoria, meta.fechaLimite.time, meta.activa)
        val requestBody = gson.toJson(payload).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/metas/${meta.codigoMeta}")
            .addHeader("Authorization", authHeader)
            .put(requestBody)
            .build()
        try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteMeta(codigoMeta: Int): Boolean = withContext(Dispatchers.IO) {
        val authHeader = getAuthHeader() ?: return@withContext false
        val request = Request.Builder()
            .url("$BASE_URL/metas/$codigoMeta")
            .addHeader("Authorization", authHeader)
            .delete()
            .build()
        try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }
}

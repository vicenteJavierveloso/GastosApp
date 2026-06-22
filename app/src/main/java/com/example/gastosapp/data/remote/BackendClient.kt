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

object BackendClient {
    private const val BASE_URL = "http://10.0.2.2:8080/v1"
    private val client = OkHttpClient()
    private val gson = Gson()
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private var jwtToken: String? = null
    private var nombreUsuario: String? = null
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("gastosapp_prefs", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwt_token", null)
        nombreUsuario = prefs.getString("nombre_usuario", null)
    }

    fun saveAuth(token: String, username: String) {
        jwtToken = token
        nombreUsuario = username
        prefs.edit()
            .putString("jwt_token", token)
            .putString("nombre_usuario", username)
            .apply()
    }

    fun getNombreUsuario(): String? = nombreUsuario

    fun clearAuth() {
        jwtToken = null
        nombreUsuario = null
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
    fun login(email: String, password: String): Boolean {
        val requestBody = gson.toJson(LoginRequest(contrasena = password, correo = email)).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/auth/login")
            .post(requestBody)
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val loginRes = gson.fromJson(body, LoginResponse::class.java)
                    saveAuth(loginRes.token, loginRes.usuario.nombreUsuario)
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

    fun register(username: String, name: String, email: String, password: String): Boolean {
        val requestBody = gson.toJson(RegisterRequest(username, name, email, password)).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/auth/register")
            .post(requestBody)
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val loginRes = gson.fromJson(body, LoginResponse::class.java)
                    saveAuth(loginRes.token, loginRes.usuario.nombreUsuario)
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
    fun getCategorias(): List<Categoria> {
        val authHeader = getAuthHeader() ?: return emptyList()
        val request = Request.Builder()
            .url("$BASE_URL/categorias")
            .addHeader("Authorization", authHeader)
            .build()
        return try {
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

    fun insertCategoria(categoria: Categoria): Boolean {
        val authHeader = getAuthHeader() ?: return false
        val requestBody = gson.toJson(CategoryDto(categoria.nombre, categoria.tipo.name, categoria.esDeMeta)).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/categorias")
            .addHeader("Authorization", authHeader)
            .post(requestBody)
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    fun deleteCategoria(nombre: String, tipo: String): Boolean {
        val authHeader = getAuthHeader() ?: return false
        val request = Request.Builder()
            .url("$BASE_URL/categorias/$nombre/$tipo")
            .addHeader("Authorization", authHeader)
            .delete()
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    // Expense Sync
    fun getGastos(): List<Gasto> {
        val authHeader = getAuthHeader() ?: return emptyList()
        val request = Request.Builder()
            .url("$BASE_URL/gastos")
            .addHeader("Authorization", authHeader)
            .build()
        return try {
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

    fun insertGasto(gasto: Gasto): Int? {
        val authHeader = getAuthHeader() ?: return null
        val payload = ExpenseRequest(gasto.monto, gasto.detalle, gasto.nombreCategoria, gasto.fecha.time)
        val requestBody = gson.toJson(payload).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/gastos")
            .addHeader("Authorization", authHeader)
            .post(requestBody)
            .build()
        return try {
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

    fun deleteGasto(codigoGasto: Int): Boolean {
        val authHeader = getAuthHeader() ?: return false
        val request = Request.Builder()
            .url("$BASE_URL/gastos/$codigoGasto")
            .addHeader("Authorization", authHeader)
            .delete()
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    // Income Sync
    fun getIngresos(): List<Ingreso> {
        val authHeader = getAuthHeader() ?: return emptyList()
        val request = Request.Builder()
            .url("$BASE_URL/ingresos")
            .addHeader("Authorization", authHeader)
            .build()
        return try {
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

    fun insertIngreso(ingreso: Ingreso): Int? {
        val authHeader = getAuthHeader() ?: return null
        val payload = IncomeRequest(ingreso.monto, ingreso.detalle, ingreso.nombreCategoria, ingreso.fecha.time)
        val requestBody = gson.toJson(payload).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/ingresos")
            .addHeader("Authorization", authHeader)
            .post(requestBody)
            .build()
        return try {
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

    fun deleteIngreso(codigoIngreso: Int): Boolean {
        val authHeader = getAuthHeader() ?: return false
        val request = Request.Builder()
            .url("$BASE_URL/ingresos/$codigoIngreso")
            .addHeader("Authorization", authHeader)
            .delete()
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    // Goal Sync
    fun getMetas(): List<Meta> {
        val authHeader = getAuthHeader() ?: return emptyList()
        val request = Request.Builder()
            .url("$BASE_URL/metas")
            .addHeader("Authorization", authHeader)
            .build()
        return try {
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

    fun insertMeta(meta: Meta): Int? {
        val authHeader = getAuthHeader() ?: return null
        val payload = GoalRequest(meta.monto, meta.nombreCategoria, meta.fechaLimite.time, meta.activa)
        val requestBody = gson.toJson(payload).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/metas")
            .addHeader("Authorization", authHeader)
            .post(requestBody)
            .build()
        return try {
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

    fun updateMeta(meta: Meta): Boolean {
        val authHeader = getAuthHeader() ?: return false
        val payload = GoalRequest(meta.monto, meta.nombreCategoria, meta.fechaLimite.time, meta.activa)
        val requestBody = gson.toJson(payload).toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url("$BASE_URL/metas/${meta.codigoMeta}")
            .addHeader("Authorization", authHeader)
            .put(requestBody)
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }

    fun deleteMeta(codigoMeta: Int): Boolean {
        val authHeader = getAuthHeader() ?: return false
        val request = Request.Builder()
            .url("$BASE_URL/metas/$codigoMeta")
            .addHeader("Authorization", authHeader)
            .delete()
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (e: Exception) {
            false
        }
    }
}

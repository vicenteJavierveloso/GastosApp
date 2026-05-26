package com.example.gastosapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey
    @ColumnInfo(name = "nombreusuario")
    val nombreUsuario: String,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "correo")
    val correo: String,

    @ColumnInfo(name = "ultimoiniciodesesion")
    val ultimoInicioDeSesion: Date,

    @ColumnInfo(name = "contrasena")
    val contrasena: String
) {
    init {
        require(nombreUsuario.length <= 30) {
            "nombreusuario no puede superar 30 caracteres"
        }
        require(nombre.length <= 255) {
            "nombre no puede superar 255 caracteres"
        }
        require(correo.length <= 255) {
            "correo no puede superar 255 caracteres"
        }
    }
}

package com.example.gastosapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias", primaryKeys = ["nombre", "tipo"])
data class Categoria(
    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "tipo")
    val tipo: String,

    @ColumnInfo(name = "esDeMeta")
    val esDeMeta: Boolean = false
) {
    init {
        require(nombre.length <= 30) {
            "nombre no puede superar 30 caracteres"
        }
        require(tipo == "GASTO" || tipo == "INGRESO") {
            "tipo de categoria invalido"
        }
    }
}

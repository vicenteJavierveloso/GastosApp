package com.example.gastosapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey
    @ColumnInfo(name = "nombre")
    val nombre: String
) {
    init {
        require(nombre.length <= 30) {
            "nombre no puede superar 30 caracteres"
        }
    }
}

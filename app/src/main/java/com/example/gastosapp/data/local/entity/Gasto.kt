package com.example.gastosapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "gastos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["nombreusuario"],
            childColumns = ["nombredeusuario"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["nombre"],
            childColumns = ["nombrecategoria"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["nombredeusuario"]),
        Index(value = ["nombrecategoria"])
    ]
)
data class Gasto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "codigogasto")
    val codigoGasto: Int = 0,

    @ColumnInfo(name = "monto")
    val monto: Int,

    @ColumnInfo(name = "detalle")
    val detalle: String,

    @ColumnInfo(name = "nombredeusuario")
    val nombreDeUsuario: String,

    @ColumnInfo(name = "nombrecategoria")
    val nombreCategoria: String,

    @ColumnInfo(name = "fecha")
    val fecha: Date
) {
    init {
        require(nombreDeUsuario.length <= 30) {
            "nombredeusuario no puede superar 30 caracteres"
        }
        require(nombreCategoria.length <= 30) {
            "nombrecategoria no puede superar 30 caracteres"
        }
    }
}

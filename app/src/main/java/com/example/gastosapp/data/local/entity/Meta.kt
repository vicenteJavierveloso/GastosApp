package com.example.gastosapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "metas",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["nombreusuario"],
            childColumns = ["nombredeusuario"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["nombredeusuario"])
    ]
)
data class Meta(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "codigometa")
    val codigoMeta: Int = 0,

    @ColumnInfo(name = "monto")
    val monto: Int,

    @ColumnInfo(name = "nombredeusuario")
    val nombreDeUsuario: String,

    @ColumnInfo(name = "nombrecategoria")
    val nombreCategoria: String,

    @ColumnInfo(name = "fechalimite")
    val fechaLimite: Date,

    @ColumnInfo(name = "activa", defaultValue = "1")
    val activa: Boolean = true
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

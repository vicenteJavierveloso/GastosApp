package com.example.gastosapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gastosapp.data.local.dao.CategoriaDao
import com.example.gastosapp.data.local.dao.GastoDao
import com.example.gastosapp.data.local.dao.IngresoDao
import com.example.gastosapp.data.local.dao.MetaDao
import com.example.gastosapp.data.local.dao.UsuarioDao
import com.example.gastosapp.data.local.entity.Categoria
import com.example.gastosapp.data.local.entity.Gasto
import com.example.gastosapp.data.local.entity.Ingreso
import com.example.gastosapp.data.local.entity.Meta
import com.example.gastosapp.data.local.entity.Usuario

@Database(
    entities = [Usuario::class, Categoria::class, Gasto::class, Ingreso::class, Meta::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GastosDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun gastoDao(): GastoDao
    abstract fun ingresoDao(): IngresoDao
    abstract fun metaDao(): MetaDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS ingresos (
                        codigoingreso INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        monto INTEGER NOT NULL,
                        detalle TEXT NOT NULL,
                        nombredeusuario TEXT NOT NULL,
                        nombrecategoria TEXT NOT NULL,
                        fecha INTEGER NOT NULL,
                        FOREIGN KEY(nombredeusuario) REFERENCES usuarios(nombreusuario)
                            ON UPDATE CASCADE ON DELETE CASCADE,
                        FOREIGN KEY(nombrecategoria) REFERENCES categorias(nombre)
                            ON UPDATE CASCADE ON DELETE RESTRICT
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_ingresos_nombredeusuario ON ingresos(nombredeusuario)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_ingresos_nombrecategoria ON ingresos(nombrecategoria)"
                )
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS metas (
                        codigometa INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        monto INTEGER NOT NULL,
                        nombredeusuario TEXT NOT NULL,
                        FOREIGN KEY(nombredeusuario) REFERENCES usuarios(nombreusuario)
                            ON UPDATE CASCADE ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_metas_nombredeusuario ON metas(nombredeusuario)"
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE categorias ADD COLUMN tipo TEXT NOT NULL DEFAULT 'GASTO'")
            }
        }
    }
}

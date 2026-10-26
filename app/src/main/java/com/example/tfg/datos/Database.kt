package com.example.tfg.datos

import android.content.Context
import android.location.Location
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tfg.MapDao
import com.example.tfg.Marker
import com.example.tfg.Plantilla

@Entity(tableName = "visited_cells", primaryKeys = ["X", "Y"])
data class VisitedCellEntity(
    val X: Int,
    val Y: Int
)

@Entity(tableName = "puntos_ruta")      //Datos de ubicacion en segundo plano
data class PuntoRutaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lat: Double,
    val lng: Double,
    val timestampMs: Long
){
    fun a_Location(): Location{
        val anterior = Location("posicion").apply {
            latitude = lat
            longitude = lng
        }
        return anterior
    }
}

@Entity(tableName = "ajustes")
data class AjustesEntity(
    @PrimaryKey val ID: Int = 0,
    val estiloElegido: Int = 0, // del 0 al 2
    val Precision: Int = 1,
    val ImportarFotosEXIF: Boolean = false,
    val UltimaImportacionFotosMs: Long = 0

)

@Entity(tableName = "capas")
data class CapasEntity(
    @PrimaryKey val ID: Int = 0,
    val MiListaGlobal: List<String> = emptyList()
)
@Database(
    entities = [Marker::class, Plantilla::class, VisitedCellEntity::class, CapasEntity::class, AjustesEntity::class, PuntoRutaEntity::class],
    version = 13,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mapDao(): MapDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Patrón Singleton
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tfg_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
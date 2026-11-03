package com.example.tfg

import org.maplibre.geojson.Point
import java.time.LocalDate
import java.util.Locale
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.format.DateTimeFormatter

@Entity(  tableName = "markers"  )
data class Marker(
    @PrimaryKey val ID: String,
    val Localizacion: Point,
    val Nombre: String,
    val Texto: String,
    val Estilo: String,
    val Fecha: LocalDate = LocalDate.now(),
    val ImagenURL: String? = null,
    var Visitas: Int = -1,
    var Puntuacion: Float = -1.0f,
    var Cualidades: List<String> = emptyList(),
    var ToDos: List<ToDo> = emptyList(),
    var Capa: String,

){

    fun FechaString(): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.getDefault())
        return this.Fecha.format(formatter)
    }
}
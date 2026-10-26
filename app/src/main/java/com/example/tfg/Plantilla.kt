package com.example.tfg

import androidx.room.Entity
import androidx.room.PrimaryKey


data class Cualidad(
    @PrimaryKey val Nombre: String,
    val Descripcion: String,
    val Imagen: Int
)

@Entity(tableName = "plantillas")
data class Plantilla(
    @PrimaryKey val ID: String,
    val Nombre: String,
    val Texto: String,
    val Estilo: String,
    val Cualidades: List<String> = emptyList(),
    var ToDos: List<ToDo> = emptyList(),
    var Capa: String,
)
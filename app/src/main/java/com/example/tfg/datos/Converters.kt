package com.example.tfg.datos

import androidx.room.TypeConverter
import com.example.tfg.ToDo
import com.mapbox.geojson.Point
import com.google.gson.Gson
import java.time.LocalDate
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromPoint(point: Point?): String? {
        return point?.toJson() // Metodo nativo de Mapbox para pasar a JSON string
    }

    @TypeConverter
    fun toPoint(json: String?): Point? {
        return json?.let { Point.fromJson(it) }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString() // En formato estándar "YYYY-MM-DD"
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(dateString) }
    }

    // para el List<String>
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }


    // para el ToDo
    @TypeConverter
    fun fromToDoList(value: List<ToDo>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toToDoList(value: String?): List<ToDo> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<ToDo>>() {}.type
        return gson.fromJson(value, listType)
    }
}
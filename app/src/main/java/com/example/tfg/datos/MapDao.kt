package com.example.tfg

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Transaction
import com.example.tfg.datos.AjustesEntity
import com.example.tfg.datos.CapasEntity
import com.example.tfg.datos.PuntoRutaEntity
import kotlinx.coroutines.flow.Flow
import com.example.tfg.datos.VisitedCellEntity // <-- IMPORTANTE: Mantenemos el import que arregló todo

@Dao
interface MapDao {

    /**OPCIONES**/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarAjustes(ajustes: AjustesEntity)

    @Query("SELECT * FROM ajustes WHERE ID = 0")
    fun obtenerAjustes(): Flow<AjustesEntity?>

    /**OPERACIONES CON CLASES **/
    @Query("SELECT * FROM plantillas")
    fun obtenerClases(): Flow<List<Plantilla>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarClase(claseItem: Plantilla)

    @Delete
    suspend fun borrarClase(claseItem: Plantilla)

    /** Posiciones segundo plano **/

    @Insert
    suspend fun insertarPuntoRuta(punto: PuntoRutaEntity)

    @Query("SELECT * FROM puntos_ruta ORDER BY timestampMs ASC")
    suspend fun obtenerPuntosRuta(): List<PuntoRutaEntity>

    @Query("DELETE FROM puntos_ruta")
    suspend fun borrarPuntosRuta()


    /** OPERACIONES CON MARCADORES **/
    @Query("SELECT * FROM markers")
    fun obtenerMarcadores(): Flow<List<Marker>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMarcador(marker: Marker)

    @Delete
    suspend fun borrarMarcador(marker: Marker)

    /** OERACIONES CON CAPAS**/

    @Query("SELECT * FROM capas WHERE id = 0")
    fun obtenerCapas(): Flow<CapasEntity?>

    @Query("UPDATE markers SET Capa = :nuevaCapa WHERE Capa = :viejaCapa")
    suspend fun reasignarCapaMarcadores(viejaCapa: String, nuevaCapa: String)

    @Query("UPDATE plantillas SET Capa = :nuevaCapa WHERE Capa = :viejaCapa")
    suspend fun reasignarCapaPlantillas(viejaCapa: String, nuevaCapa: String)

    @Transaction
    suspend fun reasignarCapa(viejaCapa: String, nuevaCapa: String) {       //Para llamar a ambos a la vez
        reasignarCapaMarcadores(viejaCapa, nuevaCapa)
        reasignarCapaPlantillas(viejaCapa, nuevaCapa)
    }



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarCapas(ajustes: CapasEntity)


    /** OPERACIONES CON CELDAS VISITADAS **/
    @Query("SELECT * FROM visited_cells")
    fun obtenerCeldasVisitadas(): Flow<List<VisitedCellEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarCelda(celda: VisitedCellEntity)


    /** LIMPIEZA TOTAL **/
    @Query("DELETE FROM markers")
    suspend  fun borrarTodosLosMarcadores()

    @Query("DELETE FROM plantillas")
    suspend  fun borrarTodasLasClases()

    @Query("DELETE FROM visited_cells")
    suspend  fun borrarTodasLasCeldas()
}
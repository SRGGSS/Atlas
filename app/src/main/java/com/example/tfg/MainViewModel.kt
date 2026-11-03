    package com.example.tfg

    import android.content.Context
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.setValue
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import androidx.datastore.preferences.preferencesDataStore
    import org.maplibre.geojson.Point
    import kotlinx.coroutines.launch
    import android.net.Uri
    import androidx.compose.runtime.mutableStateMapOf
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.graphics.toArgb
    import com.example.tfg.MainActivity.OrigenMenuSecundario
    import com.example.tfg.datos.AjustesEntity
    import com.example.tfg.datos.AppDatabase
    import com.example.tfg.datos.CapasEntity
    import com.example.tfg.datos.VisitedCellEntity
    import kotlinx.coroutines.flow.first
    import java.time.LocalDate
    import org.locationtech.jts.geom.Geometry
    import org.locationtech.jts.geom.GeometryFactory
    import org.locationtech.jts.io.WKBReader
    import org.locationtech.jts.io.WKBWriter
    import java.io.BufferedInputStream
    import java.io.BufferedOutputStream
    import java.io.DataInputStream
    import java.io.DataOutputStream
    import java.io.File
    import java.io.FileInputStream
    import java.io.FileOutputStream

    val Context.dataStore by preferencesDataStore(name = "settings")

    data class MiObjeto(

        var UriImagen: Uri? = null,
        var NextDate: LocalDate? = null,
        var NextPointLocation: Point? = null,
        var Texto: String? = null,
        var Titulo: String? = null,
        var ListaCualidades: List<String> = emptyList(),
        var ListaChecklist: List<ToDo> = emptyList(),
        var Puntuacion: Float = -1.0f,
        var EstiloPredeterminado: String = "pin_-16777216_nulo",
        var Estado: Boolean? = null,
    ){

        var Capa by mutableStateOf("")
        var ColorEstilo by mutableStateOf(Color.Black)
        var EstiloIcono by mutableStateOf("Nada")
    }



    data class MiPlantilla(
        var Nombre: String = "",
        var Descripcion: String = "",
        var EditandoClase: String? = null,
        val EstiloInicial: String? = null,
        var ListaCualidades: List<String> = emptyList(),
        var ListaChecklist: List<ToDo> = emptyList(),
    )
    {
        var EstiloPredeterminado by mutableStateOf(EstiloInicial)

        var ColorEstilo by mutableStateOf(Color.Black)
        var EstiloIcono by mutableStateOf("Nada")

        var Capa by mutableStateOf("")
    }

    class MainViewModel : ViewModel() {

        var origenMenuSecundario by  mutableStateOf(OrigenMenuSecundario.MARCADOR)
        var CapaActiva by mutableStateOf(-1)
        var DatosProxPin = MiObjeto()

        val OpcionesEstilos = listOf("asset://estilo2.json", "asset://estilo1.json", "asset://estilo3.json")


        //Opciones
        var EstiloSelecc by mutableStateOf(0)
        var PrecisionSelecc by mutableStateOf(2)
        var ImportarFotosActivado by mutableStateOf(false)
        var UltimaImportacion: Long = 0


        var DatosProxClase = MiPlantilla()

        var EditandoPin by  mutableStateOf<String?>(null)

        var visitedCells by mutableStateOf(setOf<Pair<Int, Int>>())

        var Capas by mutableStateOf(listOf("Principal", "Fotografías"))

        val miLista = mutableStateMapOf<String, Marker>()

        var ListaClases = mutableStateMapOf<String, Plantilla>()

        var mostrarDialogo by mutableStateOf(false)

        suspend  fun cargarDatos(context: Context) {
            val dao = AppDatabase.getDatabase(context).mapDao() //Cargar todos los datos, implementado con Room, parcialmente basado en https://developer.android.com/codelabs/basic-android-kotlin-training-intro-room-flow?hl=es-419#0

            //valor inicial, no flujo
            val markersIniciales = dao.obtenerMarcadores().first()
            miLista.clear()
            markersIniciales.forEach { miLista[it.ID] = it }

            val clasesIniciales = dao.obtenerClases().first()
            ListaClases.clear()
            clasesIniciales.forEach { ListaClases[it.ID] = it }

            val capasIniciales = dao.obtenerCapas().first()
            Capas = capasIniciales?.MiListaGlobal ?: Capas      //Si esta es vacia, se mantiene el valor actual (con Principal)

            if(ListaClases.size==0)
            {

                val plantilla = Plantilla(  //Siempre debe existir esta, se aplica a las importadas automaticamente
                    ID = "primero",
                    Nombre = "Fotografías",
                    Texto = "",
                    Estilo = "pin_${Color.Black.toArgb()}_${R.drawable.icono_camara}",
                    Cualidades = emptyList(),
                    ToDos = emptyList(),
                    Capa = Capas.get(1)
                )

                viewModelScope.launch {
                    AppDatabase.getDatabase(context).mapDao().insertarClase(plantilla)
                }

            }



            val celdasIniciales = dao.obtenerCeldasVisitadas().first()
            visitedCells = celdasIniciales.map { Pair(it.X, it.Y) }.toSet()


            //Ajustes
            val ajustesIniciales = dao.obtenerAjustes().first()
            if (ajustesIniciales != null) {
                EstiloSelecc = ajustesIniciales.estiloElegido
                PrecisionSelecc = ajustesIniciales.Precision
                ImportarFotosActivado = ajustesIniciales.ImportarFotosEXIF
                UltimaImportacion = ajustesIniciales.UltimaImportacionFotosMs
            }


            viewModelScope.launch {
                dao.obtenerMarcadores().collect { lista ->
                    miLista.clear()
                    lista.forEach { miLista[it.ID] = it }
                }
            }

            viewModelScope.launch {
                dao.obtenerClases().collect { lista ->
                    ListaClases.clear()
                    lista.forEach { ListaClases[it.ID] = it }
                }
            }

            viewModelScope.launch {
                dao.obtenerCeldasVisitadas().collect { lista ->
                    val puntosTemporales = lista.map { Pair(it.X, it.Y) }.toSet()
                    visitedCells = puntosTemporales
                }
            }

            viewModelScope.launch {
                dao.obtenerAjustes().collect { ajustes ->
                    if (ajustes != null) {
                        EstiloSelecc = ajustes.estiloElegido
                        PrecisionSelecc = ajustes.Precision
                        ImportarFotosActivado = ajustes.ImportarFotosEXIF
                        UltimaImportacion = ajustes.UltimaImportacionFotosMs
                    }
                }
            }
        }

        fun agregarElemento(context: Context, item: Marker) {
            viewModelScope.launch {
                AppDatabase.getDatabase(context).mapDao().insertarMarcador(item)
            }
        }

        fun agregarCLase(context: Context, item: Plantilla) {
            viewModelScope.launch {
                AppDatabase.getDatabase(context).mapDao().insertarClase(item)
            }
        }

        fun borrarClase(context: Context, item: Plantilla) {
            viewModelScope.launch {
                AppDatabase.getDatabase(context).mapDao().borrarClase(item)
            }
        }

        fun agregarPunto(context: Context, punto: Pair<Int, Int>) {
            viewModelScope.launch {
                AppDatabase.getDatabase(context).mapDao().insertarCelda(
                    VisitedCellEntity(X = punto.first, Y = punto.second)
                )
            }
        }

        fun eliminarElemento(context: Context, item: Marker) {
            viewModelScope.launch {
                AppDatabase.getDatabase(context).mapDao().borrarMarcador(item)
            }
        }

        fun guardarCapas(context: Context) {
            viewModelScope.launch {
                val NuevasCapas = CapasEntity(
                    ID = 0,
                    MiListaGlobal = Capas
                )

                AppDatabase.getDatabase(context).mapDao().guardarCapas(NuevasCapas)
            }
        }

        fun SetAjuste(context: Context, estilo: Int? = null, precision: Int? = null, activado: Boolean? = null, UltimaImp:Long? = null) {
            if (estilo != null) {
                EstiloSelecc = estilo
            }
            if (precision != null) {
                PrecisionSelecc = precision
            }
            if (activado != null) {
                ImportarFotosActivado = activado
            }
            if (UltimaImp != null) {
                UltimaImportacion = UltimaImp
            }
            viewModelScope.launch {
                val nuevosAjustes = AjustesEntity(ID = 0, estiloElegido = EstiloSelecc, Precision = PrecisionSelecc, ImportarFotosEXIF = ImportarFotosActivado, UltimaImportacionFotosMs =  UltimaImportacion)
                AppDatabase.getDatabase(context).mapDao().guardarAjustes(nuevosAjustes)
            }
        }

        fun EditarCapa(context: Context, NombreAntiguo: String, NombreNuevo: String) {
            viewModelScope.launch {
                val dao = AppDatabase.getDatabase(context).mapDao()
                dao.reasignarCapa(NombreAntiguo, NombreNuevo)
            }
        }

        fun EliminarCapa(context: Context, capaABorrar: String,) {
            viewModelScope.launch {
                val dao = AppDatabase.getDatabase(context).mapDao()
                dao.reasignarCapa(capaABorrar, Capas.get(0))
            }

            Capas = Capas - capaABorrar
        }



        fun BorrarTodo(context: Context) {
            viewModelScope.launch {
                val dao = AppDatabase.getDatabase(context).mapDao()
                dao.borrarTodosLosMarcadores()
                dao.borrarTodasLasClases()
                dao.borrarTodasLasCeldas()
            }
        }
    }

    object CacheNiebla {    //Para no recalcular la niebla cada vez que se abre la aplicacion (Hecho con IA)

        private const val ARCHIVO = "niebla_cache.bin"
        private const val VERSION = 1
        class Datos(val geometria: Geometry, val celdas: Set<Pair<Int, Int>>, val geoJson: String)

        fun guardar(context: Context, geometria: Geometry, celdas: Set<Pair<Int, Int>>, geoJson: String) {
            val tmp = File(context.filesDir, "$ARCHIVO.tmp")
            DataOutputStream(BufferedOutputStream(FileOutputStream(tmp))).use { out ->
                out.writeInt(VERSION)

                val wkb = WKBWriter().write(geometria)
                out.writeInt(wkb.size)
                out.write(wkb)

                out.writeInt(celdas.size)
                celdas.forEach { (x, y) -> out.writeInt(x); out.writeInt(y) }

                val json = geoJson.toByteArray(Charsets.UTF_8)
                out.writeInt(json.size)
                out.write(json)
            }
            tmp.renameTo(File(context.filesDir, ARCHIVO))
        }

        fun cargar(context: Context, gf: GeometryFactory): Datos? {
            val archivo = File(context.filesDir, ARCHIVO)
            if (!archivo.exists()) return null
            return try {
                DataInputStream(BufferedInputStream(FileInputStream(archivo))).use { inp ->
                    if (inp.readInt() != VERSION) return null

                    val wkb = ByteArray(inp.readInt()).also { inp.readFully(it) }
                    val geometria = WKBReader(gf).read(wkb)

                    val n = inp.readInt()
                    val celdas = HashSet<Pair<Int, Int>>(n * 2)
                    repeat(n) { celdas.add(inp.readInt() to inp.readInt()) }

                    val json = ByteArray(inp.readInt()).also { inp.readFully(it) }
                    Datos(geometria, celdas, String(json, Charsets.UTF_8))
                }
            } catch (e: Exception) {
                null    // archivo corrupto o de otra versión: se recalcula desde cero
            }
        }
    }

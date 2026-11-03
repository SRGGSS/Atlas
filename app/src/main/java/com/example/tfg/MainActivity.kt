    package com.example.tfg
    
    import android.Manifest
    import android.content.ContentUris
    import androidx.compose.material3.Switch
    import androidx.compose.runtime.Composable
    import android.os.Bundle
    import coil.compose.AsyncImage
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.material3.Button
    import androidx.compose.material3.Text
    import androidx.compose.runtime.*
    import androidx.compose.foundation.layout.Box
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.layout.padding
    import androidx.compose.runtime.getValue
    import androidx.compose.foundation.layout.width
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.activity.viewModels
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.height
    import androidx.compose.material3.AlertDialog
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.TextButton
    import android.content.Context
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.graphics.Bitmap
    import android.graphics.Canvas
    import android.location.Location
    import android.os.Build
    import android.util.Log
    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.layout.PaddingValues
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.fillMaxHeight
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.heightIn
    import androidx.compose.foundation.layout.size
    import androidx.compose.foundation.layout.widthIn
    import androidx.compose.foundation.lazy.grid.GridCells
    import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
    import androidx.compose.foundation.lazy.grid.items
    import androidx.compose.ui.graphics.Color
    import androidx.exifinterface.media.ExifInterface
    import android.net.Uri
    import android.os.Looper
    import android.provider.MediaStore
    import androidx.compose.foundation.BorderStroke
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.aspectRatio
    import androidx.compose.foundation.layout.defaultMinSize
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.LazyRow
    import androidx.compose.foundation.lazy.grid.itemsIndexed
    import androidx.compose.foundation.lazy.itemsIndexed
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.foundation.text.BasicTextField
    import androidx.compose.foundation.text.KeyboardActions
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults
    import androidx.compose.material3.Checkbox
    import androidx.compose.material3.DropdownMenu
    import androidx.compose.material3.DropdownMenuItem
    import androidx.compose.material3.HorizontalDivider
    import androidx.compose.material3.IconButton
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Surface
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.asImageBitmap
    import androidx.compose.ui.graphics.graphicsLayer
    import androidx.compose.ui.graphics.toArgb
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.text.style.TextDecoration
    import androidx.compose.ui.unit.sp
    import androidx.compose.ui.window.Dialog
    import androidx.core.content.ContextCompat
    import androidx.core.net.toUri
    import androidx.lifecycle.Lifecycle
    import androidx.lifecycle.LifecycleEventObserver
    import androidx.lifecycle.compose.LocalLifecycleOwner
    import androidx.lifecycle.lifecycleScope
    import com.example.tfg.datos.AppDatabase
    import com.example.tfg.geoloc.RecibidorActividad
    import com.google.android.gms.location.LocationCallback
    import com.google.android.gms.location.LocationRequest
    import com.google.android.gms.location.LocationResult
    import com.google.android.gms.location.LocationServices
    import com.google.android.gms.location.Priority
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.withContext
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import org.locationtech.jts.simplify.TopologyPreservingSimplifier
    import java.io.File
    import java.time.LocalDate
    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter
    import java.util.UUID
    import kotlin.String
    import androidx.compose.ui.viewinterop.AndroidView
    import org.maplibre.android.MapLibre
    import org.maplibre.android.camera.CameraPosition
    import org.maplibre.android.geometry.LatLng
    import org.maplibre.android.location.OnCameraTrackingChangedListener
    import org.maplibre.android.maps.MapLibreMap
    import org.maplibre.android.maps.Style
    import org.maplibre.android.style.sources.GeoJsonSource
    import org.maplibre.geojson.Point

    class MainActivity : ComponentActivity() {
        val viewModel: MainViewModel by viewModels()
    
    
        var iconos_pin = mapOf(     //Estilos y sus iconos
            "Nada" to null,
            "Parque" to R.drawable.icono_parque,
            "WC" to R.drawable.icono_wc,
            "Foto" to R.drawable.icono_camara,
            "Casa" to R.drawable.icono_casa,
            "Landmark" to R.drawable.icono_landmark,
            "Estrella" to R.drawable.icono_estrella,
            "Vistas" to R.drawable.icono_vistas,
            "Bandera" to R.drawable.icono_bandera,
            "Restaurante" to R.drawable.icono_restaurante,
            "Coctel" to R.drawable.icono_coctel,
            "Montaña" to R.drawable.icono_montanya,
            "Corazon" to R.drawable.icono_corazon,
            "Tienda" to R.drawable.icono_tienda,
            "Gimnasio" to R.drawable.icono_gim,
            "Dinero" to R.drawable.icono_bill,
            "Camiseta" to R.drawable.icono_camiseta,
            "Mundo" to R.drawable.icono_mundo,
        )
    
        var listaColores = listOf<Color>(
            Color(0xFF000000),
            Color(0xFFf51111),
            Color(0xFFf0680a),
            Color(0xFFfad40a),
            Color(0xFF3bb31e),
            Color(0xFF0e2fe6),
            Color(0xFF6c199e),
    
        )
    
    
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            MapLibre.getInstance(this)

            setContent {
                SolicitarPermisosEIniciarDeteccion()
                PantallaPrincipal()
            }

        }


        private fun leerCoordenadasYFechaExif(context: Context, uri: Uri): Pair<Point, LocalDate>? {

                val uriFinal = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        MediaStore.setRequireOriginal(uri)
                    } catch (e: Exception) {
                        uri
                    }
                } else {
                    uri
                }

                return context.contentResolver.openInputStream(uriFinal)?.use { stream ->   //https://stackoverflow.com/questions/78683449/exifinterface-getlatlong-always-returns-false-on-android-13

                    ExifInterface(stream).run {

                        val latLong = floatArrayOf(0f, 0f)
                        if (!getLatLong(latLong)) return@run null   //Por alguna razon, dice que si tiene y lo pone como 0,0

                        val fechaTexto = getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)  //Esto estaba dando error
                            ?: getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED)
                            ?: getAttribute(ExifInterface.TAG_DATETIME)

                        val formatter = DateTimeFormatter
                            .ofPattern("yyyy:MM:dd HH:mm:ss", java.util.Locale.US)

                        val fecha = try {
                            LocalDateTime.parse(fechaTexto, formatter).toLocalDate()
                        } catch (e: Exception) {
                            LocalDate.now()
                        }

                        Point.fromLngLat(latLong[1].toDouble(), latLong[0].toDouble()) to fecha
                    }

                }
        }
    
    
    
    
        val bitmapsCreados = mutableSetOf<String>()        //Guarda bitmaps de estilos creados para evitar duplicar
        val PinesEnRadio = mutableSetOf<String>()       //Caché visitas (el usuario está dentro)

        private fun asegurarPinEnEstilo(    //guarda en style y bitmapscreados y devuelve el string codificado del estilo
            style: Style,
            colorCompose: Color,
            idIconoPng: Int?
        ): String {
    
            val colorInt = colorCompose.toArgb()
            val nombreEstilo = if(idIconoPng != null) "pin_${colorInt}_${idIconoPng}" else "pin_${colorInt}_nulo"
    

            // Si no lo registramos en esta sesión
            if (!bitmapsCreados.contains(nombreEstilo)) {
                val bitmap = generarPinMapaMapbox(this, colorInt, idIconoPng)
                style.addImage(nombreEstilo, bitmap)
                bitmapsCreados.add(nombreEstilo)
            }
    
            return nombreEstilo
        }
    
    
        private fun EstilosGuardados(context: Context, style: Style) //Carga el estilo de cada Marker
        {
            if(!bitmapsCreados.contains("pin_-16777216_nulo"))  //Para que siempre este
            {
                val bitmap = generarPinMapaMapbox(context, -16777216, "nulo".toIntOrNull())
                style.addImage("pin_-16777216_nulo", bitmap)
                bitmapsCreados.add("pin_-16777216_nulo")
            }
            viewModel.miLista.values.forEach { marker ->    //Carga el estilo de cada marker
                val nombreEstilo = marker.Estilo
    
                if (!bitmapsCreados.contains(nombreEstilo)){    // no está cargado esta sesión

                    val partes = nombreEstilo.removePrefix("pin_").split("_")
                    val colorInt = partes[0].toInt()
                    val idIcono = partes[1].toIntOrNull()   //Si no hay icono el texto es "nulo"

                    val bitmap = generarPinMapaMapbox(context, colorInt, idIcono)
                    style.addImage(nombreEstilo, bitmap)
                    bitmapsCreados.add(nombreEstilo)

                }
    

            }
        }


        @Composable
        fun SolicitarPermisosEIniciarDeteccion() {
            val context = LocalContext.current
            val gestorActividad = RecibidorActividad(context)


            val permisosIniciales = mutableListOf(
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permisosIniciales.add(Manifest.permission.POST_NOTIFICATIONS)
            }
    
            //Para la ubicación en segundo plano
            val launcherUbicacionFondo = rememberLauncherForActivityResult( //https://developer.android.com/develop/ui/compose/notifications/notification-permission?hl=es-419
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { concedido ->
                    if (concedido) {
                        gestorActividad.iniciarDeteccion()
                    }
                }
            )


            val launcherMultiple = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { resultados ->

                    val todosInicialesConcedidos = resultados.values.all { it == true }
                    if (todosInicialesConcedidos) {
                        // Si aceptó los iniciales se pide el de segundo plano
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val yaTieneFondo = ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED   //https://developer.android.com/training/permissions/requesting?hl=es-419

                            if (yaTieneFondo) {
                                gestorActividad.iniciarDeteccion()
                            } else {
                                launcherUbicacionFondo.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            }
                        } else {
                            gestorActividad.iniciarDeteccion()
                        }
                    }
                }
            )

            //comprobacion
            LaunchedEffect(Unit) {
                val faltanPermisosIniciales = permisosIniciales.any { permiso -> ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED}
    
                if (faltanPermisosIniciales) {
                    launcherMultiple.launch(permisosIniciales.toTypedArray())   //iniciales
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {               //Segundo plano
                        if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            launcherUbicacionFondo.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        } else {
                            gestorActividad.iniciarDeteccion()
                        }
                    } else {
                        gestorActividad.iniciarDeteccion()
                    }
                }
            }
        }
    
    
        private fun decodificarEstilo(estilo: String): Pair<Color, String> {        //Codigo String a color e icono
            val partes = estilo.removePrefix("pin_").split("_")
            val colorInt = partes.getOrNull(0)?.toIntOrNull()
            val idIconoInt = partes.getOrNull(1)?.toIntOrNull()
            val color = colorInt?.let { Color(it) } ?: listaColores[0]
            val nombreIcono = iconos_pin.entries.find { it.value == idIconoInt }?.key ?: "Nada"
            return color to nombreIcono
        }
    
    
        private fun aplicarPlantilla(
            plantilla: Plantilla,
            viewModel: MainViewModel,
            mapaGlobal: MapLibreMap?
        ) {
            val pin = viewModel.DatosProxPin
    
            if (plantilla.Texto.isNotEmpty()) pin.Texto = plantilla.Texto

            if (plantilla.Estilo.isNotEmpty()) {
                pin.EstiloPredeterminado = plantilla.Estilo
                val (color, icono) = decodificarEstilo(plantilla.Estilo)
                pin.ColorEstilo = color
                pin.EstiloIcono = icono


                mapaGlobal?.getStyle { style ->
                    asegurarPinEnEstilo( style, color, iconos_pin[icono])
                }
            }
    
            if (plantilla.Capa.isNotEmpty()) pin.Capa = plantilla.Capa
            if (plantilla.Cualidades.isNotEmpty()) pin.ListaCualidades = plantilla.Cualidades
            if (plantilla.ToDos.isNotEmpty()) pin.ListaChecklist = plantilla.ToDos
        }


        enum class OrigenMenuSecundario { MARCADOR, AJUSTES }   //Para menus que pueden llegar desde multiples puntos (para retornar)
    
        @Composable fun PantallaPrincipal(){
    
            var ElegirEstiloClase by remember { mutableStateOf(false) }
    
            var mapaGlobal by remember { mutableStateOf<MapLibreMap?>(null) } //Referencia al mapa
    
            var CrearPin by remember { mutableStateOf(false) }
    
            var MostrarAjustes by remember { mutableStateOf(false) }
    
            var ElegirEstilo by remember { mutableStateOf(false) }
    
            var MenuClases by remember { mutableStateOf(false) }
    
            var CrearClases by remember { mutableStateOf(false) }
    
            var MenuCheckList by remember { mutableStateOf(false) }
    
            var MenuCapas by remember { mutableStateOf(false) }

            val versionCamara = remember { mutableIntStateOf(0) }
    
            var MenuWidgets by remember { mutableStateOf(false) }
    
            val context = LocalContext.current
    
            var pointTocado by remember { mutableStateOf<String?>(null) }      //Cuando un Pin es tocado (para expandir)

            var marcadoresCercanos by remember { mutableStateOf<List<Marker>>(emptyList()) }    //Para ver los otros de la oclusion
            var indiceMarcadorActual by remember { mutableStateOf(0) }
    
            var currentlocation by remember { mutableStateOf<Point?>(null) }
    
            var seguir by remember { mutableStateOf(false) }
    
            val PosicionesMarker = viewModel.miLista

            var progresoImportacion by remember { mutableStateOf<Pair<Int, Int>?>(null) }

            var permisoUbicacionConcedido by remember {
                mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            }

            var estiloMapa by remember { mutableStateOf<Style?>(null) }

            val marcadoresGeoJson by remember {
                derivedStateOf<String> { marcadoresAGeoJson(PosicionesMarker.values.filter { marker -> viewModel.CapaActiva < 0 || viewModel.Capas.get(viewModel.CapaActiva) == marker.Capa }) }
            }


            val worldBounds = listOf(
                Point.fromLngLat(-180.0, 90.0),
                Point.fromLngLat(180.0, 90.0),
                Point.fromLngLat(180.0, -90.0),
                Point.fromLngLat(-180.0, -90.0),
                Point.fromLngLat(-180.0, 90.0)
            )


            //SECCION CELDAS
            val TAMANO_CELDA = 0.0005
            val RADIO_CIRCULO = 0.000425


            var clearedGeometry by remember { mutableStateOf<org.locationtech.jts.geom.Geometry?>(null) }

            val celdasEnNiebla = remember { HashSet<Pair<Int, Int>>() }   // celdas ya incluidas en clearedGeometry
            var datosIniciales by remember { mutableStateOf(false) }
    

            val pendingCells = remember { mutableListOf<Pair<Int, Int>>() }
            val gf = remember { org.locationtech.jts.geom.GeometryFactory() }


            fun CoordenadasACelda(lat: Double, lng: Double): Pair<Int, Int> {
                val gridX = Math.floor(lng / TAMANO_CELDA).toInt()
                val gridY = Math.floor(lat / TAMANO_CELDA).toInt()
                return Pair(gridX, gridY)
            }


            fun comprobarVisitasMarcadores(l : Location) {
                viewModel.miLista.values.forEach { marker ->
                    if (marker.Visitas >= 0) {
                        val distancia = l.distanceTo(Location("point").apply {
                            latitude = marker.Localizacion.latitude()
                            longitude = marker.Localizacion.longitude()
                        })

                        if (distancia <= 30 && !PinesEnRadio.contains(marker.ID)) {
                            marker.Visitas += 1
                            PinesEnRadio.add(marker.ID)
                            viewModel.agregarElemento(context, marker)
                        } else if (distancia >=60 && PinesEnRadio.contains(marker.ID)) {    //Doble de distancia requerida para salir (para evitar que se cuente multiples veces en el borde)
                            PinesEnRadio.remove(marker.ID)
                            viewModel.agregarElemento(context, marker)
                        }
                    }
                }
            }

    
            fun crearCirculoJTS(centroLon: Double, centroLat: Double, radioGrados: Double): org.locationtech.jts.geom.Polygon {

                val lados = 24
                val coords = Array(lados + 1) { i ->
                    val angulo = 2 * Math.PI * i / lados
                    val aspect = Math.cos(Math.toRadians(centroLat))
                    val x = centroLon + (radioGrados / aspect) * Math.cos(angulo)
                    val y = centroLat + radioGrados * Math.sin(angulo)
                    org.locationtech.jts.geom.Coordinate(x, y)
                }
                coords[lados] = coords[0] // conecta el último punto con el primero
                return gf.createPolygon(coords)
            }
    

            var fogOfWarGeoJson by remember {
                mutableStateOf(org.maplibre.geojson.Polygon.fromLngLats(listOf(worldBounds)).toJson())
            }




            suspend fun procesarPuntosDeRuta() {

                val dao = AppDatabase.getDatabase(context).mapDao() //Accedemos a puntos guardados
                val puntos = withContext(Dispatchers.IO) { dao.obtenerPuntosRuta() }
                if (puntos.isEmpty()) return


                val celdasNuevas = mutableSetOf<Pair<Int, Int>>()
                celdasNuevas.add(CoordenadasACelda(puntos[0].lat, puntos[0].lng))

                for (i in 1 .. puntos.size-1) { //Se conectan todos los puntos
                    val anterior = puntos[i - 1]
                    val actual = puntos[i]
                    celdasNuevas.add(CoordenadasACelda(actual.lat, actual.lng))
                    comprobarVisitasMarcadores(actual.a_Location())

                    val distanciaM = anterior.a_Location().distanceTo(actual.a_Location())//Calcula si es factible o ha sido un salto del GPS
                    val segundos = (actual.timestampMs - anterior.timestampMs) / 1000.0
                    val distanciaMaximaPlausible = 42.0 * segundos // 151 km/h


                    val pasoMetros = 27.5 // ~media celda
                    if (distanciaM <= distanciaMaximaPlausible && distanciaM > pasoMetros) {
                        val pasos = (distanciaM / pasoMetros).toInt().coerceAtLeast(1)
                        for (p in 1 .. pasos-1) {
                            val fraccion = p.toDouble() / pasos
                            val lat = anterior.lat + (actual.lat - anterior.lat) * fraccion
                            val lng = anterior.lng + (actual.lng - anterior.lng) * fraccion
                            celdasNuevas.add(CoordenadasACelda(lat, lng))
                            comprobarVisitasMarcadores(Location("manual").apply
                                {latitude = lat
                                longitude = lng })
                        }
                    }
                }

                val celdasNuevasFinal = celdasNuevas.filterNot { it in viewModel.visitedCells }     //Las no repetidas
                celdasNuevasFinal.forEach { celda -> viewModel.agregarPunto(context, celda) }
                synchronized(pendingCells) { pendingCells.addAll(celdasNuevasFinal) }

                withContext(Dispatchers.IO) { dao.borrarPuntosRuta() }
            }







            suspend fun obtenerMarkersGaleria(      //Crea marker por cada imagen de la galeria pendiente
                context: Context,
                checkpoint: Long
            ): Pair<List<Marker>, Long> {

                val resultado = mutableListOf<Marker>()
                var maxFechaEncontrada = checkpoint


                val projection = arrayOf(MediaStore.Images.Media._ID,MediaStore.Images.Media.DATE_ADDED)
                val selection = "${MediaStore.Images.Media.DATE_ADDED} > ?" //EN order ascendiente y despues del checkpoint (en segundos)    https://stackoverflow.com/questions/76929927/getting-images-from-external-storage
                val selectionArgs = arrayOf((checkpoint / 1000).toString())
                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} ASC"

                context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )?.use { cursor ->
                    val total = cursor.count
                    var procesadas = 0




                    val clase = viewModel.ListaClases["primero"]

                    val texto = if (clase!!.Texto.isNotEmpty()) clase!!.Texto else ""

                    val estilo = clase!!.Estilo
                    val (color, icono) = decodificarEstilo(clase!!.Estilo)

                    withContext(Dispatchers.Main) {
                        mapaGlobal?.getStyle { style -> asegurarPinEnEstilo(style, color, iconos_pin[icono]) }
                    }

                    val capa = clase!!.Capa
                    val cualidades = clase!!.Cualidades //Si es empty se aplica igual, no afecta el resultado
                    val todos = clase!!.ToDos           //Lo mismo



                    while (cursor.moveToNext()) {

                        val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                        maxFechaEncontrada = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)) * 1000


                        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                        val imagenesExistentes = viewModel.miLista.values.mapTo(mutableSetOf()) { it.ImagenURL }        //Para evitar imagenes que, con o sin sincronizacion ya estaban puestas

                        if (uri.toString() !in imagenesExistentes) {
                            leerCoordenadasYFechaExif(context, uri)?.let { (point, fecha) ->


                                resultado += Marker(
                                    ID = UUID.randomUUID().toString(),
                                    Localizacion = point,
                                    Nombre = "",
                                    Estilo = estilo,
                                    Texto = texto,
                                    Fecha = fecha,
                                    ImagenURL = uri.toString(),
                                    Capa = capa,
                                    Cualidades = cualidades,
                                    ToDos = todos
                                )

                                val gridPos = CoordenadasACelda(point.latitude(), point.longitude())

                                if (gridPos !in viewModel.visitedCells) {
                                    viewModel.agregarPunto(context, gridPos)
                                    synchronized(pendingCells) {
                                        pendingCells.add(gridPos)
                                    }
                                }
                            }
                        }

                        procesadas++

                            withContext(Dispatchers.Main) {
                                progresoImportacion = procesadas to total
                            }

                    }
                }

                return resultado to maxFechaEncontrada
            }


            var escaneoEnCurso by remember { mutableStateOf(false) }    //Porque se hacia 2 veces en el analisis original

            suspend fun escanearGaleriaExif() // rastreo de galería
            {

                if (escaneoEnCurso){
                    return
                }


                try {
                    escaneoEnCurso = true
                    val ultimoCheckpoint = viewModel.UltimaImportacion

                val (markers, nuevoCheckpoint) = withContext(Dispatchers.IO) {
                    obtenerMarkersGaleria(
                        context = context,
                        checkpoint = ultimoCheckpoint,
                    )
                }

                markers.forEach {
                    viewModel.agregarElemento(context, it)
                }

                viewModel.SetAjuste(this, UltimaImp = nuevoCheckpoint)

                progresoImportacion = null


                } finally {
                    escaneoEnCurso = false
                }
            }





            suspend fun procesarCeldasPendientes() {    //Conecta con vecinos
                val celdas: List<Pair<Int, Int>>
                synchronized(pendingCells) {
                    if (pendingCells.isEmpty()) return
                    celdas = pendingCells.toList()
                    pendingCells.clear()
                }

                val visitadasSnapshot = viewModel.visitedCells.toSet()
                val geometriaPrevia = clearedGeometry

                val (nuevaGeometria, geoJson) = withContext(Dispatchers.Default) {
                    val cellPolygons = mutableListOf<org.locationtech.jts.geom.Polygon>()

                    celdas.forEach { (x, y) ->
                        val centerLon = (x + 0.5) * TAMANO_CELDA
                        val centerLat = (y + 0.5) * TAMANO_CELDA
                        cellPolygons.add(crearCirculoJTS(centerLon, centerLat, RADIO_CIRCULO))

                        for (dx in -1..1) {
                            for (dy in -1..1) {
                                if (dx == 0 && dy == 0) continue
                                val vecino = Pair(x + dx, y + dy)
                                if (visitadasSnapshot.contains(vecino)) {
                                    val vecinoLon = (vecino.first + 0.5) * TAMANO_CELDA
                                    val vecinoLat = (vecino.second + 0.5) * TAMANO_CELDA
                                    for (step in 1..3) {
                                        val fraction = step / 4.0
                                        val interpLon = centerLon + (vecinoLon - centerLon) * fraction
                                        val interpLat = centerLat + (vecinoLat - centerLat) * fraction
                                        cellPolygons.add(crearCirculoJTS(interpLon, interpLat, RADIO_CIRCULO))
                                    }
                                }
                            }
                        }
                    }

                    val piezaNueva = gf.createGeometryCollection(cellPolygons.toTypedArray()).union()
                    val fusionTotal = geometriaPrevia?.union(piezaNueva) ?: piezaNueva
                    val geometria = TopologyPreservingSimplifier.simplify(fusionTotal, 0.000015)

                    // Conversion geojson en segundo plano
                    val mundoConAgujeros = mutableListOf(worldBounds)
                    val islasSinRevelar = mutableListOf<List<Point>>()
                    for (i in 0 until geometria.numGeometries) {
                        val poly = geometria.getGeometryN(i) as org.locationtech.jts.geom.Polygon
                        mundoConAgujeros.add(poly.exteriorRing.coordinates.map { Point.fromLngLat(it.x, it.y) })
                        for (r in 0 until poly.numInteriorRing) {
                            islasSinRevelar.add(poly.getInteriorRingN(r).coordinates.map { Point.fromLngLat(it.x, it.y) })
                        }
                    }
                    val allPolygons = mutableListOf<List<List<Point>>>(mundoConAgujeros)
                    islasSinRevelar.forEach { isla -> allPolygons.add(listOf(isla)) }

                    geometria to org.maplibre.geojson.MultiPolygon.fromLngLats(allPolygons).toJson()
                }


                clearedGeometry = nuevaGeometria
                fogOfWarGeoJson = geoJson
                celdasEnNiebla.addAll(celdas)

                // Guarda en cache
                withContext(Dispatchers.IO) {
                    CacheNiebla.guardar(context, nuevaGeometria, celdasEnNiebla.toSet(), geoJson)
                }

            }

            // Se recalcula cada 700 ms
            LaunchedEffect(Unit) {

                while (true) {
                    delay(700)
                    procesarCeldasPendientes()
                }
            }
    


    
            LaunchedEffect(Unit) {  //Inicio app
    
                //viewModel.BorrarTodo(context)

                val cache = withContext(Dispatchers.IO) { CacheNiebla.cargar(context, gf) }
                if (cache != null) {
                    clearedGeometry = cache.geometria
                    celdasEnNiebla.addAll(cache.celdas)
                    fogOfWarGeoJson = cache.geoJson
                }

                viewModel.cargarDatos(context)

                procesarPuntosDeRuta()

                val faltan = viewModel.visitedCells.filter { it !in celdasEnNiebla}
                synchronized(pendingCells) {
                    pendingCells.addAll(faltan)
                }
                procesarCeldasPendientes()

                datosIniciales = true
            }

            LaunchedEffect(viewModel.miLista.size, estiloMapa) {
                estiloMapa?.let { EstilosGuardados(context, it) }
            }


            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        permisoUbicacionConcedido = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                        (context as? ComponentActivity)?.lifecycleScope?.launch {
                            if (!datosIniciales) return@launch
                            procesarPuntosDeRuta()
                            procesarCeldasPendientes()
                            if (viewModel.ImportarFotosActivado) {
                                escanearGaleriaExif()
                            }
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }

            DisposableEffect(permisoUbicacionConcedido) {//Localización en primer plano
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                var callbackRegistrado: LocationCallback? = null

                if (permisoUbicacionConcedido) {
                    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000L)
                        .setMinUpdateIntervalMillis(2000L)
                        .setMinUpdateDistanceMeters(8f)
                        .build()

                    val callback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            result.lastLocation?.let { location ->
                                currentlocation = Point.fromLngLat(location.longitude, location.latitude)
                                val gridPos = CoordenadasACelda(location.latitude, location.longitude)
                                if (gridPos !in viewModel.visitedCells) {
                                    viewModel.agregarPunto(context, gridPos)
                                    synchronized(pendingCells) { pendingCells.add(gridPos) }
                                }
                                comprobarVisitasMarcadores(location)
                            }
                        }
                    }
                    callbackRegistrado = callback
                    fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
                }

                onDispose {
                    callbackRegistrado?.let { fusedLocationClient.removeLocationUpdates(it) }
                }
            }


            fun alTocarMarcador(id: String) {
                val map = mapaGlobal ?: return
                val coincidencia = PosicionesMarker[id] ?: return
                pointTocado = id
                val pixelTocado = map.projection.toScreenLocation(coincidencia.Localizacion.aLatLng())
                val cercanos = PosicionesMarker.values.filter { marker ->
                    if (!(viewModel.CapaActiva < 0 || viewModel.Capas.getOrNull(viewModel.CapaActiva) == marker.Capa)) return@filter false
                    val px = map.projection.toScreenLocation(marker.Localizacion.aLatLng())
                    Math.hypot((pixelTocado.x - px.x).toDouble(), (pixelTocado.y - px.y).toDouble()) < 90.0
                }
                marcadoresCercanos = cercanos
                indiceMarcadorActual = cercanos.indexOf(coincidencia).coerceAtLeast(0)
            }


            val mapView = rememberMapViewConCicloDeVida()
            val opacidad = if (viewModel.EstiloSelecc == 1) 0.3f else 0.15f

            LaunchedEffect(mapView) {
                mapView.getMapAsync { map ->
                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(39.47, -0.37)).zoom(14.0).tilt(0.0).bearing(0.0).build()
                    map.setMinZoomPreference(1.0)
                    map.uiSettings.isCompassEnabled = false
                    map.uiSettings.isLogoEnabled = false
                    map.uiSettings.isAttributionEnabled = true   // ver nota abajo

                    map.addOnCameraMoveListener { versionCamara.intValue++ }
                    map.addOnMapClickListener { punto ->
                        if (CrearPin) {
                            viewModel.DatosProxPin.NextPointLocation = Point.fromLngLat(punto.longitude, punto.latitude)
                            viewModel.mostrarDialogo = true
                            CrearPin = false
                            return@addOnMapClickListener true
                        }
                        val id = idMarcadorEn(map, punto) ?: return@addOnMapClickListener false
                        alTocarMarcador(id)
                        true
                    }
                    map.locationComponent.addOnCameraTrackingChangedListener(object : OnCameraTrackingChangedListener {
                        override fun onCameraTrackingDismissed() { seguir = false }   // = ViewportStatus.Idle
                        override fun onCameraTrackingChanged(currentMode: Int) {}
                    })
                    mapaGlobal = map
                }
            }

            LaunchedEffect(mapaGlobal, viewModel.EstiloSelecc) {   // carga / cambio de estilo
                val map = mapaGlobal ?: return@LaunchedEffect
                estiloMapa = null
                map.setStyle(Style.Builder().fromUri(viewModel.OpcionesEstilos[viewModel.EstiloSelecc])) { style ->
                    bitmapsCreados.clear()
                    EstilosGuardados(context, style)
                    anadirCapasAtlas(style, fogOfWarGeoJson, marcadoresGeoJson, opacidad)
                    estiloMapa = style
                }
            }

            LaunchedEffect(estiloMapa, permisoUbicacionConcedido) {   // puck (se activa una vez; sobrevive a cambios de estilo)
                val map = mapaGlobal ?: return@LaunchedEffect
                val style = estiloMapa ?: return@LaunchedEffect
                if (permisoUbicacionConcedido && !map.locationComponent.isLocationComponentActivated) {
                    activarUbicacion(context, map, style)
                    seguirUbicacion(map, inmediato = true)
                }
            }

            LaunchedEffect(estiloMapa, fogOfWarGeoJson) {
                estiloMapa?.getSourceAs<GeoJsonSource>(MapaIds.FUENTE_NIEBLA)?.setGeoJson(fogOfWarGeoJson)
            }
            LaunchedEffect(estiloMapa, marcadoresGeoJson) {
                estiloMapa?.getSourceAs<GeoJsonSource>(MapaIds.FUENTE_MARCADORES)?.setGeoJson(marcadoresGeoJson)
            }
            LaunchedEffect(seguir, estiloMapa) {
                if(mapaGlobal==null)
                {
                    return@LaunchedEffect
                }
                val map = mapaGlobal!!

                if (seguir && estiloMapa != null) seguirUbicacion(map)
            }

    
            Box(modifier = Modifier.fillMaxSize()) {

                AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

                if (pointTocado != null && marcadoresCercanos.isNotEmpty()) {
                    val marker = marcadoresCercanos.getOrNull(indiceMarcadorActual)
                    if (marker != null) {
                        Box(Modifier.anclarEnMapa(mapaGlobal, marker.Localizacion.aLatLng()) { versionCamara.intValue }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // flecha izquierda
                                Box(
                                    modifier = Modifier.size(44.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (marcadoresCercanos.size > 1 && indiceMarcadorActual > 0) {
                                        Image(
                                            painter = painterResource(id = R.drawable.flecha2),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(38.dp)
                                                .graphicsLayer {
                                                    scaleX = -1f
                                                }
                                                .clickable {indiceMarcadorActual--}
                                        )
                                    }
                                }


                                Column(
                                    modifier = Modifier
                                        .widthIn(min = 120.dp, max = 220.dp)
                                        .heightIn(max = 1000.dp)
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = Color.Black,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Fila Superior: Fecha y botón Cerrar (X)
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        /*Text(
                                            text = marker.FechaString(),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB2B1B1),
                                            fontSize = 11.sp,
                                            modifier = Modifier.align(Alignment.CenterStart)
                                        )*/
                                        //Botón dirección
                                        Image(
                                            painter = painterResource(id = R.drawable.navegar),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .size(24.dp)
                                                .clickable
                                                {   //https://developer.android.com/guide/components/google-maps-intents?hl=es-419#kotlin_2
                                                    val latitude = marker.Localizacion.latitude()
                                                    val longitude = marker.Localizacion.longitude()


                                                    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(Ubicacion)")
                                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                                    mapIntent.setPackage("com.google.android.apps.maps")
                                                    mapIntent.resolveActivity(packageManager)?.let {
                                                        startActivity(mapIntent)
                                                    }
                                                }
                                        )


                                        Text(
                                            text = marker.Nombre,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .padding(bottom = 4.dp)
                                        )

                                        Image(
                                            painter = painterResource(id = R.drawable.quitar_menu),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(28.dp)
                                                .clickable {pointTocado = null}
                                        )
                                    }

                                    if (marker.ImagenURL != null) {
                                        AsyncImage(
                                            model = marker.ImagenURL,
                                            contentDescription = "",
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                                .fillMaxWidth()
                                                .aspectRatio(1f),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    else
                                    {
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }

                                    if (marker.Texto.length > 0) {
                                        Text(
                                            text = marker.Texto,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Left,
                                            modifier = Modifier.padding(
                                                top = 4.dp,
                                                bottom = 4.dp
                                            )
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }

                                    if (marker.Visitas >= 0) {
                                        Row(
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .border(
                                                    1.dp,
                                                    Color(0xFF242424),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .background(
                                                    Color(0xFF4d4d4d),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${marker.Visitas} visitas",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    if (marker.Puntuacion >= 0) {
                                        var puntuacion by remember { mutableStateOf(marker.Puntuacion) }
                                        PuntuacionEstrellas(
                                            puntuacion = puntuacion,
                                            onPuntuacionChange = { nueva ->
                                                puntuacion = nueva
                                                marker.Puntuacion = nueva
                                                viewModel.agregarElemento(context, marker)
                                            }
                                        )
                                    }

                                    if (marker.ToDos.size > 0) {
                                        CheckList(
                                            marker.ToDos,
                                            onChecked = { lista ->
                                                marker.ToDos = lista
                                                viewModel.agregarElemento(context, marker)
                                            }
                                        )
                                    }

                                    if (marker.Cualidades.contains("Clima")) {
                                        ClimaWidget(
                                            marker.Localizacion.latitude(),
                                            marker.Localizacion.longitude()
                                        )
                                    }

                                    // FILA INFERIOR: EDITAR, X de Y, BORRAR
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Editar
                                        Image(
                                            painter = painterResource(id = R.drawable.editar),
                                            contentDescription = "Editar",
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clickable {
                                                    val (color, icono) = decodificarEstilo(
                                                        marker.Estilo
                                                    )
                                                    viewModel.EditandoPin = marker.ID
                                                    viewModel.DatosProxPin = MiObjeto(
                                                        Texto = marker.Texto,
                                                        NextDate = marker.Fecha,
                                                        Titulo = marker.Nombre,
                                                        UriImagen = marker.ImagenURL?.toUri(),
                                                        NextPointLocation = marker.Localizacion,
                                                        ListaCualidades = marker.Cualidades,
                                                        ListaChecklist = marker.ToDos,
                                                        EstiloPredeterminado = marker.Estilo
                                                    )
                                                    viewModel.DatosProxPin.Capa = marker.Capa
                                                    viewModel.DatosProxPin.ColorEstilo = color
                                                    viewModel.DatosProxPin.EstiloIcono = icono
                                                    pointTocado = null
                                                    viewModel.mostrarDialogo = true
                                                }
                                        )



                                        Text(
                                            text = marker.FechaString(),
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB2B1B1),
                                            fontSize = 11.sp
                                        )


                                        // Borrar
                                        Image(
                                            painter = painterResource(id = R.drawable.papelera),
                                            contentDescription = "Eliminar",
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clickable {
                                                    marker.ImagenURL?.let { ruta ->
                                                        var indUlt = ruta.lastIndexOf('/')
                                                        var Despues = ruta.substring(indUlt + 1)
                                                        if (Despues.take(16) == "NuestraImCopiada") {
                                                            val fdelete = File(ruta)
                                                            if (fdelete.exists()) fdelete.delete()
                                                        }
                                                    }
                                                    viewModel.eliminarElemento(context, marker)
                                                    pointTocado = null
                                                }
                                        )
                                    }

                                    if (marcadoresCercanos.size > 1) {
                                        Text(
                                            text = "${indiceMarcadorActual + 1} de ${marcadoresCercanos.size}",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                }



                                // Flecha derecha

                                Box(
                                    modifier = Modifier.size(44.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (marcadoresCercanos.size > 1 && indiceMarcadorActual < marcadoresCercanos.size - 1) {
                                        Image(
                                            painter = painterResource(id = R.drawable.flecha2),
                                            contentDescription = "Eliminar",
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clickable {indiceMarcadorActual++}
                                        )
                                    }
                                }
                            }
                        }
                    }
                }


                Row(    //Bottom Bar
                    Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 35.dp, start = 20.dp, end = 20.dp),
                    Arrangement.SpaceBetween
                )
                {

                    IconButton(
                        onClick = { MostrarAjustes = true },
                        modifier = Modifier.size(55.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ajustes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color.White,
                        shadowElevation = 20.dp
                    )
                    {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical = 10.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            Image(
                                painter = painterResource(id = R.drawable.flecha2),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .graphicsLayer {
                                        scaleX = -1f
                                    }
                                    .clickable {
                                        if (viewModel.CapaActiva < 0) {
                                            viewModel.CapaActiva = viewModel.Capas.size - 1
                                        } else {
                                            viewModel.CapaActiva--
                                        }
                                    }
                            )


                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.que_capa),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(110.dp)
                                )

                                val texto = if(viewModel.CapaActiva<0) "Todas" else viewModel.Capas.get(viewModel.CapaActiva)
                                Text(
                                    text = texto,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }

                            Image(
                                painter = painterResource(id = R.drawable.flecha2),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable {
                                        if (viewModel.CapaActiva >= viewModel.Capas.size - 1) {
                                            viewModel.CapaActiva = -1
                                        } else {
                                            viewModel.CapaActiva++
                                        }
                                    }
                            )
                        }
                    }
                    if(!CrearPin)
                    {
                        IconButton(
                            onClick = {
                                CrearPin=true
                            },
                            modifier = Modifier.size(55.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.agregar),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    else
                    {
                        IconButton(
                            onClick = {
                                val loc = mapaGlobal?.locationComponent?.takeIf { it.isLocationComponentActivated }?.lastKnownLocation
                                viewModel.DatosProxPin.NextPointLocation = loc?.let { Point.fromLngLat(it.longitude, it.latitude) } ?: currentlocation
                                viewModel.mostrarDialogo = true;
                                CrearPin = false;
                            },
                            modifier = Modifier.size(55.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pinaqui),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                }






                if(CrearPin)
                {
                    IconButton(
                        onClick = { CrearPin = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top= 32.dp, end = 12.dp)
                            .size(42.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cerrar),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }



                progresoImportacion?.let { (procesadas, total) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(top = 46.dp, start = 90.dp, end = 90.dp)
                            .background(Color.Black, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = "Importando fotos ${procesadas}/${total}", color = Color.White, fontSize = 13.sp)
                    }
                }

                if(!seguir)
                {
                    IconButton(
                        onClick = { seguir = true },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top= 32.dp, start = 12.dp)
                            .size(42.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.brujula),
                            contentDescription = null,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
    
    
    
                if (viewModel.mostrarDialogo) {
    
                    MiMenuEmergente(
                        onDismiss = { viewModel.mostrarDialogo = false },
                        onConfirm = { nombre, estilo, texto, imagenURL, estado, cualidades, CheckLists ->
    
                                viewModel.DatosProxPin.NextPointLocation?.let { location ->
                                    val mark = Marker(
                                        ID = (if(viewModel.EditandoPin!=null) viewModel.EditandoPin else UUID.randomUUID().toString())!!,
                                        Localizacion = location,
                                        Nombre = nombre,
                                        Estilo = estilo,
                                        Texto = texto,
                                        Fecha = (if (viewModel.DatosProxPin.NextDate == null) LocalDate.now() else viewModel.DatosProxPin.NextDate)!!,
                                        ImagenURL = if(imagenURL == null) null else imagenURL.toString(),
                                        ToDos = CheckLists,
                                        Capa = viewModel.DatosProxPin.Capa
    
                                    )
    
                                    mark.Cualidades = cualidades.toList()
    
                                    if (!cualidades.contains("CheckList"))
                                    {
                                        if(CheckLists.size>0){
                                            mark.ToDos = emptyList()
                                        }
                                    }
    
    
                                    if(viewModel.EditandoPin!=null)
                                    {
                                        viewModel.miLista.get(viewModel.EditandoPin)?.let { mark.Puntuacion = it.Puntuacion }
                                    }

    
    
                                    if(cualidades.contains("Puntuacion"))
                                    {
                                        if(mark.Puntuacion<0.0f){
                                            mark.Puntuacion=0.0f
                                        }
                                    }
                                    else
                                    {
                                        if(mark.Puntuacion>=0.0f){
                                            mark.Puntuacion=-1.0f
                                        }
                                    }

                                    if(cualidades.contains("Contador"))
                                    {
                                        if(mark.Visitas<0){
                                            mark.Visitas=0
                                        }
                                    }
                                    else
                                    {
                                        if(mark.Visitas>=0){
                                            mark.Visitas=-1
                                        }
                                    }
    
    
    
    
    
                                    viewModel.agregarElemento(context,mark)
                                    viewModel.DatosProxPin = MiObjeto() //Se reinician los valores
    
                                    if(estado)
                                    {
                                        val gridPos = CoordenadasACelda(location.latitude(), location.longitude())
    
                                        if (gridPos !in viewModel.visitedCells) {
                                            viewModel.agregarPunto(context, gridPos)
                                            synchronized(pendingCells) {
                                                pendingCells.add(gridPos)
                                            }
                                        }
                                    }
                                    viewModel.EditandoPin=null
                                }
    
    
    
                                viewModel.mostrarDialogo = false
    
    
    
    
                        },
                        onElegirMarker = {
                            ElegirEstilo = true
                            viewModel.mostrarDialogo = false
                        },
                        onElegirClase = {
                            viewModel.origenMenuSecundario = OrigenMenuSecundario.MARCADOR
                            MenuClases = true
                            viewModel.mostrarDialogo = false
                        },
                        onElegirWidget = {
                            viewModel.origenMenuSecundario = OrigenMenuSecundario.MARCADOR
                            MenuWidgets = true
                            viewModel.mostrarDialogo = false
                        },
                        OnEditChecklist = {
                            viewModel.origenMenuSecundario = OrigenMenuSecundario.MARCADOR
                            MenuCheckList = true
                            viewModel.mostrarDialogo = false
                        },
                        OnEditCapas = {
                            viewModel.origenMenuSecundario = OrigenMenuSecundario.MARCADOR
                            viewModel.mostrarDialogo = false
                            MenuCapas = true
                        }
                    )
                }
    
                if (ElegirEstilo) {
                    MenuEstiloMarker(
                        colorSeleccionado = viewModel.DatosProxPin.ColorEstilo,
                        iconoSeleccionado = viewModel.DatosProxPin.EstiloIcono,
                        onColorChange = { viewModel.DatosProxPin.ColorEstilo = it },
                        onIconoChange = { viewModel.DatosProxPin.EstiloIcono = it },
                        onDismiss = { ElegirEstilo = false; viewModel.mostrarDialogo = true },
                        onAccept = {
                            mapaGlobal?.getStyle { style ->
                                val nombreEstiloFinal = asegurarPinEnEstilo( style, viewModel.DatosProxPin.ColorEstilo, iconos_pin[viewModel.DatosProxPin.EstiloIcono])
                                viewModel.DatosProxPin.EstiloPredeterminado = nombreEstiloFinal
                            }
                            ElegirEstilo = false
                            viewModel.mostrarDialogo = true
                        }
                    )
                }
    
                if (ElegirEstiloClase) {
                    MenuEstiloMarker(
                        colorSeleccionado = viewModel.DatosProxClase.ColorEstilo,
                        iconoSeleccionado = viewModel.DatosProxClase.EstiloIcono,
                        onColorChange = { viewModel.DatosProxClase.ColorEstilo = it },
                        onIconoChange = { viewModel.DatosProxClase.EstiloIcono = it },
                        onDismiss = { ElegirEstiloClase = false; CrearClases = true },
                        onAccept = {
                            mapaGlobal?.getStyle { style ->
                                val nombreEstiloFinal = asegurarPinEnEstilo( style, viewModel.DatosProxClase.ColorEstilo, iconos_pin[viewModel.DatosProxClase.EstiloIcono])
                                viewModel.DatosProxClase.EstiloPredeterminado = nombreEstiloFinal
                            }
                            ElegirEstiloClase = false
                            CrearClases = true
                        }
                    )
                }

                if(MenuWidgets) {
                    MenuWidgets(
                        esClase = CrearClases,
                        onDismiss = {
                            MenuWidgets = false

                            if (!CrearClases) {
                                viewModel.mostrarDialogo = true
                            }
                        }
                    )
                }

                if(MenuCheckList) {
                    MenuCheckList(
                        esClase = CrearClases,
                        onDismiss = {
                            MenuCheckList = false
                            if (!CrearClases) {
                                viewModel.mostrarDialogo = true
                            }
                        }
                    )
                }

                if(MenuCapas) {
                    MenuListaEditable(
                        onDismiss = {
                            MenuCapas = false
                            if (!CrearClases) {
                                when (viewModel.origenMenuSecundario) {
                                    OrigenMenuSecundario.AJUSTES -> MostrarAjustes = true
                                    else -> viewModel.mostrarDialogo = true
                                }
                            }
                        }
                    )
                }

                if (MenuClases) {
                    MenuClases(
                        onDismiss = {
                            MenuClases = false
                            when (viewModel.origenMenuSecundario) {
                                OrigenMenuSecundario.AJUSTES -> MostrarAjustes = true
                                else -> viewModel.mostrarDialogo = true
                            }
                        },
                        onCrear = { MenuClases = false; CrearClases = true },
                        mapaGlobal = mapaGlobal
                    )
                }

                if(MostrarAjustes)
                {
                    MenuAjustes(
                        onDismiss = { MostrarAjustes = false },
                        onEditarCapas = {
                            viewModel.origenMenuSecundario = OrigenMenuSecundario.AJUSTES
                            MostrarAjustes = false
                            MenuCapas = true
                        },
                        onEditarPlantillas = {
                            viewModel.origenMenuSecundario = OrigenMenuSecundario.AJUSTES
                            MostrarAjustes = false
                            MenuClases = true
                        },
                        onImportarActivado = {
                            (context as? ComponentActivity)?.lifecycleScope?.launch { escanearGaleriaExif() }
                        }
                    )
                }
    
                if(CrearClases)
                {
                    CrearClase(
                        listaCualidades = viewModel.DatosProxClase.ListaCualidades,
                        onDismiss = { CrearClases = false; MenuClases = true },
                        onConfirm = { nombre, estilo, texto, cualidades, CheckLists ->
    
                                val clas = Plantilla(
                                    ID = (if(viewModel.DatosProxClase.EditandoClase!=null) viewModel.DatosProxClase.EditandoClase else UUID.randomUUID().toString())!!,
                                    Nombre = nombre,
                                    Texto = texto,
                                    Estilo = estilo,
                                    Cualidades = cualidades.toList(),
                                    ToDos = CheckLists,
                                    Capa = viewModel.DatosProxClase.Capa
                                )
    
                                viewModel.agregarCLase(context,clas)
                                viewModel.DatosProxClase = MiPlantilla()
                                viewModel.DatosProxClase.EditandoClase=null
                                CrearClases = false

                                MenuClases = true
    
    
                        },
                        onElegirMarker = {
                            ElegirEstiloClase = true
                        },
                        onElegirWidget = {
                            MenuWidgets = true
                        },
                        OnEditChecklist = {
                            MenuCheckList = true
                        },
                        OnEditCapas = {
                            MenuCapas = true
                        }
                    )
                }

            }
    
    
        }
    
    
    
    
        @Composable
        fun MenuClases(
            onDismiss: () -> Unit,
            onCrear: () -> Unit,
            mapaGlobal: MapLibreMap?
    
        )
        {
            Dialog(onDismissRequest = { onDismiss() }) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                ) {
    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Selector de plantillas",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
    
                        HorizontalDivider(
                            color = Color.Black,
                            thickness = 1.dp
                        )
    
                        // Lista scrolleable
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            items(viewModel.ListaClases.values.toList()) { clase ->
    
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row() {
    
                                            Image(
                                                painter = painterResource(id = R.drawable.editar),
                                                contentDescription = "Editar",
                                                modifier = Modifier
                                                    .padding(end = 5.dp)
                                                    .size(15.dp)
                                                    .clickable {
                                                        viewModel.DatosProxClase.EditandoClase =
                                                            clase.ID

                                                        viewModel.DatosProxClase = MiPlantilla(

                                                            Nombre = clase.Nombre,
                                                            Descripcion = clase.Texto,
                                                            EditandoClase = clase.ID,
                                                            EstiloInicial = clase.Estilo.ifEmpty { null },
                                                            ListaCualidades = clase.Cualidades,
                                                            ListaChecklist = clase.ToDos,
                                                        )

                                                        val (colorDecodificado, iconoDecodificado) = decodificarEstilo(
                                                            clase.Estilo
                                                        )
                                                        viewModel.DatosProxClase.ColorEstilo =
                                                            colorDecodificado
                                                        viewModel.DatosProxClase.EstiloIcono =
                                                            iconoDecodificado
                                                        viewModel.DatosProxClase.Capa = clase.Capa


                                                        onCrear()
                                                    }
                                            )
                                            Text(
                                                text = clase.Nombre,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                    if (viewModel.origenMenuSecundario == OrigenMenuSecundario.MARCADOR) {
                                        Image(
                                            painter = painterResource(id = R.drawable.elegir_menu),
                                            contentDescription = "agregar",
                                            modifier = Modifier
                                                .padding(start = 4.dp)
                                                .size(25.dp)
                                                .clickable {
                                                    aplicarPlantilla(
                                                        clase,
                                                        viewModel,
                                                        mapaGlobal
                                                    )
                                                    onDismiss()
                                                }
                                        )
                                    }


                                }
    
                                // Línea divisoria
                                HorizontalDivider(
                                    color = Color.LightGray,
                                    thickness = 1.dp
                                )
                            }
                            item {
                                Button(
                                    onClick = { onCrear()},
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text("Añadir")
                                }
                            }
    
    
                            }
    
    
                        // Cerrar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { onDismiss() },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Continuar")
                            }
    
                        }
    
                    }
                }
            }
        }
    
        @Composable
        fun MenuCheckList(
            esClase: Boolean,
            onDismiss: () -> Unit
        )
        {
            var listaMutable = remember { mutableStateListOf<ToDo>() }
    
            var textoEditado by remember {mutableStateOf("")}
    
            var editando by remember {mutableStateOf(-1)}





            fun Guardar() {
                if (esClase) {
                    viewModel.DatosProxClase.ListaChecklist = listaMutable.toList()
                } else {
                    viewModel.DatosProxPin.ListaChecklist = listaMutable.toList()
                }
            }

            fun Cargar() {
                val listaActual = if (esClase) viewModel.DatosProxClase.ListaChecklist else viewModel.DatosProxPin.ListaChecklist
                if(listaActual.isNotEmpty()){
                    listaMutable.addAll(listaActual)
                }
            }


            LaunchedEffect(Unit) {
                Cargar()
            }
    
            Dialog(onDismissRequest = { onDismiss() }) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                ) {
    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Título
                        Text(
                            text = "Objetivos",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                        )
    
                        HorizontalDivider(
                            color = Color.Black,
                            thickness = 1.dp
                        )
    
                        // Lista Scrolleable
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            itemsIndexed(listaMutable) { indice, Objetivo ->
    
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .height(34.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
    
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row() {
    
                                            if(editando == indice)
                                            {
    
                                                BasicTextField(
                                                    value = textoEditado,
                                                    onValueChange = { textoEditado = it },
                                                    keyboardActions = KeyboardActions(
                                                        onDone = {
                                                            val textoFinal = textoEditado.trim()
    
                                                            if (textoFinal.isEmpty()) {
                                                                listaMutable.removeAt(indice)
                                                            } else {
                                                                listaMutable.set(indice, ToDo(textoFinal,Objetivo.completada))
                                                            }
    
                                                            textoEditado = ""
                                                            editando = -1
                                                        }
                                                    ),
                                                    singleLine = true,
                                                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(end = 2.dp)
                                                        .height(30.dp)

                                                        .border(
                                                            width = 1.dp,
                                                            color = MaterialTheme.colorScheme.outline,
                                                            shape = RoundedCornerShape(4.dp)
                                                        ),
                                                    decorationBox = { innerTextField ->
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.padding(horizontal = 8.dp)
                                                        ) {
                                                            innerTextField()
                                                        }
                                                    }
                                                )
                                                if(textoEditado.length>0)
                                                {
                                                    Image(
                                                        painter = painterResource(id = R.drawable.check),
                                                        contentDescription = "Aceptar",
                                                        modifier = Modifier
                                                            .size(22.dp)
                                                            .clickable {
                                                                listaMutable.set(
                                                                    editando,
                                                                    ToDo(
                                                                        textoEditado,
                                                                        listaMutable.get(editando).completada
                                                                    )
                                                                )
                                                                textoEditado = ""
                                                                editando = -1
                                                            }
                                                    )
                                                }
                                                else
                                                {
                                                    Image(
                                                        painter = painterResource(id = R.drawable.quitar_menu),
                                                        contentDescription = "Quitar",
                                                        modifier = Modifier
                                                            .size(22.dp)
                                                            .clickable {
                                                                listaMutable.removeAt(indice)


                                                                textoEditado = ""
                                                                editando = -1
                                                            }
                                                    )
                                                }
    
                                            }
    
    
                                            else
                                            {
                                                Text(
                                                    text = Objetivo.texto,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Color.DarkGray,
                                                    modifier = Modifier.weight(1f)
                                                )
    
    
                                                Image(
                                                    painter = painterResource(id = R.drawable.editar),
                                                    contentDescription = "Editar",
                                                    modifier = Modifier
                                                        .padding(end = 5.dp)
                                                        .size(15.dp)
                                                        .clickable {
                                                            editando = indice
                                                            textoEditado = Objetivo.texto
                                                        }
                                                )
                                            }
                                        }
                                }
    
                                // Línea divisoria
                                HorizontalDivider(
                                    color = Color.LightGray,
                                    thickness = 1.dp
                                )
                            }
                            item {
                                Button(
                                    onClick = {
    
                                                if(editando >= 0)
                                                {
                                                    Log.d("TAG", "intentado " + editando.toString() + " caben " + listaMutable.size.toString())
                                                    val textoFinal = textoEditado.trim()
    
                                                    if (textoFinal.isEmpty()) {
                                                        listaMutable.removeAt(editando)
                                                    } else {
                                                        listaMutable.set(editando, ToDo(textoEditado,listaMutable.get(editando).completada))
                                                    }
    
    
                                                    textoEditado = ""
                                                    editando = -1
                                                }
                                              listaMutable.add(ToDo("",false))
                                              editando = listaMutable.size-1},
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text("Añadir")
                                }
                            }
    
    
                        }
    
    
                        // Cerrar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    Guardar()
    
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Continuar")
                            }
    
                        }
    
                    }
                }
            }
        }
    
        @Composable
        fun MenuListaEditable(
            onDismiss: () -> Unit
        )
        {
            val context = LocalContext.current

            var listaMutable = remember { mutableStateListOf<String>() }
    
            var textoEditado by remember { mutableStateOf("")}
    
            var editando by remember { mutableStateOf(-1)}
    
    
    
    
    
            fun Guardar() {
                viewModel.Capas = listaMutable
                viewModel.guardarCapas(context)
    
    
            }

            fun Cargar() {
                listaMutable.addAll(viewModel.Capas)
            }

            LaunchedEffect(Unit) {
                Cargar()
            }
    
            Dialog(onDismissRequest = { onDismiss() }) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                ) {
    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Título
                        Text(
                            text = "Objetivos",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                        )
    
                        HorizontalDivider(
                            color = Color.Black,
                            thickness = 1.dp
                        )
    
                        // Lista Scrolleable
                        LazyColumn(
                            modifier = Modifier.weight(1f)
                        ) {
                            itemsIndexed(listaMutable) { indice, nombre ->
    
    
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .height(34.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row() {
    
                                        if(editando == indice)
                                        {
    
                                            BasicTextField(
                                                value = textoEditado,
                                                onValueChange = { textoEditado = it },
                                                singleLine = true,
                                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = 2.dp)
                                                    .height(30.dp)

                                                    .border(
                                                        width = 1.dp,
                                                        color = MaterialTheme.colorScheme.outline,
                                                        shape = RoundedCornerShape(4.dp)
                                                    ),
                                                decorationBox = { innerTextField ->
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(horizontal = 8.dp)
                                                    ) {
                                                        innerTextField()
                                                    }
                                                }
                                            )
                                            if(textoEditado.length>0)
                                            {
                                                Image(
                                                    painter = painterResource(id = R.drawable.check),
                                                    contentDescription = "Aceptar",
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .clickable {
                                                            val capaAEditar = listaMutable[indice]

                                                            viewModel.EditarCapa(
                                                                context,
                                                                capaAEditar,
                                                                textoEditado
                                                            )
                                                            listaMutable.set(editando, textoEditado)
                                                            textoEditado = ""
                                                            editando = -1
                                                        }
                                                )
                                            }
                                            else
                                            {
                                                if(editando > 0)
                                                {
                                                    Image(
                                                        painter = painterResource(id = R.drawable.quitar_menu),
                                                        contentDescription = "Quitar",
                                                        modifier = Modifier
                                                            .size(22.dp)
                                                            .clickable {
                                                                val capaABorrar =
                                                                    listaMutable[indice]

                                                                viewModel.EliminarCapa(
                                                                    context,
                                                                    capaABorrar
                                                                )
                                                                listaMutable.removeAt(indice)

                                                                textoEditado = ""
                                                                editando = -1
                                                            }
                                                    )
                                                }
                                            }
    
                                        }
    
    
                                        else
                                        {
                                            Text(
                                                text = nombre,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.DarkGray,
                                                modifier = Modifier.weight(1f)
                                            )
    
    
                                            Image(
                                                painter = painterResource(id = R.drawable.editar),
                                                contentDescription = "Editar",
                                                modifier = Modifier
                                                    .padding(end = 5.dp)
                                                    .size(15.dp)
                                                    .clickable {
                                                        editando = indice
                                                        textoEditado = nombre
                                                    }
                                            )
                                        }
                                    }
                                }
    
                                // Línea divisoria
                                HorizontalDivider(
                                    color = Color.LightGray,
                                    thickness = 1.dp
                                )
                            }
                            item {
                                Button(
                                    onClick = {
    
                                        if(editando >= 0)
                                        {
                                            Log.d("TAG", "intentado " + editando.toString() + " caben " + listaMutable.size.toString())
                                            val textoFinal = textoEditado.trim()
    
                                            if (textoFinal.isEmpty()) {
                                                listaMutable.removeAt(editando)
                                            } else {
                                                listaMutable.set(editando, textoEditado)
                                            }
    
    
                                            textoEditado = ""
                                            editando = -1
                                        }
                                        listaMutable.add("")
                                        editando = listaMutable.size-1},
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text("Añadir")
                                }
                            }
    
    
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    Guardar()
    
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Continuar")
                            }
    
                        }
    
                    }
                }
            }
        }
    
    
        @Composable
        fun MenuWidgets(
            esClase: Boolean,
            onDismiss: () -> Unit
        )
        {
    
            var Widgets = listOf<Cualidad>(Cualidad("Puntuacion", "Sistema de puntuación, de 0 a 5 estrellas", R.drawable.estrella_llena), Cualidad("CheckList", "Lista con una serie de tareas y su completación", R.drawable.checklist), Cualidad("Clima", "Clima en tiempo real en la posición del marcador", R.drawable.termometro),Cualidad("Contador", "Registra cuantas veces pasas por una ubicación", R.drawable.visitas))
    
            var Seleccionados = remember { mutableStateListOf<String>() }
    
    
    
            LaunchedEffect(Unit) {
                val listaActual = if (esClase) viewModel.DatosProxClase.ListaCualidades else viewModel.DatosProxPin.ListaCualidades
                if(listaActual.size>0){
                    Seleccionados.addAll(listaActual)
                }
            }
    
    
            Dialog(onDismissRequest = { onDismiss() }) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                ) {
    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Título
                        Text(
                            text = "Tipos de Widgets",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
    
                        HorizontalDivider(
                            color = Color.Black,
                            thickness = 1.dp
                        )
    
                        // Lista Scrolleable
    
    
                        LazyColumn(modifier = Modifier.weight(1f)) {
    
    
                            items(Widgets) { widget ->
                                Row(
    
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (Seleccionados.contains(widget.Nombre)) Color(
                                                0xFFE0F7FA
                                            ) else Color.Transparent
                                        )
                                        .clickable {
                                            val nombre = widget.Nombre
                                            if (Seleccionados.contains(nombre)) {
                                                Seleccionados.remove(nombre)
                                            } else {
                                                Seleccionados.add(nombre)
                                            }
                                        }
                                        .padding(vertical = 4.dp),
    
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row() {
    
                                            Text(
                                                text = widget.Nombre,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
    
                                        Text(
                                            text = widget.Descripcion,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.DarkGray
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
    
                                    Image(
                                        painter = painterResource(id = widget.Imagen),
                                        contentDescription = "agregar",
                                        modifier = Modifier
                                            .padding(start = 1.dp)
                                            .size(30.dp)
                                            .clickable {

                                            }
                                    )
    
                                    /*if(viewModel.DatosProxPin.IDClase != clase.ID)
                                    {
                                        Image(
                                            painter = painterResource(id = R.drawable.elegir_menu),
                                            contentDescription = "agregar",
                                            modifier = Modifier
                                                .padding( start = 4.dp)
                                                .size(25.dp)
                                                .clickable {
    
                                                }
                                        )
                                    }
                                    else
                                    {
                                        Image(
                                            painter = painterResource(id = R.drawable.agregar_menu),
                                            contentDescription = "quitar",
                                            modifier = Modifier
                                                .padding( start = 4.dp)
                                                .size(25.dp)
                                                .clickable {
    
                                                }
                                        )
                                    }*/
    
    
    
    
    
    
                                }

                                HorizontalDivider(
                                    color = Color.LightGray,
                                    thickness = 1.dp
                                )
                            }
    
    
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    if (esClase) {
                                        viewModel.DatosProxClase.ListaCualidades = Seleccionados.toList()
                                    } else {
                                        viewModel.DatosProxPin.ListaCualidades = Seleccionados.toList()
                                    }
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Continuar")
                            }
    
                        }
    
                    }
                }
            }
        }
    
    
    
        @Composable
        fun MenuAjustes(
            onDismiss: () -> Unit,
            onEditarCapas: () -> Unit,
            onEditarPlantillas: () -> Unit,
            onImportarActivado: () -> Unit
        )
        {
            val context = LocalContext.current
            var importarActivado by remember { mutableStateOf(viewModel.ImportarFotosActivado) }

            val permisoImagenes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            val launcherPermisoImagenes = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { concedido ->
                if (concedido) {
                    importarActivado = true
                    viewModel.SetAjuste(context, activado = true)
                    onImportarActivado()
                }
            }
    
            Dialog(onDismissRequest = { onDismiss() }) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                ) {
    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Ajustes",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
    
                        HorizontalDivider(
                            color = Color.Black,
                            thickness = 1.dp
                        )
    
    
    
                        val ImagenesBotones = listOf(R.drawable.icono_estilo1,R.drawable.icono_estilo2, R.drawable.icono_estilo3)
                        var EstiloElegido by remember {mutableStateOf(viewModel.EstiloSelecc)}
                        var PrecisionElegida by remember {mutableStateOf(viewModel.PrecisionSelecc)}



                        Text(
                            text = "Estilo de mapa",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 10.dp, start = 30.dp)
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(1.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 10.dp)
                        ) {
                            itemsIndexed(ImagenesBotones) { index, imagen ->
    
                                CeldaEstiloMapa(
                                    ImagenRes = imagen,
                                    seleccionado = EstiloElegido == index,
                                    onClick = { EstiloElegido = index }
                                )
    
                            }
                        }




                        Text(
                            text = "Precisión del tracking",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 10.dp, start = 30.dp)
                        )

                        val etiquetasPrecision = listOf("Apagado","Ahorro", "Equilibrado", "Alta")

                        val coloresSeleccionados = listOf(
                            Color(0xFFf5bac0),  //Rojo
                            Color(0xFFE8F5E9), // Verde
                            Color(0xFFE3F2FD), // Azul
                            Color(0xFFFFF3E0)  // Naranja
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            etiquetasPrecision.forEachIndexed { index, etiqueta ->

                                val seleccionado = PrecisionElegida == index

                                Button(
                                    onClick = { PrecisionElegida = index },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (seleccionado)
                                            coloresSeleccionados[index]
                                        else
                                            Color.White,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(
                                        width = 1.5.dp,
                                        color = if(seleccionado) Color.Black else Color(210, 210, 210)
                                    ),
                                    contentPadding = PaddingValues(0.dp),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 0.dp,
                                        pressedElevation = 0.dp
                                    )
                                ) {
                                    Text(
                                        text = etiqueta,
                                        fontSize = 12.sp,
                                        fontWeight = if (seleccionado)
                                            FontWeight.Bold
                                        else
                                            FontWeight.Normal
                                    )
                                }
                            }
                        }



                        Text(
                            text = "Gestión",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 10.dp, start = 30.dp)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = onEditarCapas,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFfcf5e3),
                                    contentColor = Color(0xFF3A3A3A)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, Color(210, 210, 210)),
                                contentPadding = PaddingValues(0.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp
                                )
                            ) {
                                Text(
                                    text = "Editar capas",
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = onEditarPlantillas,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFfcf5e3),
                                    contentColor = Color(0xFF3A3A3A)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, Color(210, 210, 210)),
                                contentPadding = PaddingValues(0.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 0.dp,
                                    pressedElevation = 0.dp
                                )
                            ) {
                                Text(
                                    text = "Editar plantillas",
                                    fontSize = 12.sp
                                )
                            }
                        }



                        Text(
                            text = "Importar fotos con ubicación",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp, start = 30.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Crea marcadores automáticamente a partir de fotos de tu galería con GPS",
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            Switch(
                                checked = importarActivado,
                                onCheckedChange = { activar ->
                                    if (activar) {
                                        val yaConcedido = ContextCompat.checkSelfPermission(
                                            context,
                                            permisoImagenes
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (yaConcedido) {
                                            importarActivado = true
                                            viewModel.SetAjuste(context,  activado =  true)
                                            onImportarActivado()
                                        } else {
                                            launcherPermisoImagenes.launch(permisoImagenes)
                                        }
                                    } else {
                                        importarActivado = false
                                        viewModel.SetAjuste(context,  activado = false)
                                    }
                                }
                            )

                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = {

                                    val trackingEstabaApagado = viewModel.PrecisionSelecc == 0
                                    val trackingSeApaga = PrecisionElegida == 0


                                    viewModel.SetAjuste(context, estilo = EstiloElegido, precision = PrecisionElegida)

                                    if (trackingEstabaApagado && !trackingSeApaga) {
                                        RecibidorActividad(context).iniciarDeteccion()
                                    } else if (!trackingEstabaApagado && trackingSeApaga) {
                                        RecibidorActividad(context).detenerDeteccion()
                                    }

                                    onDismiss()
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Continuar")
                            }
    
                        }
    
                    }
                }
            }
        }
    
    
    
        @Composable
        fun MiMenuEmergente(
            onDismiss: () -> Unit,
            onConfirm: (String, String, String, Uri?, Boolean, List<String>, List<ToDo>) -> Unit,
            onElegirMarker: () -> Unit,
            onElegirClase: () -> Unit,
            onElegirWidget: () -> Unit,
            OnEditChecklist: () -> Unit,
            OnEditCapas: () -> Unit,
    
        ) {
    
            var nombre by remember { mutableStateOf("") }
            var texto by remember { mutableStateOf("") }
            var imagenURL by remember { mutableStateOf<Uri?>(null) }
            var estado by remember {
                mutableStateOf(true)
            }
    
    
            val context = LocalContext.current
    
            fun Guardar() {
                viewModel.DatosProxPin.Texto = texto
                viewModel.DatosProxPin.Titulo = nombre
                viewModel.DatosProxPin.UriImagen = imagenURL
                viewModel.DatosProxPin.Estado = estado
            }
    
            fun Cargar() {
                viewModel.DatosProxPin.Texto?.let { textoP ->
                    texto = textoP
                }
    
                viewModel.DatosProxPin.Titulo?.let { tituloP ->
                    nombre = tituloP
                }
    
                viewModel.DatosProxPin.UriImagen?.let { UriP ->
                    imagenURL = UriP
                }

                viewModel.DatosProxPin.Estado?.let { est ->
                    estado = est
                }
    
            }
    
            LaunchedEffect(Unit) {
                Cargar()
            }
    
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
    
                uri?.let {
                    // Para que sobrevivan los permisos
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    imagenURL = it
                }
    
            }
    
            if (viewModel.DatosProxPin.UriImagen != null) {
                imagenURL = viewModel.DatosProxPin.UriImagen
            }
    
    
    
            Dialog(onDismissRequest = { onDismiss() }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Titulo
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Crear marcador",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
    
                            )
    
                            TextButton(onClick = {
                                Guardar()
                                onElegirClase() },
                                modifier = Modifier.height(34.dp))
                            {
                                Text(text = "Plantillas", fontWeight = FontWeight.Bold, fontSize = 12.sp, textDecoration = TextDecoration.Underline)
                            }

                            if(viewModel.DatosProxPin.Capa.length==0)
                            {
                                viewModel.DatosProxPin.Capa = viewModel.Capas.get(0)
                            }
    
                            SelectorListaGlobal(
                                listaOpciones = viewModel.Capas,
                                capaSeleccionada = viewModel.DatosProxPin.Capa,
                                onSeleccionar = { viewModel.DatosProxPin.Capa = it },
                                onGuardar = {
                                    Guardar()
                                    OnEditCapas() }
                            )
    
    
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    Guardar()
                                    onElegirMarker()
                                },
                                modifier = Modifier
                                    .size(55.dp)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                val estiloActual = viewModel.DatosProxPin.EstiloPredeterminado
                                if (estiloActual != null) {

                                    //Log.d("TAG", estiloActual)

                                    val idIconoPreview = iconos_pin[viewModel.DatosProxPin.EstiloIcono]
                                    val bitmapPreview = remember(viewModel.DatosProxPin.ColorEstilo, viewModel.DatosProxPin.EstiloIcono) {
                                        generarPinMapaMapbox(context, viewModel.DatosProxPin.ColorEstilo.toArgb(), idIconoPreview)
                                    }
                                    Image(
                                        bitmap = bitmapPreview.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
    
                            Spacer(modifier = Modifier.width(8.dp))
    
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre del pin") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
    
                        Spacer(modifier = Modifier.height(12.dp))
    
                        // Imagen
                        if (imagenURL != null) {
                            AsyncImage(
                                model = imagenURL,
                                contentDescription = "",
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                    .aspectRatio(4f/3f),
    
                                contentScale = ContentScale.Crop
                            )
                        }


                        OutlinedTextField(
                            value = texto,
                            onValueChange = { texto = it },
                            label = { Text("Descripción") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
    
                        Spacer(modifier = Modifier.height(8.dp))
    
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
    
                            if(viewModel.EditandoPin==null){
    
                                Checkbox(
                                    checked = estado,
                                    onCheckedChange = { estado = !estado }
                                )
                                Text(
                                    text = "¿Has estado?"
                                )
                            }
                            else{
                                estado = false
                            }
    
                        }
    
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            item{
                                Button(
                                    onClick = {
                                        Guardar()
                                        onElegirWidget() },
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(8.dp))
    
    
                                    {
                                        Text("Widgets+")
                                    }
                            }
                            items(viewModel.DatosProxPin.ListaCualidades) { nombre ->
                                Button(onClick = { },
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(8.dp)) {
                                    if(nombre=="CheckList")
                                    {
                                        Image(
                                            painter = painterResource(id = R.drawable.editar),
                                            contentDescription = "Editar",
                                            modifier = Modifier
                                                .padding(end = 5.dp)
                                                .size(18.dp)
                                                .clickable {
                                                    Guardar()
                                                    OnEditChecklist()
                                                }
                                        )
                                    }
    
                                    Text(nombre)
                                }
                            }
                        }
    
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    if (imagenURL == null) {
                                        launcher.launch(arrayOf("image/*"))
                                    } else {
                                        imagenURL = null
                                    }
                                },
                                modifier = Modifier.size(45.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF5F5F5),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    text = if (imagenURL==null) "📷" else "X",
                                    fontWeight = FontWeight.Bold
                                )
                            }


                            Spacer(modifier = Modifier.weight(1f))
    
                            TextButton(onClick = {
                                viewModel.EditandoPin=null
                                viewModel.DatosProxPin = MiObjeto()
                                onDismiss() }) {
                                Text("Cancelar")
                            }
    
                            Spacer(modifier = Modifier.width(8.dp))
    
                            Button(
                                onClick = {
                                    onConfirm(
                                        nombre,
                                        viewModel.DatosProxPin.EstiloPredeterminado ?: "marcador1",
                                        texto,
                                        imagenURL,
                                        estado,
                                        viewModel.DatosProxPin.ListaCualidades,
                                        viewModel.DatosProxPin.ListaChecklist
                                    )
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    
        @Composable
        fun SelectorListaGlobal(
            listaOpciones: List<String>,
            capaSeleccionada: String,
            onSeleccionar: (String) -> Unit,
            onGuardar: () -> Unit
        ) {
            var expandido by remember { mutableStateOf(false) }
    
            Box {
                TextButton(
                    onClick = { expandido = true },
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = capaSeleccionada.ifEmpty { "Capa" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
    
                DropdownMenu(
                    expanded = expandido,
                    onDismissRequest = { expandido = false }
                ) {
                        DropdownMenuItem(   // Para agregar/cambiar nombre de capas
                            text = { Text("Gestionar...") },
                            onClick = {
                                expandido = false
                                onGuardar()
                            }
                        )
    
                        listaOpciones.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    onSeleccionar(opcion)
                                    expandido = false
                                }
                            )
    
                        }
                }
            }
        }
    
        @Composable
        fun CrearClase(
            listaCualidades: List<String>,
            onDismiss: () -> Unit,
            onConfirm: (String, String, String, List<String>, List<ToDo>) -> Unit,
            onElegirMarker: () -> Unit,
            onElegirWidget: () -> Unit,
            OnEditChecklist: () -> Unit,
            OnEditCapas: () -> Unit,
    
            ) {
    
            var nombre by remember { mutableStateOf("") }
            var texto by remember { mutableStateOf("") }
    
    
            val context = LocalContext.current
    
            fun Guardar() {
                viewModel.DatosProxClase.Nombre = nombre
                viewModel.DatosProxClase.Descripcion = texto
            }
    
            fun Cargar() {
                nombre = viewModel.DatosProxClase.Nombre
    
                texto = viewModel.DatosProxClase.Descripcion
    
            }
    
            LaunchedEffect(Unit) {
                Cargar()
            }
    
            Dialog(onDismissRequest = { onDismiss() }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Crear nueva plantilla",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
    
                            )
    
    
                            SelectorListaGlobal(
                                listaOpciones = viewModel.Capas,
                                capaSeleccionada = viewModel.DatosProxClase.Capa,
                                onSeleccionar = { viewModel.DatosProxClase.Capa = it },
                                onGuardar = {
                                    Guardar()
                                    OnEditCapas() }
                            )
    
                            if(viewModel.DatosProxClase.EditandoClase!=null && viewModel.DatosProxClase.EditandoClase!="primero")    //Boton eliminar
                            {
                                Button(
                                    onClick = { viewModel.ListaClases.get(viewModel.DatosProxClase.EditandoClase)?.let { viewModel.borrarClase(context, it) }
                                        onDismiss()},
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(
                                        horizontal = 14.dp,
                                        vertical = 4.dp
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .height(30.dp)
                                ) {
                                    Text(
                                        text = "Eliminar",
                                        fontSize = 12.sp
                                    )
                                }
                            }
    
    
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    Guardar()
                                    onElegirMarker()
                                },
                                modifier = Modifier
                                    .size(55.dp)
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                val estiloActual = viewModel.DatosProxClase.EstiloPredeterminado
                                if (estiloActual != null) {

                                    val idIconoPreview = iconos_pin[viewModel.DatosProxClase.EstiloIcono] ?: null
                                    val bitmapPreview = remember(viewModel.DatosProxClase.ColorEstilo, viewModel.DatosProxClase.EstiloIcono) {
                                        generarPinMapaMapbox(context, viewModel.DatosProxClase.ColorEstilo.toArgb(), idIconoPreview)
                                    }
                                    Image(
                                        bitmap = bitmapPreview.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
    
                            Spacer(modifier = Modifier.width(8.dp))
    
                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre (obligatorio)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
    
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = texto,
                            onValueChange = { texto = it },
                            label = { Text("Descripción (opcional)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
    
                        Spacer(modifier = Modifier.height(8.dp))
    
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item{
                                Button(
                                    onClick = {
                                        Guardar()
                                        onElegirWidget() },
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(8.dp))
    
    
                                {
                                    Text("Widgets+")
                                }
                            }
                            items(listaCualidades) { nombre ->
                                Button(onClick = { /* No hace nada */ },
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                    shape = RoundedCornerShape(4.dp),
                                    contentPadding = PaddingValues(8.dp)) {
                                    if(nombre=="CheckList")
                                    {
                                        Image(
                                            painter = painterResource(id = R.drawable.editar),
                                            contentDescription = "Editar",
                                            modifier = Modifier
                                                .padding(end = 5.dp)
                                                .size(18.dp)
                                                .clickable {
                                                    Guardar()
                                                    OnEditChecklist()
                                                }
                                        )
                                    }
    
                                    Text(nombre)
                                }
                            }
                        }
    
                        Spacer(modifier = Modifier.height(10.dp))


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Spacer(modifier = Modifier.weight(1f))
    
                            TextButton(onClick = {
                                viewModel.DatosProxClase.EditandoClase=null
                                viewModel.DatosProxClase = MiPlantilla()
                                onDismiss() }) {
                                Text("Cancelar")
                            }
    
                            Spacer(modifier = Modifier.width(8.dp))
    
                            Button(
                                onClick = {
                                    onConfirm(
                                        nombre,
                                        viewModel.DatosProxClase.EstiloPredeterminado ?: "",
                                        texto,
                                        viewModel.DatosProxClase.ListaCualidades,
                                        viewModel.DatosProxClase.ListaChecklist
                                    )
                                },
                                enabled = nombre.length>0,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    
    
    
        @Composable
        fun MenuEstiloMarker(
            colorSeleccionado: Color,
            iconoSeleccionado: String,
            onColorChange: (Color) -> Unit,
            onIconoChange: (String) -> Unit,
    
            onDismiss: () -> Unit,
            onAccept: () -> Unit
        )
        {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 245.dp)
                    ) {
                        Text(
                            text = "Estilo Marcador",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 10.dp)
    
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            contentPadding = PaddingValues(1.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(iconos_pin.toList()) { (nombreEstilo, drawableId) ->
    
                                CeldaEstilo(
                                    ImagenRes = drawableId,
                                    seleccionado = iconoSeleccionado == nombreEstilo,
                                    onClick = { onIconoChange(nombreEstilo) }
                                )
    
                            }
                        }
    
                        LazyRow(
                            contentPadding = PaddingValues(1.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                6.dp,
                                Alignment.CenterHorizontally
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 260.dp),
    
                        ) {
                            items(listaColores) { valor ->
    
                                CeldaColor(
                                    colorCelda = valor,
                                    seleccionado = colorSeleccionado == valor,
                                    onClick = { onColorChange(valor) }
                                )
    
    
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onAccept()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Continuar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onDismiss() }
                    ) {
                        Text("Cancelar")
                    }
                },
                containerColor = Color.White,
    
                )
        }
    
    
    
        @Composable
        fun CeldaEstiloMapa(
            ImagenRes: Int? = null,
            seleccionado: Boolean,
            onClick: () -> Unit
        )
        {
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(
                        width = 2.dp,
                        color = if (seleccionado) Color(80, 80, 80) else Color(210, 210, 210),
                        shape = RoundedCornerShape(8.dp)
                    ),
    
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
    
    
            )
            {
                if (ImagenRes != null) {
                    Image(
                        painter = painterResource(id = ImagenRes),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
    
            }
        }
    
    
        @Composable
        fun CeldaEstilo(
            ImagenRes: Int? = null,
            seleccionado: Boolean,
            onClick: () -> Unit
        )
        {
            Button(
                onClick = onClick,
                modifier = Modifier

                    .border(
                        width = 2.dp,
                        color = if (seleccionado) Color(80, 80, 80) else Color(210, 210, 210),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .size(42.dp),
    
    
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(100, 100, 100)
                ),
    
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
    
    
            )
            {
                if (ImagenRes != null) {
                    Image(
                        painter = painterResource(id = ImagenRes),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
    
            }
        }
    
        @Composable
        fun CeldaColor(
            colorCelda: Color,
            seleccionado: Boolean,
            onClick: () -> Unit
        )
        {
    
            Button(
                onClick = onClick,
                modifier = Modifier

                    .border(
                        width = 2.dp,

                        color = if (seleccionado) Color(80, 80, 80) else Color(210, 210, 210),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .size(31.dp),
    
    
                colors = ButtonDefaults.buttonColors(
                    containerColor =  colorCelda,
                ),
    
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
    
    
            )
            {
            }
        }
    
        private fun generarPinMapaMapbox(context: Context, colorInt: Int, idIconoPng: Int?): Bitmap //crea el Bitmap
        {
            val layerDrawable = ContextCompat.getDrawable(context, R.drawable.pin_dinamico) as android.graphics.drawable.LayerDrawable
            layerDrawable.mutate()

            val width = layerDrawable.intrinsicWidth.takeIf { it > 0 } ?: 100
            val height = layerDrawable.intrinsicHeight.takeIf { it > 0 } ?: 100

            // Pintar el fondo
            val capaColor = layerDrawable.findDrawableByLayerId(R.id.capa_color)
            capaColor.setTint(colorInt)

            // Poner el icono
            val nuevoIcono = ContextCompat.getDrawable(context, idIconoPng ?: R.drawable.icono_nada)
            layerDrawable.setDrawableByLayerId(R.id.capa_icono, nuevoIcono)

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            layerDrawable.setBounds(0, 0, width, height)
            layerDrawable.draw(canvas)

            return bitmap
        }
    }
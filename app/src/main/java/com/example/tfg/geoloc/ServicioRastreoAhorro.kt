package com.example.tfg.geoloc

import android.Manifest
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.tfg.R
import com.example.tfg.datos.AppDatabase
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.tfg.datos.PuntoRutaEntity
import kotlinx.coroutines.flow.first


const val EXTRA_TIPO_ACTIVIDAD = "tipo_actividad"

class ServicioRastreoAhorro : Service() {

    val TIEMPO_MAX_SIN_MOVIMIENTO_MS = 6 * 60 * 1000L
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback //https://developer.android.com/develop/sensors-and-location/location/request-updates?hl=es-419
    val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    var tipoActividadActual = DetectedActivity.WALKING
    var watchdogJob: Job? = null
    var enModoAhorro = false

    private var precisionActual = 2


    fun MultiplicadorPrecision (precisionActual: Int): Float {
        return when (precisionActual) {
            1 -> 0.5f
            2-> 1f
            else -> 1.5f
        }
    }




    private fun reiniciarWatchdog() {
        watchdogJob?.cancel()
        watchdogJob = serviceScope.launch {
            delay(TIEMPO_MAX_SIN_MOVIMIENTO_MS)
            entrarEnModoAhorro()
        }
    }

    private fun activarSeguimientoActivo(tipo: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            val multiplicador = MultiplicadorPrecision(precisionActual)



                enModoAhorro = false
            fusedLocationClient.removeLocationUpdates(locationCallback)

            val (segundosMin, distanciaMinM) = when (tipo) {       //Construimos el LocationRequest personalizado para el tipo de actividad
                DetectedActivity.IN_VEHICLE -> 20L to 100f         //Milisegundos y distancia minimos
                DetectedActivity.ON_BICYCLE -> 26L to 64f
                DetectedActivity.RUNNING -> 32L to 46f
                else -> 45L to 32f  //Andando
            }
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, (segundosMin * 1000L /multiplicador).toLong()) //https://developer.android.com/develop/sensors-and-location/location/change-location-settings?hl=es-419
                .setMinUpdateIntervalMillis((segundosMin * 500L /multiplicador).toLong())
                .setMinUpdateDistanceMeters(distanciaMinM / multiplicador)
                .build()

            fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper())  //https://developer.android.com/develop/sensors-and-location/location/request-updates?hl=es-419
            reiniciarWatchdog()
        }
        else
        {
            stopSelf()
        }
    }

    private fun entrarEnModoAhorro() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        enModoAhorro = true
        fusedLocationClient.removeLocationUpdates(locationCallback)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, (3 * 60 * 1000L ))    //Menos precision y cada menos tiempo
            .setMinUpdateIntervalMillis((60* 1000L ))
            .setMinUpdateDistanceMeters(20f )
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        else
        {
            stopSelf()
        }
    }

    override fun onCreate() {       //Solo la primera vez
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)     //fusiona diferentes fuentes de ubicación para intentar dar una buena posición equilibrando precisión, velocidad y consumo


        serviceScope.launch {   //Obtenemos precision
            AppDatabase.getDatabase(applicationContext).mapDao().obtenerAjustes().collect { ajustes ->
                val nuevaPrecision = ajustes?.Precision ?: 1

                if (nuevaPrecision == 0) {
                    stopSelf()
                }
                else{
                    if (nuevaPrecision != precisionActual) {
                        precisionActual = nuevaPrecision
                        if (!enModoAhorro) {
                            activarSeguimientoActivo(tipoActividadActual) // reconstruye porque tiene parámetros nuevos
                        }
                        else{
                            entrarEnModoAhorro()
                        }
                    }
                }
            }

        }


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) { //https://developer.android.com/develop/sensors-and-location/location/request-updates?hl=es-419
                locationResult.lastLocation?.let { location ->


                        serviceScope.launch {
                            val dao = AppDatabase.getDatabase(applicationContext).mapDao()
                            dao.insertarPuntoRuta(      //Se guarda posicion en la tabla
                                PuntoRutaEntity(
                                    lat = location.latitude,
                                    lng = location.longitude,
                                    timestampMs = System.currentTimeMillis()
                                )
                            )
                        }
                        if (enModoAhorro) {
                            activarSeguimientoActivo(tipoActividadActual)
                        } else {
                            reiniciarWatchdog()
                        }

                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {   //Después de OnCreate o Cuando EnMovimientoDetectado envia el Intent y inicia el servicio
        tipoActividadActual = intent?.getIntExtra(EXTRA_TIPO_ACTIVIDAD, DetectedActivity.WALKING) ?: DetectedActivity.WALKING




        val notification = NotificationCompat.Builder(this, CHANNEL_ID) //https://developer.android.com/develop/ui/compose/notifications/create-notification?hl=es-419
            .setContentTitle("Explorando el mapa")
            .setContentText("Guardando tu recorrido de forma optimizada...")
            .setSmallIcon(R.drawable.brujula)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1001, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(1001, notification)
        }


        serviceScope.launch {
            AppDatabase.getDatabase(applicationContext).mapDao().obtenerAjustes().first()?.let {
                if (it.Precision != 0) precisionActual = it.Precision
            }
            activarSeguimientoActivo(tipoActividadActual)
        }



        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        watchdogJob?.cancel()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null //Service lo requiere
}
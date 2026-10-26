package com.example.tfg.geoloc

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.example.tfg.datos.AppDatabase
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.jvm.java


fun createNotificationChannel(context: Context) {       //https://developer.android.com/develop/ui/compose/notifications/create-notification?hl=es-419
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is not in the Support Library.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "CanalAndroid"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {}
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            context.getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
class RecibidorActividad(val context: Context) {


    fun iniciarDeteccion() {

        createNotificationChannel( context)     //https://developer.android.com/develop/sensors-and-location/location/transitions?hl=es-419

        // Creamos la petición
        val transitions = mutableListOf<ActivityTransition>()

        // Empieza a caminar
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        //Correr
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        //Bicicleta
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        //Coche (vehiculo)
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        // Cuando se para (para apagar el GPS)
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )

        val request = ActivityTransitionRequest(transitions)

        val intent = Intent(context, EnmovimientoDetectado::class.java) //Comunicacion con EnMovimientoDetectado

        val myPendingIntent = PendingIntent.getBroadcast(   //https://developer.android.com/agents/skills/security/android-intent-security/SKILL se enviará incluso si no está activa
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Registramos la petición (requiere haber aceptado el permiso ACTIVITY_RECOGNITION)
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        ) {
            ActivityRecognition.getClient(context).requestActivityTransitionUpdates(request, myPendingIntent)   //empieza a detectar estas transiciones
        }
    }

    fun detenerDeteccion() {
        val intent = Intent(context, EnmovimientoDetectado::class.java)
        val myPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            ActivityRecognition.getClient(context).removeActivityTransitionUpdates(myPendingIntent)
        }
        // apaga el servicio de rastreo por si estaba encendido
        val serviceIntent = Intent(context, ServicioRastreoAhorro::class.java)
        context.stopService(serviceIntent)
    }

}


class ArranqueDispositivo : BroadcastReceiver() {       //Codigo al reiniciar el dispositivo, para que no haga falta abrir la aplicacion https://developer.android.com/health-and-fitness/health-services/monitor-background?hl=es-419
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action != Intent.ACTION_BOOT_COMPLETED) return

        // goAsync() le dice a Android "espera, que voy a leer la base de datos en otro hilo antes de cerrar el receiver"
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (AppDatabase.getDatabase(context).mapDao().obtenerAjustes().firstOrNull()?.Precision != 0) { //Apagado
                    RecibidorActividad(context).iniciarDeteccion()
                }
            } finally {
                pendingResult.finish()
            }
        }

    }
}
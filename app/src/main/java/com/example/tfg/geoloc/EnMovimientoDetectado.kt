package com.example.tfg.geoloc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

val CHANNEL_ID: String = "com.chikeandroid.tutsplustalerts.ANDROID"


class EnmovimientoDetectado : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

        val tiposEnMovimiento = setOf(
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE
        )

        if (ActivityTransitionResult.hasResult(intent)) {       //Cómo procesar eventos de transición de actividad https://developer.android.com/develop/sensors-and-location/location/transitions?hl=es-419
            val result = ActivityTransitionResult.extractResult(intent)!!
            for (event in result.transitionEvents) {

                if(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                {
                    val serviceIntent = Intent(context, ServicioRastreoAhorro::class.java)  //Para comunicarse con el ServicioRastreoAhorro


                    if (event.activityType in tiposEnMovimiento) {  //Si entramos en uno de los 4  tipos de mov
                        serviceIntent.putExtra(EXTRA_TIPO_ACTIVIDAD, event.activityType)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent)
                        } else {
                            context.startService(serviceIntent)
                        }
                    } else if (event.activityType == DetectedActivity.STILL) {  //Si nos acabamos de parar
                        context.stopService(serviceIntent)
                    }
                }

            }
        }
    }
}


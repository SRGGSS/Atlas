package com.example.tfg


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.http.GET
import retrofit2.http.Query


object RetrofitInstance {
    private const val BASE_URL = "https://api.open-meteo.com/"

    val api: AdviceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdviceApiService::class.java)
    }
}



data class ClimaCacheado(
    val respuesta: Respuesta,
    val timestamp: Long
)

object GestorCacheClima { //object es singleton en java
    val cache = mutableMapOf<Pair<Double, Double>, ClimaCacheado>() // coordenadas y resultado
    const val TIEMPO_EXPIRACION_MS = 10 * 60 * 1000

    fun obtener(lat: Double, lon: Double): Respuesta? { //Si cacheado y no caducado
        val guardado = cache[Pair(lat, lon)]

        if (guardado != null && (System.currentTimeMillis() - guardado.timestamp) < TIEMPO_EXPIRACION_MS) {
            return guardado.respuesta
        }
        return null
    }

    fun guardar(lat: Double, lon: Double, respuesta: Respuesta) {
        cache[Pair(lat, lon)] = ClimaCacheado(respuesta, System.currentTimeMillis())
    }
}

data class Respuesta(
    val temperature_2m: Double = 0.0,
    val relative_humidity_2m: Int = 0,
    val wind_speed_10m: Double = 0.0,
    val weather_code: Int = -1,
    val time: String = ""
)

data class MiTiempo(    //Estructura de la respuesta
    val current: Respuesta
)

interface AdviceApiService {
    @GET("v1/forecast")
    suspend fun getAdvice(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code"
    ): MiTiempo
}


@Composable
fun ClimaWidget(lat : Double, long: Double) {

    fun obtenerDescripcionClima(codigo: Int): String {       //weather code de open meteo a String
        return when (codigo) {
            0 -> "Despejado"
            1 -> "Casi despejado"
            2 -> "Parcialmente nublado"
            3 -> "Nublado"
            45, 48 -> "Niebla"
            51, 53, 55 -> "Llovizna"
            56, 57 -> "Llovizna helada"
            61 -> "Lluvia leve"
            63 -> "Lluvia"
            65 -> "Lluvia fuerte"
            66, 67 -> "Lluvia helada"
            71 -> "Nieve leve"
            73 -> "Nieve"
            75 -> "Nieve fuerte"
            77 -> "Aguanieve"
            80, 81, 82 -> "Chubascos"
            85, 86 -> "Nevadas"
            95 -> "Tormenta"
            96, 99 -> "Tormenta severa"
            else -> "Desconocido"
        }
    }

    val peticion = remember { mutableStateOf<Respuesta?>(null) }


    LaunchedEffect(Unit) {      //Petición

        val climaGuardado = GestorCacheClima.obtener(lat, long)

        if (climaGuardado != null) {
            peticion.value = climaGuardado
        }else{
            try {
                val response = RetrofitInstance.api.getAdvice(lat, long)


                peticion.value = response.current
                GestorCacheClima.guardar(lat, long, response.current)
            } catch (e: Exception) {}
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        if (peticion.value != null) {
            val clima = peticion.value!!
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.size(width = 24.dp, height = 55.dp)) {
                    val fraccion = (clima.temperature_2m / 35.0).coerceIn(0.0, 1.0)
                    if (fraccion > 0f) {
                        Image(
                            painter = painterResource(R.drawable.termometro_relleno),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(Color.Red),
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithContent {
                                    val drawScope = this
                                    clipRect(top = (size.height * (1f - fraccion.toFloat()))) { //Recorta desde arriba
                                        drawScope.drawContent()
                                    }
                                }
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.termometro_repiciente),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = clima.temperature_2m.toString() + "°C",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.Black
                )

                Spacer(Modifier.width(19.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Viento: ${clima.wind_speed_10m}Km/h",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Humedad: ${clima.relative_humidity_2m}%",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = obtenerDescripcionClima(clima.weather_code),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }
        } else {
            Text(text ="Cargando clima...", fontSize = 11.sp, color = Color.Gray)
        }
    }




}

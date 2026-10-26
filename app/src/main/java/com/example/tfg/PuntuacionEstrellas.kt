package com.example.tfg

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PuntuacionEstrellas(
    puntuacion: Float,                              // 0f..5f, en pasos de 0.5f
    onPuntuacionChange: ((Float) -> Unit),
) {

    Row(horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier
            .padding(top = 8.dp)) {
        for (i in 1..5) {       //Las 5 estrellas
            // Cuánto de ESTA estrella en concreto está rellena
            val fraccion = (puntuacion - (i - 1)).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .then(
                            Modifier.pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val mitadIzquierda = offset.x < size.width / 2f
                                    val nueva = if (mitadIzquierda) i - 0.5f else i.toFloat()
                                    Log.d("TAG", nueva.toString())
                                    onPuntuacionChange(nueva)
                                }
                            }
                    )
            ) {
                // Estrella vacía, siempre visible de fondo
                Image(
                    painter = painterResource(id = R.drawable.estrella_vacia),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

                // Estrella rellena por encima, recortada según "fraccion"
                if (fraccion > 0f) {
                        Image(
                            painter = painterResource(R.drawable.estrella_llena),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithContent {
                                    clipRect(right = size.width * fraccion) {
                                        this@drawWithContent.drawContent()      //https://slack-chats.kotlinlang.org/t/8494856/i-wanted-to-add-shadow-to-a-composable-shadow-should-be-only
                                    }
                                }
                        )

                }
            }
        }
    }
}
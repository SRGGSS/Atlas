package com.example.tfg


import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

data class ToDo(
    val texto: String,
    val completada: Boolean
)

@Composable
fun CheckList(
    ToDos : List<ToDo>,
    onChecked: ((List<ToDo>) -> Unit),

    ) {

    val lista = remember {
        mutableStateListOf<ToDo>().apply {
            addAll(ToDos)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 6.dp)
    ) {
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp
            )
        lista.forEachIndexed { index, elemento ->


            Row(
                modifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    modifier = Modifier.weight(1f),
                    text = elemento.texto,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    textDecoration = if(elemento.completada) TextDecoration.LineThrough else null
                )

                Checkbox(
                    checked = elemento.completada,
                    onCheckedChange = { checked ->
                        onChecked(lista)
                        lista[index] = ToDo(elemento.texto,checked)
                    },
                    modifier = Modifier.height(32.dp)

                    )
            }

            // Línea divisoria negra horizontal
                HorizontalDivider(
                    color = Color.LightGray,
                    thickness = 1.dp
                )

        }
    }
}
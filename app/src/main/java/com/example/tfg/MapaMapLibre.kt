package com.example.tfg

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.RectF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.LocationComponentOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import kotlin.math.roundToInt

// Piezas de MapLibre para Atlas: sustituyen a MapboxMap (Compose), PointAnnotationGroup,
// ViewAnnotation, viewport/FollowPuck y el location puck de Mapbox.

object MapaIds {
    const val FUENTE_NIEBLA = "fog-source"
    const val CAPA_NIEBLA = "fog-layer"
    const val FUENTE_MARCADORES = "marcadores-source"
    const val CAPA_MARCADORES = "capa-marcadores"
}

fun Point.aLatLng() = LatLng(latitude(), longitude())


/** MapView de MapLibre ligado al ciclo de vida de la Activity (MapLibre no tiene composable oficial). */
@Composable
fun rememberMapViewConCicloDeVida(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        val opciones = MapLibreMapOptions.createFromAttributes(context)
            .textureMode(true)          // mapa en TextureView: se mueve en sincronía con Compose
        MapView(context, opciones).apply { onCreate(null) }
    }


    mapView.getMapAsync { mapa ->       //Para quitar el boton de informacion
        mapa.uiSettings.isAttributionEnabled = false
    }


    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle, mapView) {
        // Al registrarse, el observer recibe los eventos atrasados (START, RESUME) automáticamente
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    return mapView
}


/** Marcadores -> GeoJSON. Cada feature lleva el ID del marcador y el nombre de su imagen ("pin_color_icono"). */
fun marcadoresAGeoJson(marcadores: Collection<Marker>): String =
    FeatureCollection.fromFeatures(
        marcadores.map { m ->
            Feature.fromGeometry(m.Localizacion).apply {
                addStringProperty("id", m.ID)
                addStringProperty("estilo", m.Estilo)
            }
        }
    ).toJson()


/**
 * Añade niebla y marcadores al estilo recién cargado.
 * Orden final: mapa base < niebla < marcadores < puck de ubicación.
 * Si el componente de ubicación ya ha metido sus capas (cambio de estilo), se colocan debajo de ellas.
 */
fun anadirCapasAtlas(style: Style, nieblaGeoJson: String, marcadoresGeoJson: String, opacidadNiebla: Float) {
    val capaUbicacion = style.layers.firstOrNull { it.id.contains("location", ignoreCase = true) }?.id

    style.addSource(GeoJsonSource(MapaIds.FUENTE_NIEBLA, nieblaGeoJson))
    val niebla = FillLayer(MapaIds.CAPA_NIEBLA, MapaIds.FUENTE_NIEBLA).withProperties(
        PropertyFactory.fillColor("#2A2A2A"),
        PropertyFactory.fillOpacity(opacidadNiebla)
    )

    style.addSource(GeoJsonSource(MapaIds.FUENTE_MARCADORES, marcadoresGeoJson))
    val marcadores = SymbolLayer(MapaIds.CAPA_MARCADORES, MapaIds.FUENTE_MARCADORES).withProperties(
        PropertyFactory.iconImage(Expression.get("estilo")),   // imagen registrada con style.addImage
        PropertyFactory.iconSize(0.1f),
        PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM)
        // icon-allow-overlap se deja en false (igual que los PointAnnotation): los pines se ocluyen
        // entre sí y por eso sigue teniendo sentido tu lógica de "marcadores cercanos"
    )

    if (capaUbicacion != null) {
        style.addLayerBelow(niebla, capaUbicacion)
        style.addLayerBelow(marcadores, capaUbicacion)   // queda entre la niebla y el puck
    } else {
        style.addLayer(niebla)
        style.addLayer(marcadores)
    }
}


/** Equivalente a location.updateSettings { createDefault2DPuck(withBearing = false); enabled = true }. */
@SuppressLint("MissingPermission")  // solo se llama con el permiso concedido
fun activarUbicacion(context: Context, mapa: MapLibreMap, style: Style) {
    val lc = mapa.locationComponent
    if (!lc.isLocationComponentActivated) {
        val estiloPuck = LocationComponentOptions.builder(context)
            .accuracyAlpha(0f)
            .elevation(0f)
            .minZoomIconScale(1.2f)
            .maxZoomIconScale(1.2f)
            .pulseEnabled(false)
            .build()

        val opciones = LocationComponentActivationOptions.builder(context, style)
            .locationComponentOptions(estiloPuck)
            .useDefaultLocationEngine(true)
            .build()
        lc.activateLocationComponent(opciones)
    }
    lc.isLocationComponentEnabled = true
    lc.renderMode = RenderMode.NORMAL   // punto sin flecha de rumbo
}


/** Equivalente a makeFollowPuckViewportState(zoom 16, pitch 0). */
fun seguirUbicacion(mapa: MapLibreMap, inmediato: Boolean = false) {
    val lc = mapa.locationComponent
    if (!lc.isLocationComponentActivated) return
    lc.setCameraMode(CameraMode.TRACKING, if (inmediato) 0L else 5000L, 16.0, null, 0.0, null)
}


/** ID del marcador dibujado en el punto tocado, o null si no hay ninguno. */
fun idMarcadorEn(mapa: MapLibreMap, punto: LatLng): String? {
    val px = mapa.projection.toScreenLocation(punto)
    val zona = RectF(px.x - 12f, px.y - 12f, px.x + 12f, px.y + 12f)
    return mapa.queryRenderedFeatures(zona, MapaIds.CAPA_MARCADORES)
        .firstOrNull()
        ?.getStringProperty("id")
}


/**
 * Sustituto de ViewAnnotation (anchor TOP): coloca el contenido con su borde superior
 * en la coordenada y centrado en horizontal. versionCamara se lee en la fase de colocación,
 * así que al mover la cámara solo se recoloca, no se recompone.
 */
fun Modifier.anclarEnMapa(mapa: MapLibreMap?, posicion: LatLng, versionCamara: () -> Int): Modifier =
    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        layout(constraints.maxWidth, constraints.maxHeight) {
            versionCamara()
            val px = mapa?.projection?.toScreenLocation(posicion)
            if (px != null) {
                placeable.place(px.x.roundToInt() - placeable.width / 2, px.y.roundToInt())
            }
        }
    }

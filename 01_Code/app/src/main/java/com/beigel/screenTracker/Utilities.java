package com.beigel.screenTracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Statische Utility-Klasse mit erweiterten Methoden für Scroll-Marker
 */
public class Utilities {

    private static final String PREFS_NAME = "ScreentrackrPrefs";

    // Privater Konstruktor verhindert Instanziierung
    Utilities() {
        throw new UnsupportedOperationException("Utilities ist eine statische Hilfsklasse");
    }

    /**
     * Erstellt Marker mit den angegebenen Einstellungen
     */
    public static void createMarker(ArrayList<ImageView> trackingPointList, TrackingValues trackingValues) {
        int markerType = getMarkerDrawable(trackingValues.getMarkerType());
        int markerSize = getMarkerSize(trackingValues.getMarkerSize());
        int markerColor = Color.parseColor(trackingValues.getMarkerColor());

        for (ImageView marker : trackingPointList) {
            marker.setImageResource(markerType);

            // Größe anpassen
            marker.getLayoutParams().height = markerSize;
            marker.getLayoutParams().width = markerSize;

            // Farbe anpassen
            marker.setColorFilter(markerColor);

            // Layout aktualisieren
            marker.requestLayout();
        }
    }

    /**
     * Bestimmt den Drawable-Ressourcen-ID basierend auf dem Marker-Typ
     */
    private static int getMarkerDrawable(TrackingValues.MarkerType markerType) {
        switch (markerType) {
            case PIE:
                return R.drawable.ic_marker_pie;
            case CIRCLE:
                return R.drawable.ic_marker_circle;
            case TRIANGLE:
                return R.drawable.ic_marker_triangle;
            case CROSS:
            default:
                return R.drawable.ic_marker_cross;
        }
    }

    /**
     * Ermittelt die Markergröße in Pixeln basierend auf der Größeneinstellung
     */
    public static int getMarkerSize(int markerSize) {
        switch (markerSize) {
            case 1:
                return 40;
            case 2:
                return 50;
            case 3:
                return 60;
            case 4:
                return 70;
            case 5:
                return 80;
            default:
                return 40;
        }
    }

    /**
     * Erstellt Edge-Marker mit den angegebenen Einstellungen
     */
    public static void createEdgeMarker(ArrayList<ImageView> trackingPointListE, TrackingValues trackingValues) {
        // Wenn keine Eckmarker gewünscht sind, frühzeitig beenden
        if (trackingValues.getEdgeMarker() == TrackingValues.EdgeMarkerType.NONE) {
            return;
        }

        int edgeMarker = getEdgeMarkerDrawable(trackingValues.getEdgeMarker());
        int markerColor = Color.parseColor(trackingValues.getMarkerColor());

        for (ImageView marker : trackingPointListE) {
            marker.setImageResource(edgeMarker);
            marker.setColorFilter(markerColor);
        }
    }

    /**
     * Erstellt Scroll-Marker mit den angegebenen Einstellungen
     * Diese Methode wird für die statische Vorschau verwendet
     */
    public static void createScrollMarker(ArrayList<ImageView> trackingPointListSV,
                                          ArrayList<ImageView> trackingPointListSH,
                                          TrackingValues trackingValues) {
        // Alle Scroll-Marker zurücksetzen
        resetScrollMarkers(trackingPointListSV, trackingPointListSH);

        TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

        switch (scrollType) {
            case VERTICAL:
                createMarker(trackingPointListSV, trackingValues);
                break;
            case HORIZONTAL:
                createMarker(trackingPointListSH, trackingValues);
                break;
            case NONE:
            default:
                // Keine Scroll-Marker anzeigen
                break;
        }
    }

    /**
     * Setzt alle Scroll-Marker zurück
     */
    private static void resetScrollMarkers(ArrayList<ImageView> trackingPointListSV,
                                           ArrayList<ImageView> trackingPointListSH) {
        for (ImageView marker : trackingPointListSV) {
            marker.setImageResource(0);
        }
        for (ImageView marker : trackingPointListSH) {
            marker.setImageResource(0);
        }
    }

    /**
     * Bestimmt den Drawable-Ressourcen-ID basierend auf dem Edge-Marker-Typ
     */
    private static int getEdgeMarkerDrawable(TrackingValues.EdgeMarkerType edgeMarkerType) {
        switch (edgeMarkerType) {
            case CORNER:
                return R.drawable.ic_marker_cross_edge;
            case SEMICIRCLE:
                return R.drawable.ic_marker_circle_edge;
            default:
                return 0; // Sollte nicht erreicht werden
        }
    }

    /**
     * Berechnet optimale Marker-Abstände basierend auf Bildschirmgröße und Scroll-Typ
     */
    public static float[] calculateMarkerSpacing(Context context, TrackingValues.ScrollMarkerType scrollType) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        float spacingX, spacingY;

        switch (scrollType) {
            case VERTICAL:
                // Für vertikales Scrollen: Weniger dichte Marker
                spacingX = screenWidth / 3f; // 3 Marker pro Bildschirmbreite (reduziert von 4f)
                spacingY = screenHeight / 4f; // 4 Marker pro Bildschirmhöhe (reduziert von 6f)
                break;
            case HORIZONTAL:
                // Für horizontales Scrollen: Weniger dichte Marker
                spacingX = screenWidth / 4f; // 4 Marker pro Bildschirmbreite (reduziert von 6f)
                spacingY = screenHeight / 3f; // 3 Marker pro Bildschirmhöhe (reduziert von 4f)
                break;
            default:
                // Fallback-Werte - auch weniger dicht
                spacingX = screenWidth / 3.5f;
                spacingY = screenHeight / 3.5f;
                break;
        }

        return new float[]{spacingX, spacingY};
    }

    /**
     * Berechnet die optimale Scroll-Geschwindigkeit basierend auf der Marker-Dichte
     */
    public static float calculateScrollSensitivity(TrackingValues trackingValues) {
        // Basis-Sensitivität
        float baseSensitivity = 1.0f;

        // Anpassung basierend auf Marker-Dichte
        int density = trackingValues.getMarkerDensity();
        switch (density) {
            case 0:
                return baseSensitivity * 2.0f; // Schneller, da weniger Details
            case 1:
                return baseSensitivity * 1.5f;
            case 2:
                return baseSensitivity;
            case 3:
                return baseSensitivity * 0.7f; // Langsamer für präzise Bewegungen
            default:
                return baseSensitivity;
        }
    }

    /**
     * Prüft ob Scroll-Funktionalität verfügbar ist
     */
    public static boolean isScrollingEnabled(TrackingValues trackingValues) {
        return trackingValues != null &&
                trackingValues.getScrollMarker() != TrackingValues.ScrollMarkerType.NONE;
    }

    /**
     * Speichert die Tracking-Einstellungen
     */
    public static void saveSettings(Context context, TrackingValues trackingValues) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("backgroundColor", trackingValues.getBackgroundColor());
        editor.putString("markerColor", trackingValues.getMarkerColor());
        editor.putInt("markerDensity", trackingValues.getMarkerDensity());
        editor.putInt("markerSize", trackingValues.getMarkerSize());
        editor.putString("markerType", trackingValues.getMarkerType().name());
        editor.putString("edgeMarker", trackingValues.getEdgeMarker().name());
        editor.putString("scrollMarker", trackingValues.getScrollMarker().name());

        editor.apply();
    }

    /**
     * Lädt die Tracking-Einstellungen
     */
    public static TrackingValues loadSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        TrackingValues trackingValues = new TrackingValues();

        // Gespeicherte Werte oder Default-Werte verwenden
        trackingValues.setBackgroundColor(prefs.getString("backgroundColor", "#000000"));
        trackingValues.setMarkerColor(prefs.getString("markerColor", "#FFFFFF"));
        trackingValues.setMarkerDensity(prefs.getInt("markerDensity", 1));
        trackingValues.setMarkerSize(prefs.getInt("markerSize", 1));

        // Enum-Werte wiederherstellen
        try {
            String markerTypeString = prefs.getString("markerType", TrackingValues.MarkerType.CROSS.name());
            trackingValues.setMarkerType(TrackingValues.MarkerType.valueOf(markerTypeString));
        } catch (IllegalArgumentException e) {
            trackingValues.setMarkerType(TrackingValues.MarkerType.CROSS);
        }

        try {
            String edgeMarkerString = prefs.getString("edgeMarker", TrackingValues.EdgeMarkerType.NONE.name());
            trackingValues.setEdgeMarker(TrackingValues.EdgeMarkerType.valueOf(edgeMarkerString));
        } catch (IllegalArgumentException e) {
            trackingValues.setEdgeMarker(TrackingValues.EdgeMarkerType.NONE);
        }

        try {
            String scrollMarkerString = prefs.getString("scrollMarker", TrackingValues.ScrollMarkerType.NONE.name());
            trackingValues.setScrollMarker(TrackingValues.ScrollMarkerType.valueOf(scrollMarkerString));
        } catch (IllegalArgumentException e) {
            trackingValues.setScrollMarker(TrackingValues.ScrollMarkerType.NONE);
        }

        return trackingValues;
    }
}
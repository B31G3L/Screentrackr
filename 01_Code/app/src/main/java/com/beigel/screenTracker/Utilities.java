package com.beigel.screenTracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Statische Utility-Klasse mit verbesserten Methoden für die Marker-Erstellung
 * und Einstellungsverwaltung
 */
public class Utilities {

    private static final String PREFS_NAME = "ScreentrackrPrefs";

    // Privater Konstruktor verhindert Instanziierung
    private Utilities() {
        throw new UnsupportedOperationException("Utilities ist eine statische Hilfsklasse");
    }

    /**
     * Erstellt Marker mit den angegebenen Einstellungen
     *
     * @param trackingPointList Liste der ImageViews für die Marker
     * @param trackingValues Einstellungen für die Marker
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
     *
     * @param trackingPointListE Liste der ImageViews für die Edge-Marker
     * @param trackingValues Einstellungen für die Marker
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
     * Speichert die Tracking-Einstellungen
     *
     * @param context App-Kontext
     * @param trackingValues Zu speichernde Einstellungen
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

        editor.apply();
    }

    /**
     * Lädt die Tracking-Einstellungen
     *
     * @param context App-Kontext
     * @return Gespeicherte Einstellungen oder Default-Werte, falls keine Einstellungen existieren
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

        return trackingValues;
    }
}
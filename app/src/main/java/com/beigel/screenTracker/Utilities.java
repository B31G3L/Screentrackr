package com.beigel.screenTracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Aufgeräumte und erweiterte Utilities-Klasse
 * - Besseres Error Handling
 * - Verwendung von AppConstants
 * - Defensive Programmierung
 * - Proper Logging
 */
public final class Utilities {

    private static final String TAG = AppConstants.LogTags.MAIN;

    // Privater Konstruktor verhindert Instanziierung
    private Utilities() {
        throw new UnsupportedOperationException("Utilities ist eine statische Hilfsklasse");
    }

    // ========== MARKER CREATION ==========

    /**
     * Erstellt Marker mit verbessertem Error Handling
     */
    public static void createMarker(@NonNull ArrayList<ImageView> trackingPointList,
                                    @NonNull TrackingValues trackingValues) {
        if (trackingPointList.isEmpty()) {
            Log.w(TAG, "Leere Marker-Liste übergeben");
            return;
        }

        try {
            int markerType = getMarkerDrawable(trackingValues.getMarkerType());
            int markerSize = getMarkerSizeInPixels(trackingValues.getMarkerSize());
            int markerColor = parseColorSafely(trackingValues.getMarkerColor());

            for (ImageView marker : trackingPointList) {
                if (marker != null) {
                    applyMarkerProperties(marker, markerType, markerSize, markerColor);
                } else {
                    Log.w(TAG, "Null ImageView in Marker-Liste gefunden");
                }
            }

            Log.d(TAG, String.format("Marker erstellt: Typ=%s, Größe=%d, Farbe=%s",
                    trackingValues.getMarkerType(), markerSize, trackingValues.getMarkerColor()));

        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Erstellen der Marker", e);
        }
    }

    /**
     * Wendet Marker-Eigenschaften auf einen ImageView an
     */
    private static void applyMarkerProperties(@NonNull ImageView marker,
                                              int drawableRes,
                                              int size,
                                              int color) {
        marker.setImageResource(drawableRes);

        // Größe sicher setzen
        if (marker.getLayoutParams() != null) {
            marker.getLayoutParams().height = size;
            marker.getLayoutParams().width = size;
            marker.requestLayout();
        }

        marker.setColorFilter(color);
    }

    /**
     * Bestimmt den Drawable-Ressourcen-ID basierend auf dem Marker-Typ
     * Jetzt mit AppConstants
     */
    public static int getMarkerDrawable(@NonNull TrackingValues.MarkerType markerType) {
        switch (markerType) {
            case PIE:
                return AppConstants.MarkerDrawables.PIE;
            case CIRCLE:
                return AppConstants.MarkerDrawables.CIRCLE;
            case TRIANGLE:
                return AppConstants.MarkerDrawables.TRIANGLE;
            case CROSS:
            default:
                return AppConstants.MarkerDrawables.CROSS;
        }
    }

    /**
     * Verbesserte Markergröße-Berechnung mit Konstanten
     */
    public static int getMarkerSizeInPixels(int markerSize) {
        switch (markerSize) {
            case 1: return AppConstants.MarkerSizes.SIZE_1;
            case 2: return AppConstants.MarkerSizes.SIZE_2;
            case 3: return AppConstants.MarkerSizes.SIZE_3;
            case 4: return AppConstants.MarkerSizes.SIZE_4;
            case 5: return AppConstants.MarkerSizes.SIZE_5;
            default:
                Log.w(TAG, "Ungültige Markergröße: " + markerSize + ", verwende Standard");
                return AppConstants.MarkerSizes.DEFAULT_SIZE;
        }
    }

    /**
     * Legacy-Methode für Kompatibilität
     * @deprecated Verwende {@link #getMarkerSizeInPixels(int)} stattdessen
     */
    @Deprecated
    public static int getMarkerSize(int markerSize) {
        return getMarkerSizeInPixels(markerSize);
    }

    // ========== EDGE MARKERS ==========


    /**
     * Verbesserte Edge-Marker-Größe-Berechnung mit separaten Konstanten
     */
    public static int getEdgeMarkerSizeInPixels(int edgeMarkerSize) {
        switch (edgeMarkerSize) {
            case 1: return AppConstants.MarkerSizes.EDGE_SIZE_1;
            case 2: return AppConstants.MarkerSizes.EDGE_SIZE_2;
            case 3: return AppConstants.MarkerSizes.EDGE_SIZE_3;
            case 4: return AppConstants.MarkerSizes.EDGE_SIZE_4;
            case 5: return AppConstants.MarkerSizes.EDGE_SIZE_5;
            default:
                Log.w(TAG, "Ungültige Edge-Markergröße: " + edgeMarkerSize + ", verwende Standard");
                return AppConstants.MarkerSizes.DEFAULT_EDGE_SIZE;
        }
    }
    /**
     * Erstellt Edge-Marker mit verbessertem Error Handling und konfigurierbarer Größe
     */
    public static void createEdgeMarker(@NonNull ArrayList<ImageView> trackingPointListE,
                                        @NonNull TrackingValues trackingValues) {
        TrackingValues.EdgeMarkerType edgeType = trackingValues.getEdgeMarker();

        if (edgeType == TrackingValues.EdgeMarkerType.NONE) {
            clearMarkers(trackingPointListE);
            return;
        }

        try {
            int edgeMarkerRes = getEdgeMarkerDrawable(edgeType);
            int edgeMarkerSize = getEdgeMarkerSizeInPixels(trackingValues.getEdgeMarkerSize());  // NEU: Konfigurierbare Größe
            int markerColor = parseColorSafely(trackingValues.getMarkerColor());

            for (ImageView marker : trackingPointListE) {
                if (marker != null) {
                    marker.setImageResource(edgeMarkerRes);
                    marker.setColorFilter(markerColor);

                    // Größe sicher setzen (NEU)
                    if (marker.getLayoutParams() != null) {
                        marker.getLayoutParams().height = edgeMarkerSize;
                        marker.getLayoutParams().width = edgeMarkerSize;
                        marker.requestLayout();
                    }
                } else {
                    Log.w(TAG, "Null ImageView in Edge-Marker-Liste");
                }
            }

            Log.d(TAG, "Edge-Marker erstellt: " + edgeType + ", Größe: " + edgeMarkerSize + "px");

        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Erstellen der Edge-Marker", e);
        }
    }

    /**
     * Bestimmt Edge-Marker Drawable mit AppConstants
     */
    private static int getEdgeMarkerDrawable(@NonNull TrackingValues.EdgeMarkerType edgeMarkerType) {
        switch (edgeMarkerType) {
            case CORNER:
                return AppConstants.MarkerDrawables.EDGE_CORNER;
            case SEMICIRCLE:
                return AppConstants.MarkerDrawables.EDGE_SEMICIRCLE;
            case NONE:
            default:
                return 0;
        }
    }

    // ========== COLOR UTILITIES ==========

    /**
     * Sichere Farb-Parsing mit Fallback
     */
    public static int parseColorSafely(@Nullable String colorString) {
        if (colorString == null || colorString.trim().isEmpty()) {
            Log.w(TAG, "Null/leerer Farb-String, verwende Standard");
            return Color.parseColor(AppConstants.DEFAULT_MARKER_COLOR);
        }

        try {
            return Color.parseColor(colorString);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Ungültiger Farb-Code: " + colorString + ", verwende Standard", e);
            return Color.parseColor(AppConstants.DEFAULT_MARKER_COLOR);
        }
    }

    /**
     * Validiert Hex-Farbcode
     */
    public static boolean isValidHexColor(@Nullable String colorCode) {
        if (colorCode == null) return false;
        return colorCode.matches(AppConstants.Validation.HEX_COLOR_PATTERN);
    }

    // ========== SETTINGS PERSISTENCE ==========

    /**
     * Verbesserte Settings-Speicherung mit Edge-Marker-Größe und separatem Scroll-Marker-Typ
     */
    public static void saveSettings(@NonNull Context context, @NonNull TrackingValues trackingValues) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.Prefs.NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(AppConstants.Prefs.KEY_BACKGROUND_COLOR, trackingValues.getBackgroundColor());
            editor.putString(AppConstants.Prefs.KEY_MARKER_COLOR, trackingValues.getMarkerColor());
            editor.putInt(AppConstants.Prefs.KEY_MARKER_DENSITY, trackingValues.getMarkerDensity());
            editor.putInt(AppConstants.Prefs.KEY_MARKER_SIZE, trackingValues.getMarkerSize());
            editor.putInt(AppConstants.Prefs.KEY_EDGE_MARKER_SIZE, trackingValues.getEdgeMarkerSize());
            editor.putString(AppConstants.Prefs.KEY_MARKER_TYPE, trackingValues.getMarkerType().name());
            editor.putString(AppConstants.Prefs.KEY_EDGE_MARKER, trackingValues.getEdgeMarker().name());
            editor.putString(AppConstants.Prefs.KEY_SCROLL_MARKER, trackingValues.getScrollMarker().name());

            // NEU: Separate Scroll-Marker Einstellungen
            editor.putBoolean(AppConstants.Prefs.KEY_USE_CUSTOM_SCROLL_MARKER, trackingValues.isUseCustomScrollMarker());
            editor.putString(AppConstants.Prefs.KEY_SCROLL_MARKER_TYPE, trackingValues.getScrollMarkerType().name());

            boolean success = editor.commit();

            if (success) {
                Log.d(TAG, "Einstellungen erfolgreich gespeichert (mit separatem Scroll-Marker-Typ)");
            } else {
                Log.e(TAG, "Fehler beim Speichern der Einstellungen");
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception beim Speichern der Einstellungen", e);
        }
    }

    /**
     * Verbesserte Settings-Ladung mit Edge-Marker-Größe und separatem Scroll-Marker-Typ
     */
    @NonNull
    public static TrackingValues loadSettings(@NonNull Context context) {
        TrackingValues trackingValues = new TrackingValues();

        try {
            SharedPreferences prefs = context.getSharedPreferences(AppConstants.Prefs.NAME, Context.MODE_PRIVATE);

            // Mit Validierung laden
            trackingValues.setBackgroundColor(
                    prefs.getString(AppConstants.Prefs.KEY_BACKGROUND_COLOR, AppConstants.DEFAULT_BACKGROUND_COLOR));
            trackingValues.setMarkerColor(
                    prefs.getString(AppConstants.Prefs.KEY_MARKER_COLOR, AppConstants.DEFAULT_MARKER_COLOR));
            trackingValues.setMarkerDensity(
                    prefs.getInt(AppConstants.Prefs.KEY_MARKER_DENSITY, 1));
            trackingValues.setMarkerSize(
                    prefs.getInt(AppConstants.Prefs.KEY_MARKER_SIZE, 1));
            trackingValues.setEdgeMarkerSize(
                    prefs.getInt(AppConstants.Prefs.KEY_EDGE_MARKER_SIZE, 1));

            // Enum-Werte sicher wiederherstellen
            loadMarkerTypeEnum(prefs, trackingValues);
            loadEdgeMarkerEnum(prefs, trackingValues);
            loadScrollMarkerEnum(prefs, trackingValues);

            // NEU: Separate Scroll-Marker Einstellungen laden
            trackingValues.setUseCustomScrollMarker(
                    prefs.getBoolean(AppConstants.Prefs.KEY_USE_CUSTOM_SCROLL_MARKER, false));
            loadScrollMarkerTypeEnum(prefs, trackingValues);

            Log.d(TAG, "Einstellungen erfolgreich geladen (mit separatem Scroll-Marker-Typ)");

        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Laden der Einstellungen, verwende Standardwerte", e);
        }

        return trackingValues;
    }

    /**
     * Hilfsmethoden für sichere Enum-Ladung ohne Java 8 Features
     */
    private static void loadMarkerTypeEnum(SharedPreferences prefs, TrackingValues trackingValues) {
        try {
            String value = prefs.getString(AppConstants.Prefs.KEY_MARKER_TYPE, TrackingValues.MarkerType.CROSS.name());
            TrackingValues.MarkerType enumValue = TrackingValues.MarkerType.valueOf(value);
            trackingValues.setMarkerType(enumValue);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Ungültiger MarkerType-Wert, verwende Standard: " + TrackingValues.MarkerType.CROSS);
            trackingValues.setMarkerType(TrackingValues.MarkerType.CROSS);
        }
    }

    private static void loadEdgeMarkerEnum(SharedPreferences prefs, TrackingValues trackingValues) {
        try {
            String value = prefs.getString(AppConstants.Prefs.KEY_EDGE_MARKER, TrackingValues.EdgeMarkerType.NONE.name());
            TrackingValues.EdgeMarkerType enumValue = TrackingValues.EdgeMarkerType.valueOf(value);
            trackingValues.setEdgeMarker(enumValue);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Ungültiger EdgeMarkerType-Wert, verwende Standard: " + TrackingValues.EdgeMarkerType.NONE);
            trackingValues.setEdgeMarker(TrackingValues.EdgeMarkerType.NONE);
        }
    }

    private static void loadScrollMarkerEnum(SharedPreferences prefs, TrackingValues trackingValues) {
        try {
            String value = prefs.getString(AppConstants.Prefs.KEY_SCROLL_MARKER, TrackingValues.ScrollMarkerType.NONE.name());
            TrackingValues.ScrollMarkerType enumValue = TrackingValues.ScrollMarkerType.valueOf(value);
            trackingValues.setScrollMarker(enumValue);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Ungültiger ScrollMarkerType-Wert, verwende Standard: " + TrackingValues.ScrollMarkerType.NONE);
            trackingValues.setScrollMarker(TrackingValues.ScrollMarkerType.NONE);
        }
    }

    private static void loadScrollMarkerTypeEnum(SharedPreferences prefs, TrackingValues trackingValues) {
        try {
            String value = prefs.getString(AppConstants.Prefs.KEY_SCROLL_MARKER_TYPE, TrackingValues.MarkerType.CROSS.name());
            TrackingValues.MarkerType enumValue = TrackingValues.MarkerType.valueOf(value);
            trackingValues.setScrollMarkerType(enumValue);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Ungültiger ScrollMarkerType-Wert, verwende Standard: " + TrackingValues.MarkerType.CROSS);
            trackingValues.setScrollMarkerType(TrackingValues.MarkerType.CROSS);
        }
    }

    // ========== HELPER METHODS ==========

    /**
     * Entfernt alle Marker aus einer Liste
     */
    private static void clearMarkers(@NonNull ArrayList<ImageView> markerList) {
        for (ImageView marker : markerList) {
            if (marker != null) {
                marker.setImageResource(0);
                marker.clearColorFilter();
            }
        }
    }

    /**
     * Berechnet die Helligkeit einer Farbe
     */
    public static boolean isColorBright(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        double luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0;
        return luminance > AppConstants.UI.COLOR_BRIGHTNESS_THRESHOLD;
    }
}
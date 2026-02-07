package com.beigel.screenTracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Verbesserte TrackingValues-Klasse mit erweiterten Funktionen
 * - AppConstants Integration
 * - Bessere Validierung
 * - toString für Debugging
 * - Immutable Enums
 * - Separate Scroll-Marker Konfiguration
 */
public class TrackingValues implements Serializable {
    private static final long serialVersionUID = 2L; // Erhöht wegen neuer Felder

    /**
     * Verfügbare Marker-Typen
     */
    public enum MarkerType {
        PIE("Pie"),
        CIRCLE("Circle"),
        TRIANGLE("Triangle"),
        CROSS("Cross");

        private final String displayName;

        MarkerType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static MarkerType fromString(@Nullable String text) {
            if (text == null) return CROSS;

            for (MarkerType type : MarkerType.values()) {
                if (type.displayName.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return CROSS; // Default-Wert
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Verfügbare Edge-Marker-Typen
     */
    public enum EdgeMarkerType {
        NONE("None"),
        CORNER("Corner"),
        SEMICIRCLE("Semicircle");

        private final String displayName;

        EdgeMarkerType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static EdgeMarkerType fromString(@Nullable String text) {
            if (text == null) return NONE;

            for (EdgeMarkerType type : EdgeMarkerType.values()) {
                if (type.displayName.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return NONE; // Default-Wert
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Verfügbare Scroll-Marker-Typen
     */
    public enum ScrollMarkerType {
        NONE("None"),
        VERTICAL("Vertical"),
        HORIZONTAL("Horizontal");

        private final String displayName;

        ScrollMarkerType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static ScrollMarkerType fromString(@Nullable String text) {
            if (text == null) return NONE;

            for (ScrollMarkerType type : ScrollMarkerType.values()) {
                if (type.displayName.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return NONE; // Default-Wert
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    // Felder mit AppConstants als Standardwerte
    private String backgroundColor = AppConstants.DEFAULT_BACKGROUND_COLOR;
    private String markerColor = AppConstants.DEFAULT_MARKER_COLOR;
    private int markerDensity = 1;
    private int markerSize = 1;
    private int edgeMarkerSize = 1;

    private MarkerType markerType = MarkerType.CROSS;
    private EdgeMarkerType edgeMarker = EdgeMarkerType.NONE;
    private ScrollMarkerType scrollMarker = ScrollMarkerType.NONE;

    // NEU: Separate Scroll-Marker Einstellungen
    private boolean useCustomScrollMarker = false;
    private MarkerType scrollMarkerType = MarkerType.CROSS;

    // ========== CONSTRUCTORS ==========

    /**
     * Standard-Konstruktor mit Default-Werten
     */
    public TrackingValues() {
        // Standardwerte werden in den Feldern gesetzt
    }

    /**
     * Copy-Konstruktor
     */
    public TrackingValues(@NonNull TrackingValues other) {
        this.backgroundColor = other.backgroundColor;
        this.markerColor = other.markerColor;
        this.markerDensity = other.markerDensity;
        this.markerSize = other.markerSize;
        this.edgeMarkerSize = other.edgeMarkerSize;
        this.markerType = other.markerType;
        this.edgeMarker = other.edgeMarker;
        this.scrollMarker = other.scrollMarker;
        this.useCustomScrollMarker = other.useCustomScrollMarker;
        this.scrollMarkerType = other.scrollMarkerType;
    }

    // ========== GETTERS AND SETTERS WITH VALIDATION ==========

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(@Nullable String backgroundColor) {
        if (isValidHexColor(backgroundColor)) {
            this.backgroundColor = backgroundColor;
        } else {
            this.backgroundColor = AppConstants.DEFAULT_BACKGROUND_COLOR;
        }
    }

    public String getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(@Nullable String markerColor) {
        if (isValidHexColor(markerColor)) {
            this.markerColor = markerColor;
        } else {
            this.markerColor = AppConstants.DEFAULT_MARKER_COLOR;
        }
    }

    public int getMarkerDensity() {
        return markerDensity;
    }

    public void setMarkerDensity(int markerDensity) {
        if (markerDensity >= AppConstants.Validation.MIN_MARKER_DENSITY &&
                markerDensity <= AppConstants.Validation.MAX_MARKER_DENSITY) {
            this.markerDensity = markerDensity;
        }
    }

    // Überladene Methode für String-Eingabe
    public void setMarkerDensity(@Nullable String markerDensity) {
        try {
            setMarkerDensity(Integer.parseInt(markerDensity));
        } catch (NumberFormatException e) {
            // Behalte den aktuellen Wert bei
        }
    }

    public int getMarkerSize() {
        return markerSize;
    }

    public void setMarkerSize(int markerSize) {
        if (markerSize >= AppConstants.Validation.MIN_MARKER_SIZE &&
                markerSize <= AppConstants.Validation.MAX_MARKER_SIZE) {
            this.markerSize = markerSize;
        }
    }

    // Überladene Methode für String-Eingabe
    public void setMarkerSize(@Nullable String markerSize) {
        try {
            setMarkerSize(Integer.parseInt(markerSize));
        } catch (NumberFormatException e) {
            // Behalte den aktuellen Wert bei
        }
    }

    public MarkerType getMarkerType() {
        return markerType;
    }

    public void setMarkerType(@Nullable MarkerType markerType) {
        this.markerType = markerType != null ? markerType : MarkerType.CROSS;
    }

    // Überladene Methode für String-Eingabe
    public void setMarkerType(@Nullable String markerType) {
        this.markerType = MarkerType.fromString(markerType);
    }

    public EdgeMarkerType getEdgeMarker() {
        return edgeMarker;
    }

    public void setEdgeMarker(@Nullable EdgeMarkerType edgeMarker) {
        this.edgeMarker = edgeMarker != null ? edgeMarker : EdgeMarkerType.NONE;
    }

    // Überladene Methode für String-Eingabe
    public void setEdgeMarker(@Nullable String edgeMarker) {
        this.edgeMarker = EdgeMarkerType.fromString(edgeMarker);
    }

    public ScrollMarkerType getScrollMarker() {
        return scrollMarker;
    }

    public void setScrollMarker(@Nullable ScrollMarkerType scrollMarker) {
        this.scrollMarker = scrollMarker != null ? scrollMarker : ScrollMarkerType.NONE;
    }

    // Überladene Methode für String-Eingabe
    public void setScrollMarker(@Nullable String scrollMarker) {
        this.scrollMarker = ScrollMarkerType.fromString(scrollMarker);
    }

    public int getEdgeMarkerSize() {
        return edgeMarkerSize;
    }

    public void setEdgeMarkerSize(int edgeMarkerSize) {
        if (edgeMarkerSize >= AppConstants.Validation.MIN_EDGE_MARKER_SIZE &&
                edgeMarkerSize <= AppConstants.Validation.MAX_EDGE_MARKER_SIZE) {
            this.edgeMarkerSize = edgeMarkerSize;
        }
    }

    // Überladene Methode für String-Eingabe
    public void setEdgeMarkerSize(@Nullable String edgeMarkerSize) {
        try {
            setEdgeMarkerSize(Integer.parseInt(edgeMarkerSize));
        } catch (NumberFormatException e) {
            // Behalte den aktuellen Wert bei
        }
    }

    // ========== NEU: SEPARATE SCROLL-MARKER GETTERS/SETTERS ==========

    public boolean isUseCustomScrollMarker() {
        return useCustomScrollMarker;
    }

    public void setUseCustomScrollMarker(boolean useCustomScrollMarker) {
        this.useCustomScrollMarker = useCustomScrollMarker;
    }

    public MarkerType getScrollMarkerType() {
        return scrollMarkerType;
    }

    public void setScrollMarkerType(@Nullable MarkerType scrollMarkerType) {
        this.scrollMarkerType = scrollMarkerType != null ? scrollMarkerType : MarkerType.CROSS;
    }

    // Überladene Methode für String-Eingabe
    public void setScrollMarkerType(@Nullable String scrollMarkerType) {
        this.scrollMarkerType = MarkerType.fromString(scrollMarkerType);
    }

    /**
     * Gibt den effektiv zu verwendenden Marker-Typ für Scroll-Marker zurück
     * @return scrollMarkerType wenn useCustomScrollMarker=true, sonst markerType
     */
    public MarkerType getEffectiveScrollMarkerType() {
        return useCustomScrollMarker ? scrollMarkerType : markerType;
    }

    // ========== LEGACY METHODS FOR COMPATIBILITY ==========

    /**
     * @deprecated Verwende {@link #getMarkerDensity()} direkt
     */
    @Deprecated
    public String getMarkerDensityAsString() {
        return String.valueOf(markerDensity);
    }

    /**
     * @deprecated Verwende {@link #getMarkerSize()} direkt
     */
    @Deprecated
    public String getMarkerSizeAsString() {
        return String.valueOf(markerSize);
    }

    /**
     * @deprecated Verwende {@link #getMarkerType()} direkt
     */
    @Deprecated
    public String getMarkerTypeAsString() {
        return markerType.toString();
    }

    /**
     * @deprecated Verwende {@link #getEdgeMarker()} direkt
     */
    @Deprecated
    public String getEdgeMarkerAsString() {
        return edgeMarker.toString();
    }

    /**
     * @deprecated Verwende {@link #getScrollMarker()} direkt
     */
    @Deprecated
    public String getScrollMarkerAsString() {
        return scrollMarker.toString();
    }

    /**
     * @deprecated Verwende {@link #getEdgeMarkerSize()} direkt
     */
    @Deprecated
    public String getEdgeMarkerSizeAsString() {
        return String.valueOf(edgeMarkerSize);
    }

    // ========== VALIDATION METHODS ==========

    /**
     * Validiert Hex-Farbcode mit AppConstants
     */
    private boolean isValidHexColor(@Nullable String colorCode) {
        if (colorCode == null) return false;
        return colorCode.matches(AppConstants.Validation.HEX_COLOR_PATTERN);
    }

    /**
     * Prüft ob alle Einstellungen gültig sind
     */
    public boolean isValid() {
        return isValidHexColor(backgroundColor) &&
                isValidHexColor(markerColor) &&
                markerDensity >= AppConstants.Validation.MIN_MARKER_DENSITY &&
                markerDensity <= AppConstants.Validation.MAX_MARKER_DENSITY &&
                markerSize >= AppConstants.Validation.MIN_MARKER_SIZE &&
                markerSize <= AppConstants.Validation.MAX_MARKER_SIZE &&
                edgeMarkerSize >= AppConstants.Validation.MIN_EDGE_MARKER_SIZE &&
                edgeMarkerSize <= AppConstants.Validation.MAX_EDGE_MARKER_SIZE &&
                markerType != null &&
                edgeMarker != null &&
                scrollMarker != null &&
                scrollMarkerType != null;
    }

    /**
     * Setzt alle Werte auf gültige Standardwerte zurück
     */
    public void resetToDefaults() {
        backgroundColor = AppConstants.DEFAULT_BACKGROUND_COLOR;
        markerColor = AppConstants.DEFAULT_MARKER_COLOR;
        markerDensity = 1;
        markerSize = 1;
        edgeMarkerSize = 1;
        markerType = MarkerType.CROSS;
        edgeMarker = EdgeMarkerType.NONE;
        scrollMarker = ScrollMarkerType.NONE;
        useCustomScrollMarker = false;
        scrollMarkerType = MarkerType.CROSS;
    }

    // ========== OBJECT METHODS ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackingValues that = (TrackingValues) o;

        if (markerDensity != that.markerDensity) return false;
        if (markerSize != that.markerSize) return false;
        if (edgeMarkerSize != that.edgeMarkerSize) return false;
        if (useCustomScrollMarker != that.useCustomScrollMarker) return false;
        if (!backgroundColor.equals(that.backgroundColor)) return false;
        if (!markerColor.equals(that.markerColor)) return false;
        if (markerType != that.markerType) return false;
        if (edgeMarker != that.edgeMarker) return false;
        if (scrollMarker != that.scrollMarker) return false;
        return scrollMarkerType == that.scrollMarkerType;
    }

    @Override
    public int hashCode() {
        int result = backgroundColor.hashCode();
        result = 31 * result + markerColor.hashCode();
        result = 31 * result + markerDensity;
        result = 31 * result + markerSize;
        result = 31 * result + edgeMarkerSize;
        result = 31 * result + markerType.hashCode();
        result = 31 * result + edgeMarker.hashCode();
        result = 31 * result + scrollMarker.hashCode();
        result = 31 * result + (useCustomScrollMarker ? 1 : 0);
        result = 31 * result + scrollMarkerType.hashCode();
        return result;
    }

    @Override
    @NonNull
    public String toString() {
        return "TrackingValues{" +
                "backgroundColor='" + backgroundColor + '\'' +
                ", markerColor='" + markerColor + '\'' +
                ", markerDensity=" + markerDensity +
                ", markerSize=" + markerSize +
                ", edgeMarkerSize=" + edgeMarkerSize +
                ", markerType=" + markerType +
                ", edgeMarker=" + edgeMarker +
                ", scrollMarker=" + scrollMarker +
                ", useCustomScrollMarker=" + useCustomScrollMarker +
                ", scrollMarkerType=" + scrollMarkerType +
                ", isValid=" + isValid() +
                '}';
    }
}
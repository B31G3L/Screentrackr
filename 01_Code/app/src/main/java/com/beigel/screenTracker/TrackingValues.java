package com.beigel.screenTracker;

import java.io.Serializable;

/**
 * Verbesserte TrackingValues-Klasse mit Enums für sicherere Typisierung
 * und bessere Code-Qualität
 */
public class TrackingValues implements Serializable {
    private static final long serialVersionUID = 1L;

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

        public static MarkerType fromString(String text) {
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

        public static EdgeMarkerType fromString(String text) {
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

    // Standardwerte
    private String backgroundColor = "#000000";
    private String markerColor = "#FFFFFF";
    private int markerDensity = 1;
    private int markerSize = 1;
    private MarkerType markerType = MarkerType.CROSS;
    private EdgeMarkerType edgeMarker = EdgeMarkerType.NONE;

    // Konstruktoren
    public TrackingValues() {
        // Standardwerte werden in den Feldern gesetzt
    }

    // Getter und Setter mit Validierung
    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        if (isValidHexColor(backgroundColor)) {
            this.backgroundColor = backgroundColor;
        }
    }

    public String getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(String markerColor) {
        if (isValidHexColor(markerColor)) {
            this.markerColor = markerColor;
        }
    }

    public int getMarkerDensity() {
        return markerDensity;
    }

    public void setMarkerDensity(int markerDensity) {
        if (markerDensity >= 0 && markerDensity <= 3) {
            this.markerDensity = markerDensity;
        }
    }

    // Überladene Methode für String-Eingabe
    public void setMarkerDensity(String markerDensity) {
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
        if (markerSize >= 1 && markerSize <= 5) {
            this.markerSize = markerSize;
        }
    }

    // Überladene Methode für String-Eingabe
    public void setMarkerSize(String markerSize) {
        try {
            setMarkerSize(Integer.parseInt(markerSize));
        } catch (NumberFormatException e) {
            // Behalte den aktuellen Wert bei
        }
    }

    public MarkerType getMarkerType() {
        return markerType;
    }

    public void setMarkerType(MarkerType markerType) {
        if (markerType != null) {
            this.markerType = markerType;
        }
    }

    // Überladene Methode für String-Eingabe
    public void setMarkerType(String markerType) {
        this.markerType = MarkerType.fromString(markerType);
    }

    public EdgeMarkerType getEdgeMarker() {
        return edgeMarker;
    }

    public void setEdgeMarker(EdgeMarkerType edgeMarker) {
        if (edgeMarker != null) {
            this.edgeMarker = edgeMarker;
        }
    }

    // Überladene Methode für String-Eingabe
    public void setEdgeMarker(String edgeMarker) {
        this.edgeMarker = EdgeMarkerType.fromString(edgeMarker);
    }

    // Legacy-Methoden für Kompatibilität
    public String getMarkerDensityAsString() {
        return String.valueOf(markerDensity);
    }

    public String getMarkerSizeAsString() {
        return String.valueOf(markerSize);
    }

    public String getMarkerTypeAsString() {
        return markerType.toString();
    }

    public String getEdgeMarkerAsString() {
        return edgeMarker.toString();
    }

    // Hilfsmethode zur Validierung von Hex-Farbcodes
    private boolean isValidHexColor(String colorCode) {
        return colorCode != null && colorCode.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$");
    }
}
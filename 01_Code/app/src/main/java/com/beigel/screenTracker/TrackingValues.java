package com.beigel.screenTracker;

import java.io.Serializable;

public class TrackingValues implements Serializable {
    private String backgroundColor = "#000000";
    private String markerColor = "#FFFFFF";
    private String markerDensity = "1";
    private String markerSize = "1";
    private String markerType = "Cross";
    private String edgeMarker = "None";

    // Getter und Setter für alle Eigenschaften
    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getMarkerColor() {
        return markerColor;
    }

    public void setMarkerColor(String markerColor) {
        this.markerColor = markerColor;
    }

    public String getMarkerDensity() {
        return markerDensity;
    }

    public void setMarkerDensity(String markerDensity) {
        this.markerDensity = markerDensity;
    }

    public String getMarkerSize() {
        return markerSize;
    }

    public void setMarkerSize(String markerSize) {
        this.markerSize = markerSize;
    }

    public String getMarkerType() {
        return markerType;
    }

    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }

    public String getEdgeMarker() {
        return edgeMarker;
    }

    public void setEdgeMarker(String edgeMarker) {
        this.edgeMarker = edgeMarker;
    }
}
package com.beigel.screenTracker;

import android.graphics.Color;
import android.widget.ImageView;

import java.util.ArrayList;

public class Utilities {

    public void createMarker(ArrayList<ImageView> trackingPointList, TrackingValues trackingValues) {
        int markerType = R.drawable.ic_marker_cross;

        switch (trackingValues.getMarkerType()) {
            case "Pie":
                markerType = R.drawable.ic_marker_pie;
                break;
            case "Circle":
                markerType = R.drawable.ic_marker_circle;
                break;
            case "Triangle":
                markerType = R.drawable.ic_marker_triangle;
                break;
            case "Cross":
                markerType = R.drawable.ic_marker_cross;
                break;
        }

        for (ImageView x : trackingPointList) {
            x.setImageResource(markerType);
            x.getLayoutParams().height = getMarkerSize(trackingValues.getMarkerSize());
            x.getLayoutParams().width = getMarkerSize(trackingValues.getMarkerSize());
            x.setColorFilter(Color.parseColor(trackingValues.getMarkerColor()));
        }
    }

    private int getMarkerSize(String markerSize) {
        switch (markerSize) {
            case "1":
                return 40;
            case "2":
                return 50;
            case "3":
                return 60;
            case "4":
                return 70;
            case "5":
                return 80;
            default:
                return 40;
        }
    }

    public void createEdgeMarker(ArrayList<ImageView> trackingPointListE, TrackingValues trackingValues) {
        int edgeMarker = R.drawable.ic_marker_cross_edge;

        switch (trackingValues.getEdgeMarker()) {
            case "Corner":
                edgeMarker = R.drawable.ic_marker_cross_edge;
                break;
            case "Semicircle":
                edgeMarker = R.drawable.ic_marker_circle_edge;
                break;
        }

        for (ImageView x : trackingPointListE) {
            x.setImageResource(edgeMarker);
            x.setColorFilter(Color.parseColor(trackingValues.getMarkerColor()));
        }
    }
}
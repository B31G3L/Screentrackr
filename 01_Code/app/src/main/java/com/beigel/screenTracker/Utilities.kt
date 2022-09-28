package com.beigel.screenTracker

import android.graphics.Color
import android.widget.ImageView


class Utilities {

     fun createMarker(trackingPointList: ArrayList<ImageView>, trackingValues: TrackingValues){
        var markerType :Int = R.drawable.ic_marker_cross
        when (trackingValues.markerType) {
            "Pie" -> {
                markerType = R.drawable.ic_marker_pie
            }
            "Circle" -> {
                markerType = R.drawable.ic_marker_circle
            }
            "Triangle" -> {
                markerType = R.drawable.ic_marker_triangle
            }
            "Cross" -> {
                markerType = R.drawable.ic_marker_cross
            }
        }

        for(x in trackingPointList){
            x.setImageResource(markerType)
            x.layoutParams.height = getMarkerSize(trackingValues.markerSize)
            x.layoutParams.width = getMarkerSize(trackingValues.markerSize)
            x.setColorFilter( Color.parseColor(trackingValues.markerColor))
        }
    }


       private  fun getMarkerSize(markerSize: String): Int {
            when (markerSize) {
                "1" -> {
                    return 40
                }
                "2" -> {
                    return 50
                }
                "3" -> {
                    return 60
                }
                "4" -> {
                    return 70
                }
                "5" -> {
                    return 80
                }
            }
            return 40
        }

    fun createEdgeMarker(trackingPointListE: ArrayList<ImageView>, trackingValues: TrackingValues) {
        var edgeMarker :Int = R.drawable.ic_marker_cross_edge
        when (trackingValues.edgeMarker) {
            "Corner" -> {
                edgeMarker = R.drawable.ic_marker_cross_edge
            }
            "Semicircle" -> {
                edgeMarker = R.drawable.ic_marker_circle_edge
            }
        }

        for(x in trackingPointListE){
            x.setImageResource(edgeMarker)
            x.setColorFilter(Color.parseColor(trackingValues.markerColor))

        }
    }
}
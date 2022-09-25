package com.beigel.screenTracker

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.widget.ImageView


class Utilies {

     fun createMarker(trackingPointList: ArrayList<ImageView>, trackingValues: TrackingValues){
        var markerType :Int = R.drawable.ic_marker_cross
        val markerSize: Int = getMarkerSize(trackingValues.markerSize)
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
          //foto is my ImageView

            x.setImageResource(markerType)
            x.layoutParams.height = markerSize
            x.layoutParams.width = markerSize
            x.setColorFilter( Color.parseColor(trackingValues.markerColor))
//and below is the brightIt func

        }
    }
    private fun brightIt(fb: Int): ColorMatrixColorFilter? {
        val cmB = ColorMatrix()
        cmB.set(
            floatArrayOf(
                182f,
                0f,
                0f,
                0f,
                fb.toFloat(),
                0f,
                47f,
                0f,
                1f,
                fb.toFloat(),
                0f,
                0f,
                47f,
                0f,
                fb.toFloat(),
                0f,
                0f,
                0f,
                1f,
                0f
            )
        )
        val colorMatrix = ColorMatrix()
        colorMatrix.set(cmB)
        //Canvas c = new Canvas(b2);
//Paint paint = new Paint();
        //paint.setColorFilter(f);
        return ColorMatrixColorFilter(colorMatrix)
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
        val markerSize: Int = getMarkerSize(trackingValues.markerSize)
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
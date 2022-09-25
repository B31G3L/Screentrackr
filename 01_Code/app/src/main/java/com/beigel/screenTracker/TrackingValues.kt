package com.beigel.screenTracker

import java.io.Serializable

class TrackingValues: Serializable {
    var backgroundColor: String = "#000000"
    var markerColor: String = "#FFFFFF"
    var markerDensity: String = "1"
    var markerSize: String = "1"
    var markerType: String = "Cross"
    var edgeMarker: String = "None"


}
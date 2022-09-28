package com.beigel.screenTracker

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class Trackingscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trackingscreen)
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) actionBar.hide()

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        val previewTrackingBackground: ConstraintLayout = findViewById(R.id.trackingBackground)
        val previewTrackingPoint1_1 :ImageView = findViewById(R.id.trackingPoint_1_1)
        val previewTrackingPoint1_2 :ImageView = findViewById(R.id.trackingPoint_1_2)
        val previewTrackingPoint1_3 :ImageView = findViewById(R.id.trackingPoint_1_3)
        val previewTrackingPoint1_4 :ImageView = findViewById(R.id.trackingPoint_1_4)
        val previewTrackingPoint1_5 :ImageView = findViewById(R.id.trackingPoint_1_5)
        val previewTrackingPoint2_1 :ImageView = findViewById(R.id.trackingPoint_2_1)
        val previewTrackingPoint2_2 :ImageView = findViewById(R.id.trackingPoint_2_2)
        val previewTrackingPoint2_3 :ImageView = findViewById(R.id.trackingPoint_2_3)
        val previewTrackingPoint2_4 :ImageView = findViewById(R.id.trackingPoint_2_4)
        val previewTrackingPoint3_1 :ImageView = findViewById(R.id.trackingPoint_3_1)
        val previewTrackingPoint3_2 :ImageView = findViewById(R.id.trackingPoint_3_2)
        val previewTrackingPoint3_3 :ImageView = findViewById(R.id.trackingPoint_3_3)
        val previewTrackingPoint3_4 :ImageView = findViewById(R.id.trackingPoint_3_4)
        val previewTrackingPointE_1 :ImageView = findViewById(R.id.trackingPoint_E_1)
        val previewTrackingPointE_2 :ImageView = findViewById(R.id.trackingPoint_E_2)
        val previewTrackingPointE_3 :ImageView = findViewById(R.id.trackingPoint_E_3)
        val previewTrackingPointE_4 :ImageView = findViewById(R.id.trackingPoint_E_4)
        val trackingPointList1: ArrayList<ImageView> = ArrayList()
        val trackingPointList2: ArrayList<ImageView> = ArrayList()
        val trackingPointList3: ArrayList<ImageView> = ArrayList()
        val trackingPointListE: ArrayList<ImageView> = ArrayList()

        trackingPointList1.add(previewTrackingPoint1_1)
        trackingPointList1.add(previewTrackingPoint1_2)
        trackingPointList1.add(previewTrackingPoint1_3)
        trackingPointList1.add(previewTrackingPoint1_4)
        trackingPointList1.add(previewTrackingPoint1_5)
        trackingPointList2.add(previewTrackingPoint2_1)
        trackingPointList2.add(previewTrackingPoint2_2)
        trackingPointList2.add(previewTrackingPoint2_3)
        trackingPointList2.add(previewTrackingPoint2_4)
        trackingPointList3.add(previewTrackingPoint3_1)
        trackingPointList3.add(previewTrackingPoint3_2)
        trackingPointList3.add(previewTrackingPoint3_3)
        trackingPointList3.add(previewTrackingPoint3_4)
        trackingPointListE.add(previewTrackingPointE_1)
        trackingPointListE.add(previewTrackingPointE_2)
        trackingPointListE.add(previewTrackingPointE_3)
        trackingPointListE.add(previewTrackingPointE_4)
        for(x in trackingPointList1){
            x.setImageResource(0);
        }
        for(x in trackingPointList2){
            x.setImageResource(0);
        }
        for(x in trackingPointList3){
            x.setImageResource(0);
        }
        for(x in trackingPointListE){
            x.setImageResource(0)
        }
        // get values
        val trackingValues: TrackingValues? = intent.getSerializableExtra("trackingValues") as TrackingValues?
        val utiles:Utilities = Utilities()
        // set background
        if (trackingValues != null) {
            previewTrackingBackground.setBackgroundColor(Color.parseColor(trackingValues.backgroundColor))
            when (trackingValues.markerDensity) {
                "0" -> {}
                "1" -> {
                    utiles.createMarker(trackingPointList1,trackingValues)
                }
                "2" -> {
                    utiles.createMarker(trackingPointList1,trackingValues)
                    utiles.createMarker(trackingPointList2,trackingValues)
                }
                "3" -> {
                    utiles.createMarker(trackingPointList1,trackingValues)
                    utiles.createMarker(trackingPointList2,trackingValues)
                    utiles.createMarker(trackingPointList3,trackingValues)
                }
            }
            when (trackingValues.edgeMarker) {
                "None" -> {}
                "Corner" -> {
                    utiles.createEdgeMarker(trackingPointListE,trackingValues)
                }
                "Semicircle" -> {
                    utiles.createEdgeMarker(trackingPointListE,trackingValues)
                }
            }
        }

    }
}
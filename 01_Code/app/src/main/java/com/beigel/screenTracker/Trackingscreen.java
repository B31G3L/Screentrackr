package com.beigel.screenTracker;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.ArrayList;

public class Trackingscreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackingscreen);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }

        ConstraintLayout previewTrackingBackground = findViewById(R.id.trackingBackground);
        ImageView previewTrackingPoint1_1 = findViewById(R.id.trackingPoint_1_1);
        ImageView previewTrackingPoint1_2 = findViewById(R.id.trackingPoint_1_2);
        ImageView previewTrackingPoint1_3 = findViewById(R.id.trackingPoint_1_3);
        ImageView previewTrackingPoint1_4 = findViewById(R.id.trackingPoint_1_4);
        ImageView previewTrackingPoint1_5 = findViewById(R.id.trackingPoint_1_5);
        ImageView previewTrackingPoint2_1 = findViewById(R.id.trackingPoint_2_1);
        ImageView previewTrackingPoint2_2 = findViewById(R.id.trackingPoint_2_2);
        ImageView previewTrackingPoint2_3 = findViewById(R.id.trackingPoint_2_3);
        ImageView previewTrackingPoint2_4 = findViewById(R.id.trackingPoint_2_4);
        ImageView previewTrackingPoint3_1 = findViewById(R.id.trackingPoint_3_1);
        ImageView previewTrackingPoint3_2 = findViewById(R.id.trackingPoint_3_2);
        ImageView previewTrackingPoint3_3 = findViewById(R.id.trackingPoint_3_3);
        ImageView previewTrackingPoint3_4 = findViewById(R.id.trackingPoint_3_4);
        ImageView previewTrackingPointE_1 = findViewById(R.id.trackingPoint_E_1);
        ImageView previewTrackingPointE_2 = findViewById(R.id.trackingPoint_E_2);
        ImageView previewTrackingPointE_3 = findViewById(R.id.trackingPoint_E_3);
        ImageView previewTrackingPointE_4 = findViewById(R.id.trackingPoint_E_4);

        ArrayList<ImageView> trackingPointList1 = new ArrayList<>();
        ArrayList<ImageView> trackingPointList2 = new ArrayList<>();
        ArrayList<ImageView> trackingPointList3 = new ArrayList<>();
        ArrayList<ImageView> trackingPointListE = new ArrayList<>();

        trackingPointList1.add(previewTrackingPoint1_1);
        trackingPointList1.add(previewTrackingPoint1_2);
        trackingPointList1.add(previewTrackingPoint1_3);
        trackingPointList1.add(previewTrackingPoint1_4);
        trackingPointList1.add(previewTrackingPoint1_5);
        trackingPointList2.add(previewTrackingPoint2_1);
        trackingPointList2.add(previewTrackingPoint2_2);
        trackingPointList2.add(previewTrackingPoint2_3);
        trackingPointList2.add(previewTrackingPoint2_4);
        trackingPointList3.add(previewTrackingPoint3_1);
        trackingPointList3.add(previewTrackingPoint3_2);
        trackingPointList3.add(previewTrackingPoint3_3);
        trackingPointList3.add(previewTrackingPoint3_4);
        trackingPointListE.add(previewTrackingPointE_1);
        trackingPointListE.add(previewTrackingPointE_2);
        trackingPointListE.add(previewTrackingPointE_3);
        trackingPointListE.add(previewTrackingPointE_4);

        for (ImageView x : trackingPointList1) {
            x.setImageResource(0);
        }
        for (ImageView x : trackingPointList2) {
            x.setImageResource(0);
        }
        for (ImageView x : trackingPointList3) {
            x.setImageResource(0);
        }
        for (ImageView x : trackingPointListE) {
            x.setImageResource(0);
        }

        // Get values
        TrackingValues trackingValues = (TrackingValues) getIntent().getSerializableExtra("trackingValues");
        Utilities utilities = new Utilities();

        // Set background
        if (trackingValues != null) {
            previewTrackingBackground.setBackgroundColor(Color.parseColor(trackingValues.getBackgroundColor()));

            switch (trackingValues.getMarkerDensity()) {
                case "0":
                    break;
                case "1":
                    utilities.createMarker(trackingPointList1, trackingValues);
                    break;
                case "2":
                    utilities.createMarker(trackingPointList1, trackingValues);
                    utilities.createMarker(trackingPointList2, trackingValues);
                    break;
                case "3":
                    utilities.createMarker(trackingPointList1, trackingValues);
                    utilities.createMarker(trackingPointList2, trackingValues);
                    utilities.createMarker(trackingPointList3, trackingValues);
                    break;
            }

            switch (trackingValues.getEdgeMarker()) {
                case "None":
                    break;
                case "Corner":
                case "Semicircle":
                    utilities.createEdgeMarker(trackingPointListE, trackingValues);
                    break;
            }
        }
    }
}
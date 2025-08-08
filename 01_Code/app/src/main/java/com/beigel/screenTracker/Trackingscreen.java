package com.beigel.screenTracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.beigel.screenTracker.databinding.ActivityTrackingscreenBinding;

import java.util.ArrayList;

/**
 * Vollbildschirm-Tracking-Ansicht mit Scroll-Marker-Unterstützung
 */
public class Trackingscreen extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private ActivityTrackingscreenBinding binding;
    private TrackingValues trackingValues;
    private GestureDetectorCompat gestureDetector;

    private ArrayList<ImageView> trackingPointList1;
    private ArrayList<ImageView> trackingPointList2;
    private ArrayList<ImageView> trackingPointList3;
    private ArrayList<ImageView> trackingPointListE;
    private ArrayList<ImageView> trackingPointListSV; // Scroll Vertical
    private ArrayList<ImageView> trackingPointListSH; // Scroll Horizontal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding initialisieren
        binding = ActivityTrackingscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ActionBar ausblenden
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Systemleisten ausblenden für Vollbildmodus
        hideSystemBars();

        // Gesten-Erkennung für Navigation
        gestureDetector = new GestureDetectorCompat(this, this);

        // Marker-Listen initialisieren
        initializeMarkerLists();

        // Tracking-Werte aus dem Intent laden
        loadTrackingValues();

        // Tracking-Ansicht erstellen
        setupTracking();
    }

    /**
     * Blendet die Systemleisten aus für einen echten Vollbildmodus
     */
    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        if (windowInsetsController != null) {
            // Systemleisten können durch Wischen wieder eingeblendet werden
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

            // Alle Systemleisten ausblenden
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }
    }

    /**
     * Initialisiert die Listen für die verschiedenen Marker-Gruppen
     */
    private void initializeMarkerLists() {
        trackingPointList1 = new ArrayList<>();
        trackingPointList2 = new ArrayList<>();
        trackingPointList3 = new ArrayList<>();
        trackingPointListE = new ArrayList<>();
        trackingPointListSV = new ArrayList<>();
        trackingPointListSH = new ArrayList<>();

        // Gruppe 1
        trackingPointList1.add(binding.trackingPoint11);
        trackingPointList1.add(binding.trackingPoint12);
        trackingPointList1.add(binding.trackingPoint13);
        trackingPointList1.add(binding.trackingPoint14);
        trackingPointList1.add(binding.trackingPoint15);

        // Gruppe 2
        trackingPointList2.add(binding.trackingPoint21);
        trackingPointList2.add(binding.trackingPoint22);
        trackingPointList2.add(binding.trackingPoint23);
        trackingPointList2.add(binding.trackingPoint24);

        // Gruppe 3
        trackingPointList3.add(binding.trackingPoint31);
        trackingPointList3.add(binding.trackingPoint32);
        trackingPointList3.add(binding.trackingPoint33);
        trackingPointList3.add(binding.trackingPoint34);

        // Eckmarker
        trackingPointListE.add(binding.trackingPointE1);
        trackingPointListE.add(binding.trackingPointE2);
        trackingPointListE.add(binding.trackingPointE3);
        trackingPointListE.add(binding.trackingPointE4);

        // Scroll-Marker Vertikal
        trackingPointListSV.add(binding.trackingPointSV1);
        trackingPointListSV.add(binding.trackingPointSV2);
        trackingPointListSV.add(binding.trackingPointSV3);
        trackingPointListSV.add(binding.trackingPointSV4);

        // Scroll-Marker Horizontal
        trackingPointListSH.add(binding.trackingPointSH1);
        trackingPointListSH.add(binding.trackingPointSH2);
        trackingPointListSH.add(binding.trackingPointSH3);
        trackingPointListSH.add(binding.trackingPointSH4);
    }

    /**
     * Lädt die Tracking-Einstellungen aus dem Intent
     */
    private void loadTrackingValues() {
        // Tracking-Werte aus dem Intent extrahieren
        trackingValues = (TrackingValues) getIntent().getSerializableExtra("trackingValues");

        // Fallback zu Standardwerten, falls keine Werte übergeben wurden
        if (trackingValues == null) {
            trackingValues = new TrackingValues();
        }
    }

    /**
     * Richtet die Tracking-Ansicht gemäß den Einstellungen ein
     */
    private void setupTracking() {
        // Alle Marker zurücksetzen
        resetAllMarkers();

        // Hintergrundfarbe setzen
        binding.trackingBackground.setBackgroundColor(
                Color.parseColor(trackingValues.getBackgroundColor()));

        // Marker entsprechend der ausgewählten Dichte erstellen
        setupMarkers();

        // Eckmarker erstellen, falls ausgewählt
        setupEdgeMarkers();

        // Scroll-Marker erstellen, falls ausgewählt
        setupScrollMarkers();
    }

    /**
     * Setzt alle Marker zurück
     */
    private void resetAllMarkers() {
        // Alle Marker-Bilder zurücksetzen
        for (ImageView marker : trackingPointList1) {
            marker.setImageResource(0);
        }
        for (ImageView marker : trackingPointList2) {
            marker.setImageResource(0);
        }
        for (ImageView marker : trackingPointList3) {
            marker.setImageResource(0);
        }
        for (ImageView marker : trackingPointListE) {
            marker.setImageResource(0);
        }
        for (ImageView marker : trackingPointListSV) {
            marker.setImageResource(0);
        }
        for (ImageView marker : trackingPointListSH) {
            marker.setImageResource(0);
        }
    }

    /**
     * Erstellt Marker entsprechend der ausgewählten Dichte
     */
    private void setupMarkers() {
        int markerDensity = trackingValues.getMarkerDensity();

        switch (markerDensity) {
            case 0:
                // Keine Marker anzeigen
                break;
            case 1:
                // Nur die erste Marker-Gruppe anzeigen
                Utilities.createMarker(trackingPointList1, trackingValues);
                break;
            case 2:
                // Erste und zweite Marker-Gruppe anzeigen
                Utilities.createMarker(trackingPointList1, trackingValues);
                Utilities.createMarker(trackingPointList2, trackingValues);
                break;
            case 3:
                // Alle Marker-Gruppen anzeigen
                Utilities.createMarker(trackingPointList1, trackingValues);
                Utilities.createMarker(trackingPointList2, trackingValues);
                Utilities.createMarker(trackingPointList3, trackingValues);
                break;
        }
    }

    /**
     * Erstellt Eckmarker, falls in den Einstellungen aktiviert
     */
    private void setupEdgeMarkers() {
        if (trackingValues.getEdgeMarker() != TrackingValues.EdgeMarkerType.NONE) {
            Utilities.createEdgeMarker(trackingPointListE, trackingValues);
        }
    }

    /**
     * Erstellt Scroll-Marker, falls in den Einstellungen aktiviert
     */
    private void setupScrollMarkers() {
        if (trackingValues.getScrollMarker() != TrackingValues.ScrollMarkerType.NONE) {
            Utilities.createScrollMarker(trackingPointListSV, trackingPointListSH, trackingValues);
        }
    }

    // GestureDetector.OnGestureListener Implementierung

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {
        // Nicht verwendet
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        // Tap kann zum Beenden verwendet werden
        finish();
        return true;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        // Nicht verwendet
    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        // Fling kann zur Navigation verwendet werden
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Touch-Events an den GestureDetector weiterleiten
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
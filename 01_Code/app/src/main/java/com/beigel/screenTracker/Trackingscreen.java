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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.beigel.screenTracker.databinding.ActivityTrackingscreenBinding;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Vollbildschirm-Tracking-Ansicht mit einfachem unendlichem Scrolling
 */
public class Trackingscreen extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private ActivityTrackingscreenBinding binding;
    private TrackingValues trackingValues;
    private GestureDetectorCompat gestureDetector;

    // Statische Marker (Original-Layout)
    private ArrayList<ImageView> trackingPointList1;
    private ArrayList<ImageView> trackingPointList2;
    private ArrayList<ImageView> trackingPointList3;
    private ArrayList<ImageView> trackingPointListE;

    // Dynamische Scroll-Marker
    private ArrayList<DynamicMarker> dynamicScrollMarkers;
    private ConstraintLayout scrollMarkerLayer;

    // Scroll-Tracking
    private float totalScrollX = 0f;
    private float totalScrollY = 0f;
    private int screenWidth;
    private int screenHeight;

    // Basis-Positionen der 4 Start-Marker (näher zur Mitte)
    private static final float[] BASE_POSITIONS_X = {-0.15f, 0.15f, 0.15f, -0.15f}; // Näher zur Mitte
    private static final float[] BASE_POSITIONS_Y = {-0.15f, -0.15f, 0.15f, 0.15f}; // Näher zur Mitte
    private static final int MARKER_SPACING = 500; // Größerer Abstand zwischen Markern in px
    private static final int MAX_MARKERS = 100; // Mehr Marker für unendliches Scrollen
    private static final int CLEANUP_DISTANCE = MARKER_SPACING * 3; // Entfernungsgrenze für Cleanup

    // Momentum Scrolling
    private float velocityX = 0f;
    private float velocityY = 0f;
    private static final float MOMENTUM_DECAY = 0.95f; // Abbremsung (näher zu 1 = langsamer Stopp)
    private static final float MIN_VELOCITY = 5f; // Mindestgeschwindigkeit bevor Stopp
    private boolean isMomentumScrolling = false;
    private final android.os.Handler momentumHandler = new android.os.Handler();

    /**
     * Klasse für dynamische Marker mit Position
     */
    private static class DynamicMarker {
        ImageView imageView;
        float worldX, worldY; // Position in der virtuellen Welt
        int markerIndex; // Welcher der 4 Basis-Marker (0-3)

        DynamicMarker(ImageView imageView, float worldX, float worldY, int markerIndex) {
            this.imageView = imageView;
            this.worldX = worldX;
            this.worldY = worldY;
            this.markerIndex = markerIndex;
        }
    }

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

        // Scroll-Marker Layer Referenz
        scrollMarkerLayer = binding.scrollMarkerLayer;

        // Dynamische Marker initialisieren
        dynamicScrollMarkers = new ArrayList<>();

        // Marker-Listen initialisieren
        initializeMarkerLists();

        // Tracking-Werte aus dem Intent laden
        loadTrackingValues();

        // Bildschirmgröße ermitteln
        getScreenDimensions();

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
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }
    }

    /**
     * Ermittelt die Bildschirmabmessungen
     */
    private void getScreenDimensions() {
        getWindow().getDecorView().post(() -> {
            screenWidth = getWindow().getDecorView().getWidth();
            screenHeight = getWindow().getDecorView().getHeight();

            // Nach dem Ermitteln der Bildschirmgröße die initialen Marker erstellen
            if (trackingValues.getScrollMarker() != TrackingValues.ScrollMarkerType.NONE) {
                createInitialScrollMarkers();
            }
        });
    }

    /**
     * Initialisiert die Listen für die statischen Marker-Gruppen
     */
    private void initializeMarkerLists() {
        trackingPointList1 = new ArrayList<>();
        trackingPointList2 = new ArrayList<>();
        trackingPointList3 = new ArrayList<>();
        trackingPointListE = new ArrayList<>();

        // Gruppe 1 (Haupt-Layer)
        trackingPointList1.add(binding.trackingPoint11);
        trackingPointList1.add(binding.trackingPoint12);
        trackingPointList1.add(binding.trackingPoint13);
        trackingPointList1.add(binding.trackingPoint14);
        trackingPointList1.add(binding.trackingPoint15);

        // Gruppe 2 (Haupt-Layer)
        trackingPointList2.add(binding.trackingPoint21);
        trackingPointList2.add(binding.trackingPoint22);
        trackingPointList2.add(binding.trackingPoint23);
        trackingPointList2.add(binding.trackingPoint24);

        // Gruppe 3 (Haupt-Layer)
        trackingPointList3.add(binding.trackingPoint31);
        trackingPointList3.add(binding.trackingPoint32);
        trackingPointList3.add(binding.trackingPoint33);
        trackingPointList3.add(binding.trackingPoint34);

        // Eckmarker (Haupt-Layer)
        trackingPointListE.add(binding.trackingPointE1);
        trackingPointListE.add(binding.trackingPointE2);
        trackingPointListE.add(binding.trackingPointE3);
        trackingPointListE.add(binding.trackingPointE4);
    }

    /**
     * Lädt die Tracking-Einstellungen aus dem Intent
     */
    private void loadTrackingValues() {
        trackingValues = (TrackingValues) getIntent().getSerializableExtra("trackingValues");
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

        // Statische Marker entsprechend der ausgewählten Dichte erstellen
        setupStaticMarkers();

        // Eckmarker erstellen, falls ausgewählt
        setupEdgeMarkers();

        // Scroll-System initialisieren
        setupScrollMarkers();
    }

    /**
     * Setzt alle Marker zurück
     */
    private void resetAllMarkers() {
        // Statische Marker zurücksetzen
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

        // Dynamische Marker zurücksetzen
        clearDynamicMarkers();
        scrollMarkerLayer.setVisibility(View.GONE);
    }

    /**
     * Erstellt statische Marker entsprechend der ausgewählten Dichte
     */
    private void setupStaticMarkers() {
        int markerDensity = trackingValues.getMarkerDensity();

        switch (markerDensity) {
            case 0:
                break;
            case 1:
                Utilities.createMarker(trackingPointList1, trackingValues);
                break;
            case 2:
                Utilities.createMarker(trackingPointList1, trackingValues);
                Utilities.createMarker(trackingPointList2, trackingValues);
                break;
            case 3:
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
     * Initialisiert das Scroll-Marker System
     */
    private void setupScrollMarkers() {
        TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

        if (scrollType == TrackingValues.ScrollMarkerType.NONE) {
            scrollMarkerLayer.setVisibility(View.GONE);
            return;
        }

        scrollMarkerLayer.setVisibility(View.VISIBLE);

        // Initiale Marker werden nach Bildschirmgröße-Ermittlung erstellt
    }

    /**
     * Erstellt die 4 initialen Scroll-Marker an den Basis-Positionen
     */
    private void createInitialScrollMarkers() {
        for (int i = 0; i < 4; i++) {
            float worldX = BASE_POSITIONS_X[i] * screenWidth;
            float worldY = BASE_POSITIONS_Y[i] * screenHeight;
            createDynamicMarker(worldX, worldY, i);
        }
    }

    /**
     * Erstellt einen neuen dynamischen Marker an der angegebenen Weltposition
     */
    private void createDynamicMarker(float worldX, float worldY, int markerIndex) {
        // Stoppe wenn maximale Anzahl erreicht ist
        if (dynamicScrollMarkers.size() >= MAX_MARKERS) {
            return; // Vereinfacht: Stoppe einfach, ohne zu entfernen
        }

        // Debug: Marker-Erstellung ausgeben
        System.out.println("Creating marker at: " + worldX + ", " + worldY + " (Total: " + (dynamicScrollMarkers.size() + 1) + ")");

        // Neuen ImageView erstellen
        ImageView marker = new ImageView(this);

        // Marker-Eigenschaften setzen
        int markerType = getMarkerDrawableResource();
        int markerSize = Utilities.getMarkerSize(trackingValues.getMarkerSize());
        int markerColor = Color.parseColor(trackingValues.getMarkerColor());

        marker.setImageResource(markerType);
        marker.setColorFilter(markerColor);

        // Layout-Parameter setzen
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(markerSize, markerSize);
        marker.setLayoutParams(params);

        // Zur ScrollLayer hinzufügen
        scrollMarkerLayer.addView(marker);

        // Position berechnen und setzen
        updateMarkerPosition(marker, worldX, worldY);

        // Zu Liste hinzufügen
        dynamicScrollMarkers.add(new DynamicMarker(marker, worldX, worldY, markerIndex));
    }

    /**
     * Aktualisiert die Position eines Markers basierend auf der Weltposition und aktuellen Scroll-Offset
     */
    private void updateMarkerPosition(ImageView marker, float worldX, float worldY) {
        float screenX = worldX - totalScrollX + screenWidth / 2f;
        float screenY = worldY - totalScrollY + screenHeight / 2f;

        marker.setX(screenX);
        marker.setY(screenY);
    }

    /**
     * Aktualisiert alle dynamischen Marker basierend auf der neuen Scroll-Position
     */
    private void updateDynamicMarkers() {
        // Alle bestehenden Marker aktualisieren
        for (DynamicMarker dynamicMarker : dynamicScrollMarkers) {
            updateMarkerPosition(dynamicMarker.imageView, dynamicMarker.worldX, dynamicMarker.worldY);
        }

        // Marker außerhalb des Bildschirms entfernen
        removeFarMarkers();

        // Neue Marker hinzufügen wo nötig
        addNewMarkers();
    }

    /**
     * Entfernt Marker, die zu weit vom Bildschirm entfernt sind
     */
    private void removeFarMarkers() {
        Iterator<DynamicMarker> iterator = dynamicScrollMarkers.iterator();
        while (iterator.hasNext()) {
            DynamicMarker marker = iterator.next();

            float screenX = marker.worldX - totalScrollX + screenWidth / 2f;
            float screenY = marker.worldY - totalScrollY + screenHeight / 2f;

            // Entferne Marker, die sehr weit außerhalb des Bildschirms sind
            if (screenX < -CLEANUP_DISTANCE || screenX > screenWidth + CLEANUP_DISTANCE ||
                    screenY < -CLEANUP_DISTANCE || screenY > screenHeight + CLEANUP_DISTANCE) {

                scrollMarkerLayer.removeView(marker.imageView);
                iterator.remove();
            }
        }
    }

    /**
     * Fügt neue Marker hinzu basierend auf der Scroll-Richtung
     */
    private void addNewMarkers() {
        TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

        // Für jeden der 4 Basis-Marker prüfen, ob neue Marker hinzugefügt werden müssen
        for (int i = 0; i < 4; i++) {
            checkAndAddMarkersForBase(i, scrollType);
        }
    }

    /**
     * Prüft und fügt neue Marker für eine bestimmte Basis-Position hinzu
     */
    private void checkAndAddMarkersForBase(int baseIndex, TrackingValues.ScrollMarkerType scrollType) {
        float baseWorldX = BASE_POSITIONS_X[baseIndex] * screenWidth;
        float baseWorldY = BASE_POSITIONS_Y[baseIndex] * screenHeight;

        switch (scrollType) {
            case VERTICAL:
                // Vertikal scrollen - neue Marker oben und unten hinzufügen
                addMarkersInLine(baseWorldX, baseWorldY, baseIndex, 0, MARKER_SPACING); // Vertikal
                break;

            case HORIZONTAL:
                // Horizontal scrollen - neue Marker links und rechts hinzufügen
                addMarkersInLine(baseWorldX, baseWorldY, baseIndex, MARKER_SPACING, 0); // Horizontal
                break;
        }
    }

    /**
     * Fügt Marker in einer Linie hinzu (vereinfacht)
     */
    private void addMarkersInLine(float baseX, float baseY, int baseIndex, float deltaX, float deltaY) {
        // Berechne aktuelle Scroll-Position
        float currentScrollX = totalScrollX;
        float currentScrollY = totalScrollY;

        // Berechne wie viele Marker wir in jede Richtung brauchen
        int markersNeeded = 5; // Anzahl Marker in jede Richtung

        // Positive Richtung (rechts/unten)
        for (int i = 1; i <= markersNeeded; i++) {
            float newWorldX = baseX + deltaX * i;
            float newWorldY = baseY + deltaY * i;

            if (isMarkerNeeded(newWorldX, newWorldY, currentScrollX, currentScrollY)) {
                if (!markerExistsAt(newWorldX, newWorldY)) {
                    createDynamicMarker(newWorldX, newWorldY, baseIndex);
                }
            }
        }

        // Negative Richtung (links/oben)
        for (int i = 1; i <= markersNeeded; i++) {
            float newWorldX = baseX - deltaX * i;
            float newWorldY = baseY - deltaY * i;

            if (isMarkerNeeded(newWorldX, newWorldY, currentScrollX, currentScrollY)) {
                if (!markerExistsAt(newWorldX, newWorldY)) {
                    createDynamicMarker(newWorldX, newWorldY, baseIndex);
                }
            }
        }
    }

    /**
     * Prüft ob ein Marker an dieser Position benötigt wird
     */
    private boolean isMarkerNeeded(float worldX, float worldY, float scrollX, float scrollY) {
        // Berechne Screen-Position relativ zur aktuellen Scroll-Position
        float screenX = worldX - scrollX + screenWidth / 2f;
        float screenY = worldY - scrollY + screenHeight / 2f;

        // Marker wird benötigt wenn er im erweiterten sichtbaren Bereich liegt
        int buffer = MARKER_SPACING * 2; // Größerer Puffer für smooth scrolling
        return screenX >= -buffer && screenX <= screenWidth + buffer &&
                screenY >= -buffer && screenY <= screenHeight + buffer;
    }

    /**
     * Prüft ob bereits ein Marker an der angegebenen Position existiert
     */
    private boolean markerExistsAt(float worldX, float worldY) {
        float tolerance = MARKER_SPACING / 4f; // Toleranz basierend auf Marker-Abstand

        for (DynamicMarker marker : dynamicScrollMarkers) {
            if (Math.abs(marker.worldX - worldX) < tolerance && Math.abs(marker.worldY - worldY) < tolerance) {
                return true;
            }
        }
        return false;
    }

    /**
     * Entfernt alle dynamischen Marker
     */
    private void clearDynamicMarkers() {
        for (DynamicMarker marker : dynamicScrollMarkers) {
            scrollMarkerLayer.removeView(marker.imageView);
        }
        dynamicScrollMarkers.clear();
    }

    /**
     * Bestimmt den Drawable-Ressourcen-ID basierend auf dem Marker-Typ
     */
    private int getMarkerDrawableResource() {
        switch (trackingValues.getMarkerType()) {
            case PIE:
                return R.drawable.ic_marker_pie;
            case CIRCLE:
                return R.drawable.ic_marker_circle;
            case TRIANGLE:
                return R.drawable.ic_marker_triangle;
            case CROSS:
            default:
                return R.drawable.ic_marker_cross;
        }
    }

    // GestureDetector.OnGestureListener Implementierung

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        // Stoppe Momentum-Scrolling wenn User wieder berührt
        stopMomentumScrolling();
        return true;
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
        TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

        if (scrollType == TrackingValues.ScrollMarkerType.NONE) {
            return false;
        }

        // Stoppe eventuelles Momentum-Scrolling
        stopMomentumScrolling();

        // Scroll-Position aktualisieren
        switch (scrollType) {
            case VERTICAL:
                totalScrollY += distanceY;
                // Geschwindigkeit für Momentum trackieren (invertiert weil distanceY umgekehrt ist)
                velocityY = -distanceY * 2; // Verstärkung für besseres Momentum
                break;
            case HORIZONTAL:
                totalScrollX += distanceX;
                // Geschwindigkeit für Momentum trackieren (invertiert weil distanceX umgekehrt ist)
                velocityX = -distanceX * 2; // Verstärkung für besseres Momentum
                break;
        }

        // Debug: Scroll-Position ausgeben
        System.out.println("Scroll - X: " + totalScrollX + ", Y: " + totalScrollY + ", VelX: " + velocityX + ", VelY: " + velocityY);

        // Dynamische Marker aktualisieren
        updateDynamicMarkers();

        return true;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        // Nicht verwendet
    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

        if (scrollType == TrackingValues.ScrollMarkerType.NONE) {
            return false;
        }

        // Setze Momentum-Geschwindigkeiten basierend auf Fling
        switch (scrollType) {
            case VERTICAL:
                this.velocityY = velocityY / 10f; // Skaliere die Geschwindigkeit runter
                break;
            case HORIZONTAL:
                this.velocityX = velocityX / 10f; // Skaliere die Geschwindigkeit runter
                break;
        }

        // Starte Momentum-Scrolling
        startMomentumScrolling();

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean gestureResult = gestureDetector.onTouchEvent(event);

        // Prüfe auf ACTION_UP um Momentum-Scrolling zu starten
        if (event.getAction() == MotionEvent.ACTION_UP) {
            TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

            if (scrollType != TrackingValues.ScrollMarkerType.NONE) {
                // Starte Momentum-Scrolling mit aktueller Geschwindigkeit
                if (Math.abs(velocityX) > MIN_VELOCITY || Math.abs(velocityY) > MIN_VELOCITY) {
                    startMomentumScrolling();
                }
            }
        }

        return gestureResult || super.onTouchEvent(event);
    }

    /**
     * Startet das Momentum-Scrolling
     */
    private void startMomentumScrolling() {
        if (isMomentumScrolling) {
            return; // Bereits aktiv
        }

        isMomentumScrolling = true;
        momentumHandler.post(momentumRunnable);

        System.out.println("Starting momentum scrolling - VelX: " + velocityX + ", VelY: " + velocityY);
    }

    /**
     * Stoppt das Momentum-Scrolling
     */
    private void stopMomentumScrolling() {
        if (isMomentumScrolling) {
            isMomentumScrolling = false;
            momentumHandler.removeCallbacks(momentumRunnable);
            System.out.println("Stopping momentum scrolling");
        }
    }

    /**
     * Runnable für Momentum-Scrolling
     */
    private final Runnable momentumRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isMomentumScrolling) {
                return;
            }

            TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();
            boolean hasMovement = false;

            // Aktualisiere Position basierend auf Geschwindigkeit
            switch (scrollType) {
                case VERTICAL:
                    if (Math.abs(velocityY) > MIN_VELOCITY) {
                        totalScrollY += velocityY;
                        velocityY *= MOMENTUM_DECAY; // Abbremsung
                        hasMovement = true;
                    }
                    break;
                case HORIZONTAL:
                    if (Math.abs(velocityX) > MIN_VELOCITY) {
                        totalScrollX += velocityX;
                        velocityX *= MOMENTUM_DECAY; // Abbremsung
                        hasMovement = true;
                    }
                    break;
            }

            if (hasMovement) {
                // Marker aktualisieren
                updateDynamicMarkers();

                // Für nächsten Frame planen (60 FPS = 16ms)
                momentumHandler.postDelayed(this, 16);
            } else {
                // Momentum-Scrolling beenden
                stopMomentumScrolling();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        stopMomentumScrolling(); // Momentum-Scrolling pausieren
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMomentumScrolling(); // Momentum-Scrolling beenden
        clearDynamicMarkers();
        binding = null;
    }
}
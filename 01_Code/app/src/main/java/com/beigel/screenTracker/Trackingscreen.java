package com.beigel.screenTracker;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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
 * Aufgeräumte Vollbildschirm-Tracking-Ansicht
 * - Verwendet AppConstants für alle Magic Numbers
 * - Verbessertes Error Handling und Logging
 * - Saubere Trennung der Verantwortlichkeiten
 * - Bessere Performance durch optimierte Marker-Verwaltung
 */
public class Trackingscreen extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = AppConstants.LogTags.TRACKING;

    // UI Components
    private ActivityTrackingscreenBinding binding;
    private GestureDetectorCompat gestureDetector;

    // Core Data
    private TrackingValues trackingValues;

    // Marker Management
    private StaticMarkerManager staticMarkerManager;
    private DynamicMarkerManager dynamicMarkerManager;

    // Screen Properties
    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Trackingscreen onCreate gestartet");

        try {
            initializeUI();
            loadTrackingValues();
            initializeManagers();
            setupFullscreen();
            setupTracking();

            Log.d(TAG, "Trackingscreen erfolgreich initialisiert");
        } catch (Exception e) {
            Log.e(TAG, "Fehler bei Trackingscreen Initialisierung", e);
            finish();
        }
    }

    // ========== INITIALIZATION ==========

    /**
     * Initialisiert die UI-Komponenten
     */
    private void initializeUI() {
        binding = ActivityTrackingscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Gesten-Erkennung initialisieren
        gestureDetector = new GestureDetectorCompat(this, this);

        Log.d(TAG, "UI-Komponenten initialisiert");
    }

    /**
     * Lädt die TrackingValues aus dem Intent
     */
    private void loadTrackingValues() {
        trackingValues = (TrackingValues) getIntent().getSerializableExtra("trackingValues");
        if (trackingValues == null) {
            Log.w(TAG, "Keine TrackingValues im Intent gefunden, verwende Standardwerte");
            trackingValues = new TrackingValues();
        } else {
            Log.d(TAG, "TrackingValues geladen: " + trackingValues.toString());
        }
    }

    /**
     * Initialisiert die Manager-Klassen
     */
    private void initializeManagers() {
        staticMarkerManager = new StaticMarkerManager(binding, trackingValues);
        dynamicMarkerManager = new DynamicMarkerManager(binding.scrollMarkerLayer, trackingValues);

        Log.d(TAG, "Manager-Klassen initialisiert");
    }

    /**
     * Konfiguriert den Vollbildmodus
     */
    private void setupFullscreen() {
        // ActionBar ausblenden
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Systemleisten ausblenden
        hideSystemBars();

        // Bildschirmgröße ermitteln
        getScreenDimensions();

        Log.d(TAG, "Vollbildmodus konfiguriert");
    }

    /**
     * Blendet die Systemleisten aus
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

            Log.d(TAG, String.format("Bildschirmgröße: %dx%d", screenWidth, screenHeight));

            // Nach Bildschirmgröße-Ermittlung: dynamische Marker initialisieren
            dynamicMarkerManager.initializeWithScreenSize(screenWidth, screenHeight);

            // ✅ NEU: Scroll-Marker erst NACH der Bildschirmgröße-Ermittlung einrichten
            dynamicMarkerManager.setupScrollMarkers();
        });
    }
    /**
     * Richtet das Tracking-System ein
     */
    private void setupTracking() {
        try {
            // Hintergrundfarbe setzen
            int bgColor = Utilities.parseColorSafely(trackingValues.getBackgroundColor());
            binding.trackingBackground.setBackgroundColor(bgColor);

            // Statische Marker erstellen
            staticMarkerManager.setupMarkers();


            Log.d(TAG, "Tracking-System erfolgreich eingerichtet");
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Einrichten des Tracking-Systems", e);
        }
    }

    // ========== GESTURE DETECTION ==========

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {
        // Nicht verwendet
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        // Tap beendet das Tracking
        Log.d(TAG, "Single Tap erkannt - beende Tracking");
        finish();
        return true;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        try {
            boolean handled = dynamicMarkerManager.handleScroll(distanceX, distanceY);
            if (handled) {
                Log.v(TAG, String.format("Scroll: dX=%.2f, dY=%.2f", distanceX, distanceY));
            }
            return handled;
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Scroll-Handling", e);
            return false;
        }
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        // Nicht verwendet
    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        try {
            boolean handled = dynamicMarkerManager.handleFling(velocityX, velocityY);
            if (handled) {
                Log.d(TAG, String.format("Fling: vX=%.2f, vY=%.2f", velocityX, velocityY));
            }
            return handled;
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Fling-Handling", e);
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    // ========== LIFECYCLE ==========

    @Override
    protected void onPause() {
        super.onPause();
        try {
            dynamicMarkerManager.pauseAnimations();
            Log.d(TAG, "Animationen pausiert");
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Pausieren", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (dynamicMarkerManager != null) {
                dynamicMarkerManager.cleanup();
            }
            binding = null;
            Log.d(TAG, "Trackingscreen aufgeräumt");
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Aufräumen", e);
        }
    }

    // ========== MANAGER CLASSES ==========

    /**
     * Verwaltet statische Marker (normale Tracking-Punkte und Eckmarker)
     */
    private static class StaticMarkerManager {
        private final ActivityTrackingscreenBinding binding;
        private final TrackingValues trackingValues;

        private ArrayList<ImageView> trackingPointList1;
        private ArrayList<ImageView> trackingPointList2;
        private ArrayList<ImageView> trackingPointList3;
        private ArrayList<ImageView> trackingPointListE;

        public StaticMarkerManager(@NonNull ActivityTrackingscreenBinding binding,
                                   @NonNull TrackingValues trackingValues) {
            this.binding = binding;
            this.trackingValues = trackingValues;
            initializeMarkerLists();
        }

        /**
         * Initialisiert die Marker-Listen
         */
        private void initializeMarkerLists() {
            trackingPointList1 = new ArrayList<>();
            trackingPointList2 = new ArrayList<>();
            trackingPointList3 = new ArrayList<>();
            trackingPointListE = new ArrayList<>();

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
        }

        /**
         * Richtet alle statischen Marker ein
         */
        public void setupMarkers() {
            clearAllMarkers();

            // Statische Marker basierend auf Dichte
            int density = trackingValues.getMarkerDensity();
            switch (density) {
                case 0:
                    Log.d(TAG, "Keine statischen Marker (Dichte 0)");
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

            // Eckmarker wenn aktiviert
            if (trackingValues.getEdgeMarker() != TrackingValues.EdgeMarkerType.NONE) {
                Utilities.createEdgeMarker(trackingPointListE, trackingValues);
            }

            Log.d(TAG, "Statische Marker eingerichtet (Dichte: " + density + ")");
        }

        /**
         * Entfernt alle statischen Marker
         */
        private void clearAllMarkers() {
            clearMarkerGroup(trackingPointList1);
            clearMarkerGroup(trackingPointList2);
            clearMarkerGroup(trackingPointList3);
            clearMarkerGroup(trackingPointListE);
        }

        private void clearMarkerGroup(@NonNull ArrayList<ImageView> markerGroup) {
            for (ImageView marker : markerGroup) {
                if (marker != null) {
                    marker.setImageResource(0);
                    marker.clearColorFilter();
                }
            }
        }
    }

    /**
     * Verwaltet dynamische Scroll-Marker mit verbessertem unendlichen Scrolling
     */
    private static class DynamicMarkerManager {
        private final ConstraintLayout scrollMarkerLayer;
        private final TrackingValues trackingValues;

        private ArrayList<DynamicMarker> dynamicScrollMarkers;
        private ValueAnimator momentumAnimator;

        private float totalScrollX = 0f;
        private float totalScrollY = 0f;
        private int screenWidth;
        private int screenHeight;
        private boolean isMomentumScrolling = false;

        public DynamicMarkerManager(@NonNull ConstraintLayout scrollMarkerLayer,
                                    @NonNull TrackingValues trackingValues) {
            this.scrollMarkerLayer = scrollMarkerLayer;
            this.trackingValues = trackingValues;
            this.dynamicScrollMarkers = new ArrayList<>();
        }

        public void initializeWithScreenSize(int screenWidth, int screenHeight) {
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
        }

        /**
         * Richtet das Scroll-Marker System ein
         */
        public void setupScrollMarkers() {
            TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

            if (scrollType == TrackingValues.ScrollMarkerType.NONE) {
                scrollMarkerLayer.setVisibility(View.GONE);
                Log.d(TAG, "Scroll-Marker deaktiviert");
                return;
            }

            scrollMarkerLayer.setVisibility(View.VISIBLE);
            createInitialScrollMarkers(scrollType);

            Log.d(TAG, "Scroll-Marker System eingerichtet: " + scrollType);
        }

        /**
         * Erstellt initiale Scroll-Marker
         */
        private void createInitialScrollMarkers(@NonNull TrackingValues.ScrollMarkerType scrollType) {
            switch (scrollType) {
                case VERTICAL:
                    for (int i = 0; i < 2; i++) {
                        float worldX = AppConstants.Scrolling.VERTICAL_BASE_X[i] * screenWidth;
                        float worldY = AppConstants.Scrolling.VERTICAL_BASE_Y[i] * screenHeight;
                        createDynamicMarker(worldX, worldY, i);
                    }
                    break;
                case HORIZONTAL:
                    for (int i = 0; i < 2; i++) {
                        float worldX = AppConstants.Scrolling.HORIZONTAL_BASE_X[i] * screenWidth;
                        float worldY = AppConstants.Scrolling.HORIZONTAL_BASE_Y[i] * screenHeight;
                        createDynamicMarker(worldX, worldY, i);
                    }
                    break;
            }
        }

        /**
         * Behandelt Scroll-Gesten
         */
        public boolean handleScroll(float distanceX, float distanceY) {
            TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

            if (scrollType == TrackingValues.ScrollMarkerType.NONE) {
                return false;
            }

            // Momentum-Scrolling stoppen
            stopMomentumScrolling();

            // Scroll-Position aktualisieren
            switch (scrollType) {
                case VERTICAL:
                    totalScrollY += distanceY;
                    break;
                case HORIZONTAL:
                    totalScrollX += distanceX;
                    break;
            }

            updateDynamicMarkers();
            return true;
        }

        /**
         * Behandelt Fling-Gesten für Momentum-Scrolling
         */
        public boolean handleFling(float velocityX, float velocityY) {
            TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

            if (scrollType == TrackingValues.ScrollMarkerType.NONE) {
                return false;
            }

            float relevantVelocity = 0f;
            boolean isVertical = false;

            switch (scrollType) {
                case VERTICAL:
                    relevantVelocity = -velocityY;
                    isVertical = true;
                    break;
                case HORIZONTAL:
                    relevantVelocity = -velocityX;
                    isVertical = false;
                    break;
            }

            if (Math.abs(relevantVelocity) >= AppConstants.Scrolling.MIN_VELOCITY) {
                startMomentumScroll(relevantVelocity, isVertical);
                return true;
            }

            return false;
        }

        /**
         * Startet Momentum-Scrolling
         */
        private void startMomentumScroll(float initialVelocity, boolean isVertical) {
            stopMomentumScrolling();

            isMomentumScrolling = true;

            // GEÄNDERT: Verwende jetzt AppConstants.Scrolling.DECELERATION
            float deceleration = AppConstants.Scrolling.DECELERATION;
            float totalDistance = (initialVelocity * initialVelocity) / (2 * deceleration);
            if (initialVelocity < 0) totalDistance = -totalDistance;

            momentumAnimator = ValueAnimator.ofFloat(0f, totalDistance);
            momentumAnimator.setDuration(AppConstants.Scrolling.MOMENTUM_DURATION);

            // GEÄNDERT: Verwende jetzt AppConstants.Scrolling.INTERPOLATOR_FACTOR
            momentumAnimator.setInterpolator(new DecelerateInterpolator(AppConstants.Scrolling.INTERPOLATOR_FACTOR));

            momentumAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private float lastValue = 0f;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (!isMomentumScrolling) {
                        animation.cancel();
                        return;
                    }

                    float currentValue = (Float) animation.getAnimatedValue();
                    float deltaValue = currentValue - lastValue;
                    lastValue = currentValue;

                    if (isVertical) {
                        totalScrollY += deltaValue;
                    } else {
                        totalScrollX += deltaValue;
                    }

                    updateDynamicMarkers();
                }
            });

            momentumAnimator.addListener(new android.animation.Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(android.animation.Animator animation) {}

                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    isMomentumScrolling = false;
                }

                @Override
                public void onAnimationCancel(android.animation.Animator animation) {
                    isMomentumScrolling = false;
                }

                @Override
                public void onAnimationRepeat(android.animation.Animator animation) {}
            });

            momentumAnimator.start();
        }




        /**
         * Stoppt Momentum-Scrolling
         */
        private void stopMomentumScrolling() {
            if (momentumAnimator != null && momentumAnimator.isRunning()) {
                momentumAnimator.cancel();
                isMomentumScrolling = false;
            }
        }

        /**
         * Erstellt einen dynamischen Marker
         */
        private void createDynamicMarker(float worldX, float worldY, int markerIndex) {
            if (dynamicScrollMarkers.size() >= AppConstants.Scrolling.MAX_MARKERS) {
                removeFarMarkers();
                if (dynamicScrollMarkers.size() >= AppConstants.Scrolling.MAX_MARKERS) {
                    DynamicMarker oldest = dynamicScrollMarkers.get(0);
                    scrollMarkerLayer.removeView(oldest.imageView);
                    dynamicScrollMarkers.remove(0);
                }
            }

            ImageView marker = new ImageView(scrollMarkerLayer.getContext());

            int markerType = Utilities.getMarkerDrawable(trackingValues.getEffectiveScrollMarkerType());
            int markerSize = Utilities.getMarkerSizeInPixels(trackingValues.getMarkerSize());
            int markerColor = Utilities.parseColorSafely(trackingValues.getMarkerColor());

            marker.setImageResource(markerType);
            marker.setColorFilter(markerColor);

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(markerSize, markerSize);
            marker.setLayoutParams(params);

            scrollMarkerLayer.addView(marker);
            updateMarkerPosition(marker, worldX, worldY);

            dynamicScrollMarkers.add(new DynamicMarker(marker, worldX, worldY, markerIndex));
        }

        /**
         * Aktualisiert die Position eines Markers
         */
        private void updateMarkerPosition(@NonNull ImageView marker, float worldX, float worldY) {
            float screenX = worldX - totalScrollX + screenWidth / 2f;
            float screenY = worldY - totalScrollY + screenHeight / 2f;

            int markerSize = marker.getLayoutParams().width;
            float centeredX = screenX - (markerSize / 2f);
            float centeredY = screenY - (markerSize / 2f);

            marker.setX(centeredX);
            marker.setY(centeredY);
        }

        /**
         * Aktualisiert alle dynamischen Marker
         */
        private void updateDynamicMarkers() {
            for (DynamicMarker dynamicMarker : dynamicScrollMarkers) {
                updateMarkerPosition(dynamicMarker.imageView, dynamicMarker.worldX, dynamicMarker.worldY);
            }

            removeFarMarkers();
            addNewMarkers();
        }

        /**
         * Entfernt weit entfernte Marker
         */
        private void removeFarMarkers() {
            Iterator<DynamicMarker> iterator = dynamicScrollMarkers.iterator();
            while (iterator.hasNext()) {
                DynamicMarker marker = iterator.next();

                float screenX = marker.worldX - totalScrollX + screenWidth / 2f;
                float screenY = marker.worldY - totalScrollY + screenHeight / 2f;

                if (screenX < -AppConstants.Scrolling.CLEANUP_DISTANCE ||
                        screenX > screenWidth + AppConstants.Scrolling.CLEANUP_DISTANCE ||
                        screenY < -AppConstants.Scrolling.CLEANUP_DISTANCE ||
                        screenY > screenHeight + AppConstants.Scrolling.CLEANUP_DISTANCE) {

                    scrollMarkerLayer.removeView(marker.imageView);
                    iterator.remove();
                }
            }
        }

        /**
         * Fügt neue Marker hinzu
         */
        private void addNewMarkers() {
            TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

            switch (scrollType) {
                case VERTICAL:
                    for (int i = 0; i < 2; i++) {
                        addMarkersForBase(i, AppConstants.Scrolling.VERTICAL_BASE_X[i] * screenWidth,
                                AppConstants.Scrolling.VERTICAL_BASE_Y[i] * screenHeight, 0, AppConstants.Scrolling.MARKER_SPACING);
                    }
                    break;
                case HORIZONTAL:
                    for (int i = 0; i < 2; i++) {
                        addMarkersForBase(i, AppConstants.Scrolling.HORIZONTAL_BASE_X[i] * screenWidth,
                                AppConstants.Scrolling.HORIZONTAL_BASE_Y[i] * screenHeight, AppConstants.Scrolling.MARKER_SPACING, 0);
                    }
                    break;
            }
        }

        private void addMarkersForBase(int baseIndex, float baseX, float baseY, float deltaX, float deltaY) {
            float offsetX = deltaX != 0 ? totalScrollX : 0;
            float offsetY = deltaY != 0 ? totalScrollY : 0;

            int centerIndexX = Math.round(offsetX / AppConstants.Scrolling.MARKER_SPACING);
            int centerIndexY = Math.round(offsetY / AppConstants.Scrolling.MARKER_SPACING);

            int range = 10;
            for (int i = -range; i <= range; i++) {
                float newWorldX = baseX;
                float newWorldY = baseY;

                if (deltaX != 0) newWorldX = baseX + deltaX * (centerIndexX + i);
                if (deltaY != 0) newWorldY = baseY + deltaY * (centerIndexY + i);

                if (isMarkerNeeded(newWorldX, newWorldY) && !markerExistsAt(newWorldX, newWorldY)) {
                    createDynamicMarker(newWorldX, newWorldY, baseIndex);
                }
            }
        }

        private boolean isMarkerNeeded(float worldX, float worldY) {
            float screenX = worldX - totalScrollX + screenWidth / 2f;
            float screenY = worldY - totalScrollY + screenHeight / 2f;

            int buffer = AppConstants.Scrolling.GENERATION_BUFFER;
            return screenX >= -buffer && screenX <= screenWidth + buffer &&
                    screenY >= -buffer && screenY <= screenHeight + buffer;
        }

        private boolean markerExistsAt(float worldX, float worldY) {
            float tolerance = AppConstants.Scrolling.MARKER_SPACING / 4f;

            for (DynamicMarker marker : dynamicScrollMarkers) {
                if (Math.abs(marker.worldX - worldX) < tolerance &&
                        Math.abs(marker.worldY - worldY) < tolerance) {
                    return true;
                }
            }
            return false;
        }

        public void pauseAnimations() {
            stopMomentumScrolling();
        }

        public void cleanup() {
            stopMomentumScrolling();
            for (DynamicMarker marker : dynamicScrollMarkers) {
                scrollMarkerLayer.removeView(marker.imageView);
            }
            dynamicScrollMarkers.clear();
        }
    }

    /**
     * Repräsentiert einen dynamischen Marker mit Position
     */
    private static class DynamicMarker {
        final ImageView imageView;
        final float worldX, worldY;
        final int markerIndex;

        DynamicMarker(@NonNull ImageView imageView, float worldX, float worldY, int markerIndex) {
            this.imageView = imageView;
            this.worldX = worldX;
            this.worldY = worldY;
            this.markerIndex = markerIndex;
        }
    }
}
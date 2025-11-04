package com.beigel.screenTracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * MainActivity mit Google Play In-App Review Integration
 * - Review erscheint NUR auf dem Hauptscreen
 * - Review erscheint NIEMALS während des Trackings
 * - Intelligentes Timing für Bewertungsaufforderungen
 */
public class MainActivity extends AppCompatActivity implements SettingsManager.SettingsListener {

    private static final String TAG = AppConstants.LogTags.MAIN;

    // ========== TESTING-KONFIGURATION ==========
    // Setze auf true um Review SOFORT beim Start zu sehen (für Testing)
    // Setze auf false für normale Produktions-Logik (nach 3 Starts + 2 Tagen)
    private static final boolean TESTING_MODE = true;
    // ===========================================

    // Core Components
    private SettingsManager settingsManager;
    private TrackingValues trackingValues;
    private PreviewManager previewManager;
    private InAppReviewManager reviewManager;

    // UI References
    private Button buttonStart;
    private ConstraintLayout previewTrackingBackground;
    private ConstraintLayout previewScrollMarkerLayer;

    // NEU: Flag um zu tracken ob wir auf dem Hauptscreen sind
    private boolean isOnMainScreen = false;
    private boolean hasShownReviewThisSession = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity onCreate gestartet");

        try {
            initializeApp();
            setupUI();
            updatePreview();

            // NEU: In-App Review initialisieren (aber noch nicht anzeigen!)
            initializeInAppReview();

            Log.d(TAG, "MainActivity erfolgreich initialisiert");
        } catch (Exception e) {
            Log.e(TAG, "Fehler bei MainActivity Initialisierung", e);
            showErrorAndFinish();
        }
    }

    // ========== INITIALIZATION ==========

    private void initializeApp() {
        trackingValues = Utilities.loadSettings(this);
        settingsManager = new SettingsManager(this, trackingValues, this);
        previewManager = new PreviewManager(this, trackingValues);

        Log.d(TAG, "App-Komponenten initialisiert");
    }

    /**
     * NEU: Initialisiert den In-App Review Manager
     * Review wird NICHT sofort angezeigt, sondern erst in onResume wenn auf Hauptscreen
     */
    private void initializeInAppReview() {
        reviewManager = new InAppReviewManager(this);

        if (TESTING_MODE) {
            // Testing-Setup (Bedingungen simulieren)
            Log.d(TAG, "⚠️ TESTING-MODUS aktiviert");
            reviewManager.resetForTesting();
            reviewManager.simulateLaunches(5);
            reviewManager.simulateDaysAgo(7);
            reviewManager.printDebugInfo();
        } else {
            // Produktions-Modus: App-Launch registrieren
            reviewManager.onAppLaunched();
            reviewManager.printDebugInfo();
        }

        Log.d(TAG, "In-App Review Manager initialisiert (Review wird später in onResume gezeigt)");
    }

    private void setupUI() {
        initializeUIReferences();
        setupSettingsUI();
        setupStartButton();

        Log.d(TAG, "UI-Setup abgeschlossen");
    }

    private void initializeUIReferences() {
        buttonStart = findViewById(R.id.button_start);
        previewTrackingBackground = findViewById(R.id.trackingBackground);
        previewScrollMarkerLayer = findViewById(R.id.scrollMarkerLayer);

        previewManager.setPreviewViews(previewTrackingBackground, previewScrollMarkerLayer);
    }

    private void setupSettingsUI() {
        settingsManager.initializeViews(findViewById(android.R.id.content));
    }

    private void setupStartButton() {
        buttonStart.setOnClickListener(v -> startTracking());
    }

    // ========== TRACKING START ==========

    /**
     * Startet das Tracking
     * WICHTIG: Markiert dass wir den Hauptscreen verlassen
     */
    private void startTracking() {
        try {
            Utilities.saveSettings(this, trackingValues);
            showMessage(getString(R.string.settings_saved), false);

            // NEU: Markiere dass wir NICHT mehr auf dem Hauptscreen sind
            isOnMainScreen = false;
            Log.d(TAG, "Verlasse Hauptscreen - Review wird nicht mehr angezeigt");

            // Tracking-Screen starten
            Intent intent = new Intent(MainActivity.this, Trackingscreen.class);
            intent.putExtra("trackingValues", trackingValues);
            startActivity(intent);

            Log.d(TAG, "Tracking gestartet");

        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Starten des Trackings", e);
            showMessage("Fehler beim Starten des Trackings", true);
        }
    }

    // ========== SETTINGS LISTENER IMPLEMENTATION ==========

    @Override
    public void onSettingsChanged() {
        updatePreview();
        Log.d(TAG, "Einstellungen geändert, Preview aktualisiert");
    }

    @Override
    public void onColorChanged() {
        updatePreview();
        Log.d(TAG, "Farben geändert, Preview aktualisiert");
    }

    @Override
    public String getLocalizedString(int resId) {
        try {
            return getString(resId);
        } catch (Exception e) {
            Log.w(TAG, "String-Ressource nicht gefunden: " + resId, e);
            return "N/A";
        }
    }

    // ========== PREVIEW MANAGEMENT ==========

    private void updatePreview() {
        try {
            previewManager.updatePreview();
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Aktualisieren der Vorschau", e);
        }
    }

    // ========== LIFECYCLE ==========

    @Override
    protected void onResume() {
        super.onResume();
        try {
            updatePreview();
            settingsManager.updateUI();

            // NEU: Wir sind wieder auf dem Hauptscreen
            isOnMainScreen = true;
            Log.d(TAG, "MainActivity onResume - zurück auf Hauptscreen");

            // NEU: Review nur anzeigen wenn:
            // 1. Wir auf dem Hauptscreen sind
            // 2. Noch nicht diese Session gezeigt wurde
            // 3. Bedingungen erfüllt sind
            showReviewIfAppropriate();

        } catch (Exception e) {
            Log.e(TAG, "Fehler in onResume", e);
        }
    }

    /**
     * NEU: Zeigt Review nur wenn alle Bedingungen erfüllt sind
     * Wird in onResume aufgerufen (nur auf Hauptscreen)
     */
    private void showReviewIfAppropriate() {
        // Prüfung 1: Sind wir auf dem Hauptscreen?
        if (!isOnMainScreen) {
            Log.d(TAG, "Nicht auf Hauptscreen - kein Review");
            return;
        }

        // Prüfung 2: Wurde Review bereits diese Session gezeigt?
        if (hasShownReviewThisSession) {
            Log.d(TAG, "Review bereits diese Session gezeigt");
            return;
        }

        // Prüfung 3: Sind die Review-Bedingungen erfüllt?
        if (!reviewManager.shouldShowReviewPrompt()) {
            Log.d(TAG, "Review-Bedingungen nicht erfüllt");
            return;
        }

        // Alle Prüfungen bestanden - zeige Review nach kurzer Verzögerung
        if (TESTING_MODE) {
            // Testing: 3 Sekunden Verzögerung
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isOnMainScreen) { // Nochmal prüfen ob noch auf Hauptscreen
                    Log.d(TAG, "Zeige Review-Prompt jetzt (TESTING)...");
                    reviewManager.showReviewPromptNow();
                    hasShownReviewThisSession = true;
                }
            }, 3000);
        } else {
            // Produktion: 2 Sekunden Verzögerung für bessere UX
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isOnMainScreen) { // Nochmal prüfen ob noch auf Hauptscreen
                    Log.d(TAG, "Zeige Review-Prompt jetzt...");
                    reviewManager.showReviewPromptNow();
                    hasShownReviewThisSession = true;
                }
            }, 2000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            // NEU: Wir verlassen den Hauptscreen
            isOnMainScreen = false;
            Log.d(TAG, "MainActivity onPause - verlasse Hauptscreen");

            // Einstellungen speichern
            Utilities.saveSettings(this, trackingValues);
            Log.d(TAG, "Einstellungen gespeichert");
        } catch (Exception e) {
            Log.w(TAG, "Fehler beim Speichern in onPause", e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Explizit markieren dass wir nicht mehr auf dem Hauptscreen sind
        isOnMainScreen = false;
        Log.d(TAG, "MainActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOnMainScreen = false;
        Log.d(TAG, "MainActivity onDestroy");
    }

    // ========== UTILITY METHODS ==========

    private void showMessage(@NonNull String message, boolean isError) {
        try {
            Toast.makeText(this, message, isError ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();

            if (isError) {
                Log.e(TAG, "Error message shown: " + message);
            } else {
                Log.d(TAG, "Info message shown: " + message);
            }
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Anzeigen der Nachricht: " + message, e);
        }
    }

    private void showErrorAndFinish() {
        showMessage("Kritischer Fehler beim App-Start", true);
        finish();
    }

    // ========== PREVIEW MANAGER (INNER CLASS) ==========

    private static class PreviewManager {
        private final MainActivity activity;
        private final TrackingValues trackingValues;

        private ConstraintLayout previewBackground;
        private ConstraintLayout previewScrollLayer;
        private ArrayList<ImageView> trackingPointList1;
        private ArrayList<ImageView> trackingPointList2;
        private ArrayList<ImageView> trackingPointList3;
        private ArrayList<ImageView> trackingPointListE;

        public PreviewManager(@NonNull MainActivity activity, @NonNull TrackingValues trackingValues) {
            this.activity = activity;
            this.trackingValues = trackingValues;
            initializeMarkerLists();
        }

        public void setPreviewViews(@NonNull ConstraintLayout background, @NonNull ConstraintLayout scrollLayer) {
            this.previewBackground = background;
            this.previewScrollLayer = scrollLayer;
        }

        private void initializeMarkerLists() {
            trackingPointList1 = new ArrayList<>();
            trackingPointList2 = new ArrayList<>();
            trackingPointList3 = new ArrayList<>();
            trackingPointListE = new ArrayList<>();

            try {
                // Gruppe 1
                trackingPointList1.add(activity.findViewById(R.id.trackingPoint_1_1));
                trackingPointList1.add(activity.findViewById(R.id.trackingPoint_1_2));
                trackingPointList1.add(activity.findViewById(R.id.trackingPoint_1_3));
                trackingPointList1.add(activity.findViewById(R.id.trackingPoint_1_4));
                trackingPointList1.add(activity.findViewById(R.id.trackingPoint_1_5));

                // Gruppe 2
                trackingPointList2.add(activity.findViewById(R.id.trackingPoint_2_1));
                trackingPointList2.add(activity.findViewById(R.id.trackingPoint_2_2));
                trackingPointList2.add(activity.findViewById(R.id.trackingPoint_2_3));
                trackingPointList2.add(activity.findViewById(R.id.trackingPoint_2_4));

                // Gruppe 3
                trackingPointList3.add(activity.findViewById(R.id.trackingPoint_3_1));
                trackingPointList3.add(activity.findViewById(R.id.trackingPoint_3_2));
                trackingPointList3.add(activity.findViewById(R.id.trackingPoint_3_3));
                trackingPointList3.add(activity.findViewById(R.id.trackingPoint_3_4));

                // Eckmarker
                trackingPointListE.add(activity.findViewById(R.id.trackingPoint_E_1));
                trackingPointListE.add(activity.findViewById(R.id.trackingPoint_E_2));
                trackingPointListE.add(activity.findViewById(R.id.trackingPoint_E_3));
                trackingPointListE.add(activity.findViewById(R.id.trackingPoint_E_4));

                Log.d(TAG, "Marker-Listen für Vorschau initialisiert");
            } catch (Exception e) {
                Log.e(TAG, "Fehler beim Initialisieren der Marker-Listen", e);
            }
        }

        public void updatePreview() {
            try {
                cleanPreview();
                setBackgroundColor();
                createStaticMarkers();
                createEdgeMarkers();
                handleScrollMarkers();

                Log.d(TAG, "Vorschau erfolgreich aktualisiert");
            } catch (Exception e) {
                Log.e(TAG, "Fehler beim Aktualisieren der Vorschau", e);
            }
        }

        private void setBackgroundColor() {
            if (previewBackground != null) {
                int bgColor = Utilities.parseColorSafely(trackingValues.getBackgroundColor());
                previewBackground.setBackgroundColor(bgColor);
            }
        }

        private void createStaticMarkers() {
            int density = trackingValues.getMarkerDensity();

            switch (density) {
                case 0:
                    Log.d(TAG, "Keine Marker (Dichte 0)");
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
                default:
                    Log.w(TAG, "Ungültige Marker-Dichte: " + density);
            }
        }

        private void createEdgeMarkers() {
            if (trackingValues.getEdgeMarker() != TrackingValues.EdgeMarkerType.NONE) {
                Utilities.createEdgeMarker(trackingPointListE, trackingValues);
                Log.d(TAG, "Eckmarker erstellt: " + trackingValues.getEdgeMarker());
            }
        }

        private void handleScrollMarkers() {
            if (previewScrollLayer != null) {
                previewScrollLayer.setVisibility(android.view.View.GONE);
            }
        }

        private void cleanPreview() {
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
}
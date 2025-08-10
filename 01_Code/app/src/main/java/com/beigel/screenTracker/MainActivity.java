package com.beigel.screenTracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

/**
 * Aufgeräumte MainActivity mit verbesserter Architektur
 * - SettingsManager für UI-Logic
 * - AppConstants für alle Konstanten
 * - Besseres Error Handling
 * - Reduzierte Komplexität
 * - Proper Logging
 */
public class MainActivity extends AppCompatActivity implements SettingsManager.SettingsListener {

    private static final String TAG = AppConstants.LogTags.MAIN;

    // Core Components
    private SettingsManager settingsManager;
    private TrackingValues trackingValues;
    private PreviewManager previewManager;

    // UI References
    private Button buttonStart;
    private ConstraintLayout previewTrackingBackground;
    private ConstraintLayout previewScrollMarkerLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity onCreate gestartet");

        try {
            initializeApp();
            setupUI();
            updatePreview();

            Log.d(TAG, "MainActivity erfolgreich initialisiert");
        } catch (Exception e) {
            Log.e(TAG, "Fehler bei MainActivity Initialisierung", e);
            showErrorAndFinish();
        }
    }

    // ========== INITIALIZATION ==========

    /**
     * Initialisiert die App-Komponenten
     */
    private void initializeApp() {
        // TrackingValues laden oder mit Standardwerten initialisieren
        trackingValues = Utilities.loadSettings(this);

        // Settings Manager initialisieren
        settingsManager = new SettingsManager(this, trackingValues, this);

        // Preview Manager initialisieren
        previewManager = new PreviewManager(this, trackingValues);

        Log.d(TAG, "App-Komponenten initialisiert");
    }

    /**
     * Initialisiert alle UI-Komponenten
     */
    private void setupUI() {
        initializeUIReferences();
        setupSettingsUI();
        setupStartButton();
        setupFooterLinks();

        Log.d(TAG, "UI-Setup abgeschlossen");
    }

    /**
     * Initialisiert UI-Referenzen
     */
    private void initializeUIReferences() {
        buttonStart = findViewById(R.id.button_start);
        previewTrackingBackground = findViewById(R.id.trackingBackground);
        previewScrollMarkerLayer = findViewById(R.id.scrollMarkerLayer);

        // Preview Manager mit UI-Referenzen versorgen
        previewManager.setPreviewViews(previewTrackingBackground, previewScrollMarkerLayer);
    }

    /**
     * Initialisiert Settings UI über SettingsManager
     */
    private void setupSettingsUI() {
        settingsManager.initializeViews(findViewById(android.R.id.content));
    }

    /**
     * Konfiguriert den Start-Button
     */
    private void setupStartButton() {
        buttonStart.setOnClickListener(v -> startTracking());
    }

    /**
     * Initialisiert die Footer-Links
     */
    private void setupFooterLinks() {
        try {
            FooterLinkManager footerManager = new FooterLinkManager(this);
            footerManager.initializeFooterLinks(findViewById(android.R.id.content));
        } catch (Exception e) {
            Log.w(TAG, "Footer-Links konnten nicht initialisiert werden", e);
        }
    }

    // ========== TRACKING START ==========

    /**
     * Startet das Tracking mit verbessertem Error Handling
     */
    private void startTracking() {
        try {
            // Einstellungen speichern
            Utilities.saveSettings(this, trackingValues);

            // Erfolg anzeigen
            showMessage(getString(R.string.settings_saved), false);

            // Tracking-Screen starten
            Intent intent = new Intent(MainActivity.this, Trackingscreen.class);
            intent.putExtra("trackingValues", trackingValues);
            startActivity(intent);

            Log.d(TAG, "Tracking gestartet mit Einstellungen: " + trackingValues.toString());

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
            return "N/A"; // Fallback
        }
    }

    // ========== PREVIEW MANAGEMENT ==========

    /**
     * Aktualisiert die Vorschau
     */
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
            Log.d(TAG, "MainActivity onResume - UI aktualisiert");
        } catch (Exception e) {
            Log.e(TAG, "Fehler in onResume", e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            // Einstellungen bei Pause speichern
            Utilities.saveSettings(this, trackingValues);
            Log.d(TAG, "Einstellungen bei onPause gespeichert");
        } catch (Exception e) {
            Log.w(TAG, "Fehler beim Speichern in onPause", e);
        }
    }

    // ========== UTILITY METHODS ==========

    /**
     * Zeigt eine Toast-Nachricht
     */
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

    /**
     * Zeigt Fehler und beendet Activity bei kritischen Fehlern
     */
    private void showErrorAndFinish() {
        showMessage("Kritischer Fehler beim App-Start", true);
        finish();
    }

    // ========== INNER CLASSES ==========

    /**
     * Verwaltet die Vorschau-Funktionalität
     * Reduziert Komplexität der MainActivity
     */
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

        /**
         * Initialisiert die Marker-Listen für die Vorschau
         */
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

        /**
         * Aktualisiert die komplette Vorschau
         */
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

        /**
         * Setzt die Hintergrundfarbe
         */
        private void setBackgroundColor() {
            if (previewBackground != null) {
                int bgColor = Utilities.parseColorSafely(trackingValues.getBackgroundColor());
                previewBackground.setBackgroundColor(bgColor);
            }
        }

        /**
         * Erstellt statische Marker basierend auf Dichte
         */
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

        /**
         * Erstellt Eckmarker falls aktiviert
         */
        private void createEdgeMarkers() {
            if (trackingValues.getEdgeMarker() != TrackingValues.EdgeMarkerType.NONE) {
                Utilities.createEdgeMarker(trackingPointListE, trackingValues);
                Log.d(TAG, "Eckmarker erstellt: " + trackingValues.getEdgeMarker());
            }
        }

        /**
         * Verwaltet Scroll-Marker (in Vorschau ausgeblendet)
         */
        private void handleScrollMarkers() {
            if (previewScrollLayer != null) {
                // Scroll-Layer in Vorschau immer ausblenden
                previewScrollLayer.setVisibility(View.GONE);
            }
        }

        /**
         * Räumt die Vorschau auf
         */
        private void cleanPreview() {
            clearMarkerGroup(trackingPointList1);
            clearMarkerGroup(trackingPointList2);
            clearMarkerGroup(trackingPointList3);
            clearMarkerGroup(trackingPointListE);
        }

        /**
         * Räumt eine Marker-Gruppe auf
         */
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
     * Verwaltet Footer-Links
     * Separiert von Haupt-UI-Logic
     */
    private static class FooterLinkManager {
        private final MainActivity activity;

        public FooterLinkManager(@NonNull MainActivity activity) {
            this.activity = activity;
        }

        public void initializeFooterLinks(@NonNull View rootView) {
            TextView footerBeigelLink = rootView.findViewById(R.id.footer_beigel_link);
            TextView footerOvermindLink = rootView.findViewById(R.id.footer_overmind_link);
            TextView footerBrowserLink = rootView.findViewById(R.id.footer_browser_link);

            if (footerBeigelLink != null) {
                footerBeigelLink.setOnClickListener(v -> openWebLink(AppConstants.Urls.BEIGEL_STORE));
            }

            if (footerOvermindLink != null) {
                footerOvermindLink.setOnClickListener(v -> openWebLink(AppConstants.Urls.OVERMIND_STUDIOS));
            }

            if (footerBrowserLink != null) {
                footerBrowserLink.setOnClickListener(v -> openWebLink(AppConstants.Urls.BROWSER_VERSION));
            }

            Log.d(TAG, "Footer-Links initialisiert");
        }

        /**
         * Öffnet einen Web-Link im Browser
         */
        private void openWebLink(@NonNull String url) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(browserIntent);
                Log.d(TAG, "Browser geöffnet für: " + url);
            } catch (Exception e) {
                Log.w(TAG, "Browser konnte nicht geöffnet werden für: " + url, e);

                String message = "Browser nicht verfügbar";
                try {
                    message = activity.getString(R.string.browser_not_available);
                } catch (Exception ex) {
                    // Fallback verwenden
                }

                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
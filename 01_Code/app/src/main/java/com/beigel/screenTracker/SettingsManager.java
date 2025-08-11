package com.beigel.screenTracker;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

/**
 * Verwaltet alle Settings-bezogenen UI-Operationen
 * Entlastet die MainActivity von komplexer UI-Logik
 */
public class SettingsManager implements AdapterView.OnItemSelectedListener {

    private static final String TAG = AppConstants.LogTags.SETTINGS;

    private final Context context;
    private final TrackingValues trackingValues;
    private final SettingsListener listener;

    // UI-Referenzen
    private Button buttonBackgroundColor;
    private Button buttonMarkerColor;
    private Spinner spinnerMarkerDensity;
    private Spinner spinnerMarkerSize;
    private Spinner spinnerEdgeMarkerSize;
    private Spinner spinnerMarkerType;
    private Spinner spinnerEdgeMarkers;
    private Spinner spinnerScrollMarkers;

    /**
     * Interface für Callbacks an die MainActivity
     */
    public interface SettingsListener {
        void onSettingsChanged();
        void onColorChanged();
        String getLocalizedString(int resId);
    }

    public SettingsManager(@NonNull Context context,
                           @NonNull TrackingValues trackingValues,
                           @NonNull SettingsListener listener) {
        this.context = context;
        this.trackingValues = trackingValues;
        this.listener = listener;
    }

    // ========== INITIALIZATION ==========

    /**
     * Initialisiert alle UI-Komponenten
     */
    public void initializeViews(@NonNull View rootView) {
        buttonBackgroundColor = rootView.findViewById(R.id.buttonBackgroundColor);
        buttonMarkerColor = rootView.findViewById(R.id.buttonMarkerColor);
        spinnerEdgeMarkerSize = rootView.findViewById(R.id.spinner_edge_marker_size);
        spinnerMarkerDensity = rootView.findViewById(R.id.spinner_marker_density);
        spinnerMarkerSize = rootView.findViewById(R.id.spinner_marker_size);
        spinnerMarkerType = rootView.findViewById(R.id.spinner_marker_type);
        spinnerEdgeMarkers = rootView.findViewById(R.id.spinner_edge_marker);
        spinnerScrollMarkers = rootView.findViewById(R.id.spinner_scroll_marker);

        setupSpinners();
        setupColorButtons();
        updateUI();
    }

    /**
     * Konfiguriert alle Spinner mit Adaptern
     */
    private void setupSpinners() {
        setupSpinner(spinnerMarkerDensity, R.array.marker_density_array, trackingValues.getMarkerDensity());
        setupSpinner(spinnerMarkerSize, R.array.edge_size_array, trackingValues.getMarkerSize() - 1);
        setupSpinner(spinnerEdgeMarkerSize, R.array.edge_size_array, trackingValues.getEdgeMarkerSize() - 1);
        setupSpinner(spinnerMarkerType, R.array.marker_type_array, getMarkerTypePosition());
        setupSpinner(spinnerEdgeMarkers, R.array.edge_markers_array, getEdgeMarkerPosition());
        setupSpinner(spinnerScrollMarkers, R.array.scroll_markers_array, getScrollMarkerPosition());
    }

    /**
     * Hilfsmethode für Spinner-Setup
     */
    private void setupSpinner(@NonNull Spinner spinner, int arrayResId, int selection) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(selection);
        spinner.setOnItemSelectedListener(this);
    }

    /**
     * Konfiguriert die Farb-Buttons
     */
    private void setupColorButtons() {
        buttonBackgroundColor.setOnClickListener(this::showBackgroundColorDialog);
        buttonMarkerColor.setOnClickListener(this::showMarkerColorDialog);
    }

    // ========== POSITION MAPPING ==========

    private int getMarkerTypePosition() {
        switch (trackingValues.getMarkerType()) {
            case PIE: return 0;
            case CIRCLE: return 1;
            case TRIANGLE: return 2;
            case CROSS:
            default: return 3;
        }
    }

    private int getEdgeMarkerPosition() {
        switch (trackingValues.getEdgeMarker()) {
            case NONE: return 0;
            case CORNER: return 1;
            case SEMICIRCLE:
            default: return 2;
        }
    }

    private int getScrollMarkerPosition() {
        switch (trackingValues.getScrollMarker()) {
            case NONE: return 0;
            case VERTICAL: return 1;
            case HORIZONTAL:
            default: return 2;
        }
    }

    // ========== COLOR PICKER DIALOGS ==========

    private void showBackgroundColorDialog(@NonNull View view) {
        showColorDialog(
                listener.getLocalizedString(R.string.color_picker_background_title),
                "BackgroundColorPref",
                (envelope, fromUser) -> setBackgroundColor(envelope)
        );
    }

    private void showMarkerColorDialog(@NonNull View view) {
        showColorDialog(
                listener.getLocalizedString(R.string.color_picker_marker_title),
                "MarkerColorPref",
                (envelope, fromUser) -> setMarkerColor(envelope)
        );
    }

    /**
     * Generische Color Picker Dialog-Methode
     */
    private void showColorDialog(@NonNull String title,
                                 @NonNull String prefName,
                                 @NonNull ColorEnvelopeListener colorListener) {
        try {
            new ColorPickerDialog.Builder(context)
                    .setTitle(title)
                    .setPreferenceName(prefName)
                    .setPositiveButton(listener.getLocalizedString(R.string.color_picker_confirm), colorListener)
                    .setNegativeButton(listener.getLocalizedString(R.string.color_picker_cancel),
                            (dialogInterface, i) -> dialogInterface.dismiss())
                    .attachAlphaSlideBar(false)
                    .attachBrightnessSlideBar(true)
                    .setBottomSpace(12)
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Öffnen des Color Pickers", e);
        }
    }

    // ========== COLOR HANDLING ==========

    private void setBackgroundColor(@NonNull ColorEnvelope envelope) {
        String colorCode = processColorEnvelope(envelope);
        if (colorCode != null) {
            trackingValues.setBackgroundColor(colorCode);
            updateColorButton(buttonBackgroundColor, colorCode);
            listener.onColorChanged();
            Log.d(TAG, "Hintergrundfarbe gesetzt: " + colorCode);
        }
    }

    private void setMarkerColor(@NonNull ColorEnvelope envelope) {
        String colorCode = processColorEnvelope(envelope);
        if (colorCode != null) {
            trackingValues.setMarkerColor(colorCode);
            updateColorButton(buttonMarkerColor, colorCode);
            listener.onColorChanged();
            Log.d(TAG, "Marker-Farbe gesetzt: " + colorCode);
        }
    }

    /**
     * Verarbeitet ColorEnvelope zu Hex-String
     */
    private String processColorEnvelope(@NonNull ColorEnvelope envelope) {
        String hexCode = envelope.getHexCode();
        if (hexCode == null || hexCode.isEmpty()) {
            Log.w(TAG, "Leerer Hex-Code vom Color Picker erhalten");
            return null;
        }

        String colorCode = "#" + hexCode;
        if (!Utilities.isValidHexColor(colorCode)) {
            Log.w(TAG, "Ungültiger Hex-Code: " + colorCode);
            return null;
        }

        return colorCode;
    }

    /**
     * Aktualisiert einen Color-Button mit neuer Farbe
     */
    private void updateColorButton(@NonNull Button button, @NonNull String colorCode) {
        try {
            int color = Color.parseColor(colorCode);
            button.setBackgroundColor(color);
            setOptimalTextColor(button, color);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Fehler beim Setzen der Button-Farbe: " + colorCode, e);
        }
    }

    /**
     * Setzt optimale Textfarbe basierend auf Hintergrund
     */
    private void setOptimalTextColor(@NonNull Button button, int backgroundColor) {
        if (Utilities.isColorBright(backgroundColor)) {
            button.setTextColor(Color.BLACK);
        } else {
            button.setTextColor(Color.WHITE);
        }
    }

    // ========== SPINNER SELECTION HANDLING ==========

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == null) return;

        int parentId = parent.getId();
        boolean settingsChanged = false;

        try {
            if (parentId == R.id.spinner_edge_marker) {
                settingsChanged = handleEdgeMarkerSelection(position);
            } else if (parentId == R.id.spinner_edge_marker_size) {
                settingsChanged = handleEdgeMarkerSizeSelection(parent, position);
            } else if (parentId == R.id.spinner_scroll_marker) {
                settingsChanged = handleScrollMarkerSelection(position);
            } else if (parentId == R.id.spinner_marker_density) {
                settingsChanged = handleMarkerDensitySelection(parent, position);
            } else if (parentId == R.id.spinner_marker_size) {
                settingsChanged = handleMarkerSizeSelection(parent, position);
            } else if (parentId == R.id.spinner_marker_type) {
                settingsChanged = handleMarkerTypeSelection(position);
            }

            if (settingsChanged) {
                listener.onSettingsChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, "Fehler bei Spinner-Auswahl", e);
        }
    }

    /**
     * Behandelt Edge-Marker-Größe-Auswahl (NEU)
     */
    private boolean handleEdgeMarkerSizeSelection(@NonNull AdapterView<?> parent, int position) {
        String selectedSize = parent.getItemAtPosition(position).toString();
        trackingValues.setEdgeMarkerSize(selectedSize);
        Log.d(TAG, "Edge-Marker-Größe geändert auf: " + selectedSize);
        return true;
    }

    private boolean handleEdgeMarkerSelection(int position) {
        TrackingValues.EdgeMarkerType selectedEdge = TrackingValues.EdgeMarkerType.NONE;
        switch (position) {
            case 0: selectedEdge = TrackingValues.EdgeMarkerType.NONE; break;
            case 1: selectedEdge = TrackingValues.EdgeMarkerType.CORNER; break;
            case 2: selectedEdge = TrackingValues.EdgeMarkerType.SEMICIRCLE; break;
        }
        trackingValues.setEdgeMarker(selectedEdge);
        return true;
    }

    private boolean handleScrollMarkerSelection(int position) {
        TrackingValues.ScrollMarkerType selectedScroll = TrackingValues.ScrollMarkerType.NONE;
        switch (position) {
            case 0: selectedScroll = TrackingValues.ScrollMarkerType.NONE; break;
            case 1: selectedScroll = TrackingValues.ScrollMarkerType.VERTICAL; break;
            case 2: selectedScroll = TrackingValues.ScrollMarkerType.HORIZONTAL; break;
        }
        trackingValues.setScrollMarker(selectedScroll);
        return true;
    }

    private boolean handleMarkerDensitySelection(@NonNull AdapterView<?> parent, int position) {
        String selectedDensity = parent.getItemAtPosition(position).toString();
        trackingValues.setMarkerDensity(selectedDensity);
        return true;
    }

    private boolean handleMarkerSizeSelection(@NonNull AdapterView<?> parent, int position) {
        String selectedSize = parent.getItemAtPosition(position).toString();
        trackingValues.setMarkerSize(selectedSize);
        return true;
    }

    private boolean handleMarkerTypeSelection(int position) {
        TrackingValues.MarkerType selectedType = TrackingValues.MarkerType.CROSS;
        switch (position) {
            case 0: selectedType = TrackingValues.MarkerType.PIE; break;
            case 1: selectedType = TrackingValues.MarkerType.CIRCLE; break;
            case 2: selectedType = TrackingValues.MarkerType.TRIANGLE; break;
            case 3: selectedType = TrackingValues.MarkerType.CROSS; break;
        }
        trackingValues.setMarkerType(selectedType);
        return true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Interface-Methode, aber nicht benötigt
    }

    // ========== PUBLIC METHODS ==========

    /**
     * Aktualisiert alle UI-Komponenten mit aktuellen Werten
     */
    public void updateUI() {
        updateColorButtons();
        // Weitere UI-Updates hier
    }

    /**
     * Aktualisiert die Farb-Buttons
     */
    public void updateColorButtons() {
        try {
            int bgColor = Color.parseColor(trackingValues.getBackgroundColor());
            buttonBackgroundColor.setBackgroundColor(bgColor);
            setOptimalTextColor(buttonBackgroundColor, bgColor);

            int markerColor = Color.parseColor(trackingValues.getMarkerColor());
            buttonMarkerColor.setBackgroundColor(markerColor);
            setOptimalTextColor(buttonMarkerColor, markerColor);
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Aktualisieren der Color-Buttons", e);
        }
    }

    /**
     * Gibt die aktuellen TrackingValues zurück
     */
    @NonNull
    public TrackingValues getTrackingValues() {
        return trackingValues;
    }
}
package com.beigel.screenTracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;

/**
 * Hauptaktivität der App mit Einstellungsoptionen und Vorschau
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button buttonBackgroundColor;
    private Button buttonMarkerColor;
    private Button buttonStart;
    private TextView footerText;
    private ConstraintLayout previewTrackingBackground;

    private Spinner spinnerMarkerDensity;
    private Spinner spinnerMarkerSize;
    private Spinner spinnerMarkerType;
    private Spinner spinnerEdgeMarkers;

    private ArrayList<ImageView> trackingPointList1;
    private ArrayList<ImageView> trackingPointList2;
    private ArrayList<ImageView> trackingPointList3;
    private ArrayList<ImageView> trackingPointListE;

    private TrackingValues trackingValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TrackingValues initialisieren
        trackingValues = Utilities.loadSettings(this);

        // UI-Elemente initialisieren
        initializeViews();
        initializeMarkerLists();
        setupSpinners();
        setupButtons();
        setupActivityLink();

        // Vorschau aktualisieren
        updateColorButtons();
        createPreview();
    }

    /**
     * Initialisiert alle UI-Elemente
     */
    private void initializeViews() {
        buttonStart = findViewById(R.id.button_start);
        buttonBackgroundColor = findViewById(R.id.buttonBackgroundColor);
        buttonMarkerColor = findViewById(R.id.buttonMarkerColor);
        footerText = findViewById(R.id.footer);

        previewTrackingBackground = findViewById(R.id.trackingBackground);

        spinnerMarkerDensity = findViewById(R.id.spinner_marker_density);
        spinnerMarkerSize = findViewById(R.id.spinner_marker_size);
        spinnerMarkerType = findViewById(R.id.spinner_marker_type);
        spinnerEdgeMarkers = findViewById(R.id.spinner_edge_marker);
    }

    /**
     * Initialisiert die Listen für die verschiedenen Marker-Gruppen
     */
    private void initializeMarkerLists() {
        trackingPointList1 = new ArrayList<>();
        trackingPointList2 = new ArrayList<>();
        trackingPointList3 = new ArrayList<>();
        trackingPointListE = new ArrayList<>();

        // Gruppe 1
        trackingPointList1.add(findViewById(R.id.trackingPoint_1_1));
        trackingPointList1.add(findViewById(R.id.trackingPoint_1_2));
        trackingPointList1.add(findViewById(R.id.trackingPoint_1_3));
        trackingPointList1.add(findViewById(R.id.trackingPoint_1_4));
        trackingPointList1.add(findViewById(R.id.trackingPoint_1_5));

        // Gruppe 2
        trackingPointList2.add(findViewById(R.id.trackingPoint_2_1));
        trackingPointList2.add(findViewById(R.id.trackingPoint_2_2));
        trackingPointList2.add(findViewById(R.id.trackingPoint_2_3));
        trackingPointList2.add(findViewById(R.id.trackingPoint_2_4));

        // Gruppe 3
        trackingPointList3.add(findViewById(R.id.trackingPoint_3_1));
        trackingPointList3.add(findViewById(R.id.trackingPoint_3_2));
        trackingPointList3.add(findViewById(R.id.trackingPoint_3_3));
        trackingPointList3.add(findViewById(R.id.trackingPoint_3_4));

        // Eckmarker
        trackingPointListE.add(findViewById(R.id.trackingPoint_E_1));
        trackingPointListE.add(findViewById(R.id.trackingPoint_E_2));
        trackingPointListE.add(findViewById(R.id.trackingPoint_E_3));
        trackingPointListE.add(findViewById(R.id.trackingPoint_E_4));
    }

    private void setupSpinners() {
        // Spinner für Markerdichte
        ArrayAdapter<CharSequence> densityAdapter = ArrayAdapter.createFromResource(
                this, R.array.marker_density_array, android.R.layout.simple_spinner_item);
        densityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarkerDensity.setAdapter(densityAdapter);
        spinnerMarkerDensity.setSelection(trackingValues.getMarkerDensity());
        spinnerMarkerDensity.setOnItemSelectedListener(this);

        // Spinner für Markertyp
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.marker_type_array, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarkerType.setAdapter(typeAdapter);
        spinnerMarkerType.setSelection(getMarkerTypePosition());
        spinnerMarkerType.setOnItemSelectedListener(this);

        // Spinner für Markergröße
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(
                this, R.array.edge_size_array, android.R.layout.simple_spinner_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarkerSize.setAdapter(sizeAdapter);
        spinnerMarkerSize.setSelection(trackingValues.getMarkerSize() - 1);
        spinnerMarkerSize.setOnItemSelectedListener(this);

        // Spinner für Eckmarker
        ArrayAdapter<CharSequence> edgeAdapter = ArrayAdapter.createFromResource(
                this, R.array.edge_markers_array, android.R.layout.simple_spinner_item);
        edgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdgeMarkers.setAdapter(edgeAdapter);
        spinnerEdgeMarkers.setSelection(getEdgeMarkerPosition());
        spinnerEdgeMarkers.setOnItemSelectedListener(this);
    }

    private int getMarkerTypePosition() {
        TrackingValues.MarkerType type = trackingValues.getMarkerType();
        switch (type) {
            case PIE: return 0;
            case CIRCLE: return 1;
            case TRIANGLE: return 2;
            case CROSS: return 3;
            default: return 3;
        }
    }

    private int getEdgeMarkerPosition() {
        TrackingValues.EdgeMarkerType type = trackingValues.getEdgeMarker();
        switch (type) {
            case NONE: return 0;
            case CORNER: return 1;
            case SEMICIRCLE: return 2;
            default: return 0;
        }
    }

    private void setupButtons() {
        // Start-Button
        buttonStart.setOnClickListener(v -> {
            // Einstellungen speichern und Tracking-Screen starten
            Utilities.saveSettings(this, trackingValues);
            Toast.makeText(this, "Einstellungen gespeichert", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, Trackingscreen.class);
            intent.putExtra("trackingValues", trackingValues);
            startActivity(intent);
        });

        // Farb-Buttons
        buttonBackgroundColor.setOnClickListener(this::showBackgroundColorDialog);
        buttonMarkerColor.setOnClickListener(this::showMarkerColorDialog);
    }

    private void setupActivityLink() {
        footerText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * Berechnet die Helligkeit einer Farbe nach der Luminanz-Formel
     * @param color Die Farbe als int-Wert
     * @return true wenn die Farbe hell ist, false wenn dunkel
     */
    private boolean isColorBright(int color) {
        // RGB-Werte extrahieren
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        // Relative Luminanz berechnen (Perceptual Brightness)
        double luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0;

        // Schwellenwert für hell/dunkel (0.5 = 50%)
        return luminance > 0.5;
    }

    /**
     * Setzt die optimale Textfarbe basierend auf der Hintergrundfarbe
     * @param button Der Button, dessen Textfarbe angepasst werden soll
     * @param backgroundColor Die Hintergrundfarbe
     */
    private void setOptimalTextColor(Button button, int backgroundColor) {
        if (isColorBright(backgroundColor)) {
            // Helle Hintergrundfarbe -> Dunkler Text
            button.setTextColor(Color.BLACK);
        } else {
            // Dunkle Hintergrundfarbe -> Heller Text
            button.setTextColor(Color.WHITE);
        }
    }

    private void updateColorButtons() {
        // Hintergrundfarbe setzen
        int bgColor = Color.parseColor(trackingValues.getBackgroundColor());
        buttonBackgroundColor.setBackgroundColor(bgColor);
        setOptimalTextColor(buttonBackgroundColor, bgColor);

        // Markerfarbe setzen
        int markerColor = Color.parseColor(trackingValues.getMarkerColor());
        buttonMarkerColor.setBackgroundColor(markerColor);
        setOptimalTextColor(buttonMarkerColor, markerColor);
    }

    private void showMarkerColorDialog(View view) {
        new ColorPickerDialog.Builder(this)
                .setTitle("Marker-Farbe")
                .setPreferenceName("MarkerColorPref")
                .setPositiveButton("Bestätigen", (ColorEnvelopeListener) (envelope, fromUser) -> {
                    setMarkerColor(envelope);
                })
                .setNegativeButton("Abbrechen", (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show();
    }

    private void showBackgroundColorDialog(View view) {
        new ColorPickerDialog.Builder(this)
                .setTitle("Hintergrundfarbe")
                .setPreferenceName("BackgroundColorPref")
                .setPositiveButton("Bestätigen", (ColorEnvelopeListener) (envelope, fromUser) -> {
                    setBackgroundColor(envelope);
                })
                .setNegativeButton("Abbrechen", (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show();
    }

    private void setMarkerColor(ColorEnvelope envelope) {
        String hexCode = envelope.getHexCode();
        if (hexCode != null && !hexCode.isEmpty()) {
            String colorCode = "#" + hexCode;
            int color = Color.parseColor(colorCode);

            buttonMarkerColor.setBackgroundColor(color);
            setOptimalTextColor(buttonMarkerColor, color);

            trackingValues.setMarkerColor(colorCode);
            createPreview();
        }
    }

    private void setBackgroundColor(ColorEnvelope envelope) {
        String hexCode = envelope.getHexCode();
        if (hexCode != null && !hexCode.isEmpty()) {
            String colorCode = "#" + hexCode;
            int color = Color.parseColor(colorCode);

            buttonBackgroundColor.setBackgroundColor(color);
            setOptimalTextColor(buttonBackgroundColor, color);

            trackingValues.setBackgroundColor(colorCode);
            createPreview();
        }
    }

    private void createPreview() {
        cleanPreview();

        // Hintergrundfarbe setzen
        previewTrackingBackground.setBackgroundColor(
                Color.parseColor(trackingValues.getBackgroundColor()));

        // Marker basierend auf Dichte erstellen
        switch (trackingValues.getMarkerDensity()) {
            case 0:
                break;
            case 1:
                createMarkersForGroup(trackingPointList1);
                break;
            case 2:
                createMarkersForGroup(trackingPointList1);
                createMarkersForGroup(trackingPointList2);
                break;
            case 3:
                createMarkersForGroup(trackingPointList1);
                createMarkersForGroup(trackingPointList2);
                createMarkersForGroup(trackingPointList3);
                break;
        }

        // Eckmarker erstellen, falls ausgewählt
        if (trackingValues.getEdgeMarker() != TrackingValues.EdgeMarkerType.NONE) {
            createEdgeMarkersForGroup(trackingPointListE);
        }
    }

    private void createMarkersForGroup(ArrayList<ImageView> group) {
        int markerType = getMarkerDrawableResource();
        int markerSize = Utilities.getMarkerSize(trackingValues.getMarkerSize());
        int markerColor = Color.parseColor(trackingValues.getMarkerColor());

        for (ImageView marker : group) {
            marker.setImageResource(markerType);
            marker.getLayoutParams().height = markerSize;
            marker.getLayoutParams().width = markerSize;
            marker.setColorFilter(markerColor);
            marker.requestLayout();
        }
    }

    private void createEdgeMarkersForGroup(ArrayList<ImageView> group) {
        int edgeMarkerType = getEdgeMarkerDrawableResource();
        int markerColor = Color.parseColor(trackingValues.getMarkerColor());

        for (ImageView marker : group) {
            marker.setImageResource(edgeMarkerType);
            marker.setColorFilter(markerColor);
        }
    }

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

    private int getEdgeMarkerDrawableResource() {
        switch (trackingValues.getEdgeMarker()) {
            case CORNER:
                return R.drawable.ic_marker_cross_edge;
            case SEMICIRCLE:
                return R.drawable.ic_marker_circle_edge;
            default:
                return 0;
        }
    }

    private void cleanPreview() {
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == null) return;

        int parentId = parent.getId();
        if (parentId == R.id.spinner_edge_marker) {
            trackingValues.setEdgeMarker(parent.getItemAtPosition(position).toString());
        } else if (parentId == R.id.spinner_marker_density) {
            trackingValues.setMarkerDensity(parent.getItemAtPosition(position).toString());
        } else if (parentId == R.id.spinner_marker_size) {
            trackingValues.setMarkerSize(parent.getItemAtPosition(position).toString());
        } else if (parentId == R.id.spinner_marker_type) {
            trackingValues.setMarkerType(parent.getItemAtPosition(position).toString());
        }

        createPreview();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Nicht benötigt, aber durch Interface gefordert
    }

    @Override
    protected void onResume() {
        super.onResume();
        createPreview();
    }
}
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
 * Angepasst für separaten Scroll-Marker Layer und Mehrsprachigkeit
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button buttonBackgroundColor;
    private Button buttonMarkerColor;
    private Button buttonStart;
    private TextView footerText;
    private ConstraintLayout previewTrackingBackground;
    private ConstraintLayout previewScrollMarkerLayer;

    private Spinner spinnerMarkerDensity;
    private Spinner spinnerMarkerSize;
    private Spinner spinnerMarkerType;
    private Spinner spinnerEdgeMarkers;
    private Spinner spinnerScrollMarkers;

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
        previewScrollMarkerLayer = findViewById(R.id.scrollMarkerLayer);

        spinnerMarkerDensity = findViewById(R.id.spinner_marker_density);
        spinnerMarkerSize = findViewById(R.id.spinner_marker_size);
        spinnerMarkerType = findViewById(R.id.spinner_marker_type);
        spinnerEdgeMarkers = findViewById(R.id.spinner_edge_marker);
        spinnerScrollMarkers = findViewById(R.id.spinner_scroll_marker);
    }

    /**
     * Initialisiert die Listen für die verschiedenen Marker-Gruppen
     */
    private void initializeMarkerLists() {
        trackingPointList1 = new ArrayList<>();
        trackingPointList2 = new ArrayList<>();
        trackingPointList3 = new ArrayList<>();
        trackingPointListE = new ArrayList<>();

        // Gruppe 1 (Haupt-Layer)
        trackingPointList1.add(findViewById(R.id.trackingPoint_1_1));
        trackingPointList1.add(findViewById(R.id.trackingPoint_1_2));
        trackingPointList1.add(findViewById(R.id.trackingPoint_1_3));
        trackingPointList1.add(findViewById(R.id.trackingPoint_1_4));
        trackingPointList1.add(findViewById(R.id.trackingPoint_1_5));

        // Gruppe 2 (Haupt-Layer)
        trackingPointList2.add(findViewById(R.id.trackingPoint_2_1));
        trackingPointList2.add(findViewById(R.id.trackingPoint_2_2));
        trackingPointList2.add(findViewById(R.id.trackingPoint_2_3));
        trackingPointList2.add(findViewById(R.id.trackingPoint_2_4));

        // Gruppe 3 (Haupt-Layer)
        trackingPointList3.add(findViewById(R.id.trackingPoint_3_1));
        trackingPointList3.add(findViewById(R.id.trackingPoint_3_2));
        trackingPointList3.add(findViewById(R.id.trackingPoint_3_3));
        trackingPointList3.add(findViewById(R.id.trackingPoint_3_4));

        // Eckmarker (Haupt-Layer)
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

        // Spinner für Scroll-Marker
        ArrayAdapter<CharSequence> scrollAdapter = ArrayAdapter.createFromResource(
                this, R.array.scroll_markers_array, android.R.layout.simple_spinner_item);
        scrollAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerScrollMarkers.setAdapter(scrollAdapter);
        spinnerScrollMarkers.setSelection(getScrollMarkerPosition());
        spinnerScrollMarkers.setOnItemSelectedListener(this);
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

    private int getScrollMarkerPosition() {
        TrackingValues.ScrollMarkerType type = trackingValues.getScrollMarker();
        switch (type) {
            case NONE: return 0;
            case VERTICAL: return 1;
            case HORIZONTAL: return 2;
            default: return 0;
        }
    }

    private void setupButtons() {
        // Start-Button mit lokalisiertem Toast und Fallback
        buttonStart.setOnClickListener(v -> {
            // Einstellungen speichern und Tracking-Screen starten
            Utilities.saveSettings(this, trackingValues);

            String message = "Settings saved";
            try {
                message = getString(R.string.settings_saved);
            } catch (Exception e) {
                System.out.println("Using fallback string for settings saved message");
            }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, Trackingscreen.class);
            intent.putExtra("trackingValues", trackingValues);
            startActivity(intent);
        });

        // Farb-Buttons mit lokalisierten Dialogen
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

    private void showBackgroundColorDialog(View view) {
        String title = "Background Color";
        String confirm = "Confirm";
        String cancel = "Cancel";

        try {
            title = getString(R.string.color_picker_background_title);
            confirm = getString(R.string.color_picker_confirm);
            cancel = getString(R.string.color_picker_cancel);
        } catch (Exception e) {
            System.out.println("Using fallback strings for background color dialog");
        }

        new ColorPickerDialog.Builder(this)
                .setTitle(title)
                .setPreferenceName("BackgroundColorPref")
                .setPositiveButton(confirm, (ColorEnvelopeListener) (envelope, fromUser) -> {
                    setBackgroundColor(envelope);
                })
                .setNegativeButton(cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .show();
    }

    private void showMarkerColorDialog(View view) {
        String title = "Marker Color";
        String confirm = "Confirm";
        String cancel = "Cancel";

        try {
            title = getString(R.string.color_picker_marker_title);
            confirm = getString(R.string.color_picker_confirm);
            cancel = getString(R.string.color_picker_cancel);
        } catch (Exception e) {
            System.out.println("Using fallback strings for marker color dialog");
        }

        new ColorPickerDialog.Builder(this)
                .setTitle(title)
                .setPreferenceName("MarkerColorPref")
                .setPositiveButton(confirm, (ColorEnvelopeListener) (envelope, fromUser) -> {
                    setMarkerColor(envelope);
                })
                .setNegativeButton(cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .show();
    }

    private void setBackgroundColor(ColorEnvelope envelope) {
        String hexCode = envelope.getHexCode();
        if (hexCode != null && !hexCode.isEmpty()) {
            String colorCode = "#" + hexCode;

            try {
                int color = Color.parseColor(colorCode);

                buttonBackgroundColor.setBackgroundColor(color);
                setOptimalTextColor(buttonBackgroundColor, color);

                trackingValues.setBackgroundColor(colorCode);
                createPreview();

                System.out.println("Background color set to: " + colorCode);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid color code: " + colorCode);
                Toast.makeText(this, "Invalid color selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setMarkerColor(ColorEnvelope envelope) {
        String hexCode = envelope.getHexCode();
        if (hexCode != null && !hexCode.isEmpty()) {
            String colorCode = "#" + hexCode;

            try {
                int color = Color.parseColor(colorCode);

                buttonMarkerColor.setBackgroundColor(color);
                setOptimalTextColor(buttonMarkerColor, color);

                trackingValues.setMarkerColor(colorCode);
                createPreview();

                System.out.println("Marker color set to: " + colorCode);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid color code: " + colorCode);
                Toast.makeText(this, "Invalid color selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createPreview() {
        System.out.println("Creating preview with marker type: " + trackingValues.getMarkerType());

        cleanPreview();

        // Hintergrundfarbe setzen
        previewTrackingBackground.setBackgroundColor(
                Color.parseColor(trackingValues.getBackgroundColor()));

        // Marker basierend auf Dichte erstellen
        switch (trackingValues.getMarkerDensity()) {
            case 0:
                System.out.println("No markers (density 0)");
                break;
            case 1:
                System.out.println("Creating marker group 1");
                createMarkersForGroup(trackingPointList1);
                break;
            case 2:
                System.out.println("Creating marker groups 1 and 2");
                createMarkersForGroup(trackingPointList1);
                createMarkersForGroup(trackingPointList2);
                break;
            case 3:
                System.out.println("Creating marker groups 1, 2 and 3");
                createMarkersForGroup(trackingPointList1);
                createMarkersForGroup(trackingPointList2);
                createMarkersForGroup(trackingPointList3);
                break;
        }

        // Eckmarker erstellen, falls ausgewählt
        TrackingValues.EdgeMarkerType edgeType = trackingValues.getEdgeMarker();
        if (edgeType != TrackingValues.EdgeMarkerType.NONE) {
            System.out.println("Creating edge markers: " + edgeType);
            createEdgeMarkersForGroup(trackingPointListE);
        } else {
            System.out.println("No edge markers (NONE selected)");
            // Sicherstellen, dass Eckmarker ausgeblendet sind
            for (ImageView marker : trackingPointListE) {
                marker.setImageResource(0);
                marker.clearColorFilter();
            }
        }

        // Scroll-Marker Layer verwalten und erstellen
        createScrollMarkers();
    }

    /**
     * Zeigt eine Vorschau für aktivierte Scroll-Marker
     */
    private void createScrollMarkers() {
        TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

        // Scroll-Layer immer ausblenden in der Vorschau
        previewScrollMarkerLayer.setVisibility(View.GONE);

        // Keine Scroll-Marker in der Vorschau anzeigen
    }

    private void createMarkersForGroup(ArrayList<ImageView> group) {
        int markerType = getMarkerDrawableResource();
        int markerSize = Utilities.getMarkerSize(trackingValues.getMarkerSize());
        int markerColor = Color.parseColor(trackingValues.getMarkerColor());

        System.out.println("Creating markers - Type: " + trackingValues.getMarkerType() +
                ", Resource: " + markerType + ", Size: " + markerSize);

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

        System.out.println("Creating edge markers - Type: " + trackingValues.getEdgeMarker() +
                ", Resource: " + edgeMarkerType);

        for (ImageView marker : group) {
            marker.setImageResource(edgeMarkerType);
            marker.setColorFilter(markerColor);
        }
    }

    private int getMarkerDrawableResource() {
        TrackingValues.MarkerType currentType = trackingValues.getMarkerType();
        System.out.println("Getting drawable for marker type: " + currentType);

        switch (currentType) {
            case PIE:
                System.out.println("Returning PIE drawable");
                return R.drawable.ic_marker_pie;
            case CIRCLE:
                System.out.println("Returning CIRCLE drawable");
                return R.drawable.ic_marker_circle;
            case TRIANGLE:
                System.out.println("Returning TRIANGLE drawable");
                return R.drawable.ic_marker_triangle;
            case CROSS:
            default:
                System.out.println("Returning CROSS drawable (default)");
                return R.drawable.ic_marker_cross;
        }
    }

    private int getEdgeMarkerDrawableResource() {
        TrackingValues.EdgeMarkerType currentType = trackingValues.getEdgeMarker();
        System.out.println("Getting drawable for edge marker type: " + currentType);

        switch (currentType) {
            case CORNER:
                System.out.println("Returning CORNER edge drawable");
                return R.drawable.ic_marker_cross_edge;
            case SEMICIRCLE:
                System.out.println("Returning SEMICIRCLE edge drawable");
                return R.drawable.ic_marker_circle_edge;
            case NONE:
            default:
                System.out.println("No edge marker (NONE)");
                return 0;
        }
    }

    private void cleanPreview() {
        System.out.println("Cleaning preview - resetting all markers");

        // Alle Haupt-Layer Marker-Bilder zurücksetzen
        for (ImageView marker : trackingPointList1) {
            marker.setImageResource(0);
        }
        for (ImageView marker : trackingPointList2) {
            marker.setImageResource(0);
        }
        for (ImageView marker : trackingPointList3) {
            marker.setImageResource(0);
        }

        // Eckmarker explizit zurücksetzen
        for (ImageView marker : trackingPointListE) {
            marker.setImageResource(0);
            marker.clearColorFilter();
        }

        // Scroll-Marker zurücksetzen
        cleanScrollMarkers();

        System.out.println("Preview cleaned");
    }

    private void cleanScrollMarkers() {
        // Nichts zu tun, da keine Scroll-Marker in der Vorschau
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == null) return;

        int parentId = parent.getId();

        System.out.println("Spinner changed - ID: " + parentId + ", Position: " + position + ", Item: " + parent.getItemAtPosition(position));

        if (parentId == R.id.spinner_edge_marker) {
            // Eckmarker basierend auf Position setzen (sprachunabhängig)
            TrackingValues.EdgeMarkerType selectedEdge;
            switch (position) {
                case 0:
                    selectedEdge = TrackingValues.EdgeMarkerType.NONE;
                    break;
                case 1:
                    selectedEdge = TrackingValues.EdgeMarkerType.CORNER;
                    break;
                case 2:
                default:
                    selectedEdge = TrackingValues.EdgeMarkerType.SEMICIRCLE;
                    break;
            }
            trackingValues.setEdgeMarker(selectedEdge);
            System.out.println("Edge marker set to: " + selectedEdge + " (position: " + position + ")");
        } else if (parentId == R.id.spinner_scroll_marker) {
            // Scroll-Marker basierend auf Position setzen (sprachunabhängig)
            TrackingValues.ScrollMarkerType selectedScroll;
            switch (position) {
                case 0:
                    selectedScroll = TrackingValues.ScrollMarkerType.NONE;
                    break;
                case 1:
                    selectedScroll = TrackingValues.ScrollMarkerType.VERTICAL;
                    break;
                case 2:
                default:
                    selectedScroll = TrackingValues.ScrollMarkerType.HORIZONTAL;
                    break;
            }
            trackingValues.setScrollMarker(selectedScroll);
            System.out.println("Scroll marker set to: " + selectedScroll + " (position: " + position + ")");
        } else if (parentId == R.id.spinner_marker_density) {
            String selectedDensity = parent.getItemAtPosition(position).toString();
            trackingValues.setMarkerDensity(selectedDensity);
            System.out.println("Marker density set to: " + selectedDensity);
        } else if (parentId == R.id.spinner_marker_size) {
            String selectedSize = parent.getItemAtPosition(position).toString();
            trackingValues.setMarkerSize(selectedSize);
            System.out.println("Marker size set to: " + selectedSize);
        } else if (parentId == R.id.spinner_marker_type) {
            // Marker-Typ basierend auf Position setzen (sprachunabhängig)
            TrackingValues.MarkerType selectedType;
            switch (position) {
                case 0:
                    selectedType = TrackingValues.MarkerType.PIE;
                    break;
                case 1:
                    selectedType = TrackingValues.MarkerType.CIRCLE;
                    break;
                case 2:
                    selectedType = TrackingValues.MarkerType.TRIANGLE;
                    break;
                case 3:
                default:
                    selectedType = TrackingValues.MarkerType.CROSS;
                    break;
            }
            trackingValues.setMarkerType(selectedType);
            System.out.println("Marker type set to: " + selectedType + " (position: " + position + ")");
        }

        // Vorschau immer aktualisieren
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
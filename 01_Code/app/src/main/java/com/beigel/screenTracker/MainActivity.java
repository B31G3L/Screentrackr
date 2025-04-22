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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button buttonBackgroundColor;
    private Button buttonMarkerColor;
    private Button buttonStart;

    private ConstraintLayout previewTrackingBackground;
    private ImageView previewTrackingPoint1_1;
    private ImageView previewTrackingPoint1_2;
    private ImageView previewTrackingPoint1_3;
    private ImageView previewTrackingPoint1_4;
    private ImageView previewTrackingPoint1_5;
    private ImageView previewTrackingPoint2_1;
    private ImageView previewTrackingPoint2_2;
    private ImageView previewTrackingPoint2_3;
    private ImageView previewTrackingPoint2_4;
    private ImageView previewTrackingPoint3_1;
    private ImageView previewTrackingPoint3_2;
    private ImageView previewTrackingPoint3_3;
    private ImageView previewTrackingPoint3_4;
    private ImageView previewTrackingPointE_1;
    private ImageView previewTrackingPointE_2;
    private ImageView previewTrackingPointE_3;
    private ImageView previewTrackingPointE_4;

    private ArrayList<ImageView> trackingPointList1;
    private ArrayList<ImageView> trackingPointList2;
    private ArrayList<ImageView> trackingPointList3;
    private ArrayList<ImageView> trackingPointListE;

    private TrackingValues trackingValues;
    private Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trackingValues = new TrackingValues();
        utilities = new Utilities();

        buttonStart = findViewById(R.id.button_start);
        buttonBackgroundColor = findViewById(R.id.buttonBackgroundColor);
        buttonMarkerColor = findViewById(R.id.buttonMarkerColor);

        previewTrackingBackground = findViewById(R.id.trackingBackground);
        previewTrackingPoint1_1 = findViewById(R.id.trackingPoint_1_1);
        previewTrackingPoint1_2 = findViewById(R.id.trackingPoint_1_2);
        previewTrackingPoint1_3 = findViewById(R.id.trackingPoint_1_3);
        previewTrackingPoint1_4 = findViewById(R.id.trackingPoint_1_4);
        previewTrackingPoint1_5 = findViewById(R.id.trackingPoint_1_5);
        previewTrackingPoint2_1 = findViewById(R.id.trackingPoint_2_1);
        previewTrackingPoint2_2 = findViewById(R.id.trackingPoint_2_2);
        previewTrackingPoint2_3 = findViewById(R.id.trackingPoint_2_3);
        previewTrackingPoint2_4 = findViewById(R.id.trackingPoint_2_4);
        previewTrackingPoint3_1 = findViewById(R.id.trackingPoint_3_1);
        previewTrackingPoint3_2 = findViewById(R.id.trackingPoint_3_2);
        previewTrackingPoint3_3 = findViewById(R.id.trackingPoint_3_3);
        previewTrackingPoint3_4 = findViewById(R.id.trackingPoint_3_4);
        previewTrackingPointE_1 = findViewById(R.id.trackingPoint_E_1);
        previewTrackingPointE_2 = findViewById(R.id.trackingPoint_E_2);
        previewTrackingPointE_3 = findViewById(R.id.trackingPoint_E_3);
        previewTrackingPointE_4 = findViewById(R.id.trackingPoint_E_4);

        trackingPointList1 = new ArrayList<>();
        trackingPointList2 = new ArrayList<>();
        trackingPointList3 = new ArrayList<>();
        trackingPointListE = new ArrayList<>();

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

        Spinner spinnerMarkerDensity = findViewById(R.id.spinner_marker_density);
        Spinner spinnerMarkerSize = findViewById(R.id.spinner_marker_size);
        Spinner spinnerMarkerType = findViewById(R.id.spinner_marker_type);
        Spinner spinnerEdgeMarkers = findViewById(R.id.spinner_edge_marker);

        spinnerMarkerType.setOnItemSelectedListener(this);
        spinnerMarkerDensity.setOnItemSelectedListener(this);
        spinnerMarkerSize.setOnItemSelectedListener(this);
        spinnerEdgeMarkers.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> densityAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.marker_density_array,
                android.R.layout.simple_spinner_item
        );
        densityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarkerDensity.setAdapter(densityAdapter);
        spinnerMarkerDensity.setSelection(1);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.marker_type_array,
                android.R.layout.simple_spinner_item
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarkerType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.edge_size_array,
                android.R.layout.simple_spinner_item
        );
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMarkerSize.setAdapter(sizeAdapter);

        ArrayAdapter<CharSequence> edgeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.edge_markers_array,
                android.R.layout.simple_spinner_item
        );
        edgeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdgeMarkers.setAdapter(edgeAdapter);

        setupActivityLink();
    }

    private void setupActivityLink() {
        TextView linkTextView = findViewById(R.id.footer);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Trackingscreen.class);
                intent.putExtra("trackingValues", trackingValues);
                startActivity(intent);
            }
        });

        buttonBackgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBackgroundColorDialog(v);
            }
        });

        buttonMarkerColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMarkerColorDialog(v);
            }
        });

        fillValues();
        createPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        buttonBackgroundColor.setOnClickListener(null);
        buttonMarkerColor.setOnClickListener(null);
        buttonStart.setOnClickListener(null);
    }

    private void fillValues() {
        String backgroundColor = trackingValues.getBackgroundColor();
        String markerColor = trackingValues.getMarkerColor();

        if (backgroundColor != null) {
            buttonBackgroundColor.setBackgroundColor(Color.parseColor(backgroundColor));
        }

        if (markerColor != null) {
            buttonMarkerColor.setBackgroundColor(Color.parseColor(markerColor));
        }
    }

    private void showMarkerColorDialog(View view) {
        new ColorPickerDialog.Builder(this)
                .setTitle("Marker Color")
                .setPreferenceName("Test")
                .setPositiveButton("Confirm", new ColorEnvelopeListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                        setMarkerColor(envelope);
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show();
    }

    private void showBackgroundColorDialog(View view) {
        new ColorPickerDialog.Builder(this)
                .setTitle("Background Color")
                .setPreferenceName("Test")
                .setPositiveButton("Confirm", new ColorEnvelopeListener() {
                    @Override
                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                        setBackgroundColor(envelope);
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show();
    }

    private void setMarkerColor(ColorEnvelope envelope) {
        String hexCode = envelope.getHexCode();
        if (hexCode != null && !hexCode.isEmpty()) {
            buttonMarkerColor.setBackgroundColor(Color.parseColor("#" + hexCode));
            trackingValues.setMarkerColor("#" + hexCode);
            createPreview();
        }
    }

    private void setBackgroundColor(ColorEnvelope envelope) {
        String hexCode = envelope.getHexCode();
        if (hexCode != null && !hexCode.isEmpty()) {
            buttonBackgroundColor.setBackgroundColor(Color.parseColor("#" + hexCode));
            trackingValues.setBackgroundColor("#" + hexCode);
            createPreview();
        }
    }

    private void createPreview() {
        cleanPreview();
        if (trackingValues.getBackgroundColor() != null) {
            previewTrackingBackground.setBackgroundColor(Color.parseColor(trackingValues.getBackgroundColor()));
        }

        String markerDensity = trackingValues.getMarkerDensity();
        if (markerDensity != null) {
            switch (markerDensity) {
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
        }

        String edgeMarker = trackingValues.getEdgeMarker();
        if (edgeMarker != null && !edgeMarker.equals("None")) {
            utilities.createEdgeMarker(trackingPointListE, trackingValues);
        }
    }

    private void cleanPreview() {
        for (ImageView x : this.trackingPointList1) {
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent != null) {
            int parentId = parent.getId();
            if (parentId == R.id.spinner_edge_marker) {
                trackingValues.setEdgeMarker(parent.getItemAtPosition(position).toString());
                createPreview();
            } else if (parentId == R.id.spinner_marker_density) {
                trackingValues.setMarkerDensity(parent.getItemAtPosition(position).toString());
                createPreview();
            } else if (parentId == R.id.spinner_marker_size) {
                trackingValues.setMarkerSize(parent.getItemAtPosition(position).toString());
                createPreview();
            } else if (parentId == R.id.spinner_marker_type) {
                trackingValues.setMarkerType(parent.getItemAtPosition(position).toString());
                createPreview();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Required method from interface, but not used in this case
    }
}
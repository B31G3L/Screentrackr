package de.beigel.screentrackr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinnerMarkerDensity: Spinner = findViewById(R.id.spinner_marker_density)
        val spinnerMarkerSize: Spinner = findViewById(R.id.spinner_marker_size)
        val spinnerMarkerType: Spinner = findViewById(R.id.spinner_marker_type)
        val spinnerEdgeMarkers: Spinner = findViewById(R.id.spinner_edge_marker)
        
        
        
        
        ArrayAdapter.createFromResource(
            this,
            R.array.marker_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerMarkerType.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.edge_markers_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEdgeMarkers.adapter = adapter
        }


    }
}
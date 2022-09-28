package com.beigel.screenTracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var buttonBackgroundColor: Button
    private lateinit var buttonMarkerColor: Button
    private lateinit var buttonStart: Button

    private lateinit var previewTrackingBackground: ConstraintLayout
    private lateinit var previewTrackingPoint1_1: ImageView
    private lateinit var previewTrackingPoint1_2: ImageView
    private lateinit var previewTrackingPoint1_3: ImageView
    private lateinit var previewTrackingPoint1_4: ImageView
    private lateinit var previewTrackingPoint1_5: ImageView
    private lateinit var previewTrackingPoint2_1: ImageView
    private lateinit var previewTrackingPoint2_2: ImageView
    private lateinit var previewTrackingPoint2_3: ImageView
    private lateinit var previewTrackingPoint2_4: ImageView
    private lateinit var previewTrackingPoint3_1: ImageView
    private lateinit var previewTrackingPoint3_2: ImageView
    private lateinit var previewTrackingPoint3_3: ImageView
    private lateinit var previewTrackingPoint3_4: ImageView
    private lateinit var previewTrackingPointE_1: ImageView
    private lateinit var previewTrackingPointE_2: ImageView
    private lateinit var previewTrackingPointE_3: ImageView
    private lateinit var previewTrackingPointE_4: ImageView

    private lateinit var trackingPointList1: ArrayList<ImageView>
    private lateinit var trackingPointList2: ArrayList<ImageView>

    private lateinit var trackingPointList3: ArrayList<ImageView>
    private lateinit var trackingPointListE: ArrayList<ImageView>




    private lateinit var  trackingValues: TrackingValues
    private lateinit var  utilities: Utilities




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        trackingValues = TrackingValues();
        utilities = Utilities();

        buttonStart = findViewById(R.id.button_start)
        buttonBackgroundColor = findViewById(R.id.buttonBackgroundColor)
        buttonMarkerColor = findViewById(R.id.buttonMarkerColor)

        previewTrackingBackground = findViewById(R.id.trackingBackground)
        previewTrackingPoint1_1 = findViewById(R.id.trackingPoint_1_1)
        previewTrackingPoint1_2 = findViewById(R.id.trackingPoint_1_2)
        previewTrackingPoint1_3 = findViewById(R.id.trackingPoint_1_3)
        previewTrackingPoint1_4 = findViewById(R.id.trackingPoint_1_4)
        previewTrackingPoint1_5 = findViewById(R.id.trackingPoint_1_5)
        previewTrackingPoint2_1 = findViewById(R.id.trackingPoint_2_1)
        previewTrackingPoint2_2 = findViewById(R.id.trackingPoint_2_2)
        previewTrackingPoint2_3 = findViewById(R.id.trackingPoint_2_3)
        previewTrackingPoint2_4 = findViewById(R.id.trackingPoint_2_4)
        previewTrackingPoint3_1 = findViewById(R.id.trackingPoint_3_1)
        previewTrackingPoint3_2 = findViewById(R.id.trackingPoint_3_2)
        previewTrackingPoint3_3 = findViewById(R.id.trackingPoint_3_3)
        previewTrackingPoint3_4 = findViewById(R.id.trackingPoint_3_4)
        previewTrackingPointE_1 = findViewById(R.id.trackingPoint_E_1)
        previewTrackingPointE_2 = findViewById(R.id.trackingPoint_E_2)
        previewTrackingPointE_3 = findViewById(R.id.trackingPoint_E_3)
        previewTrackingPointE_4 = findViewById(R.id.trackingPoint_E_4)
        trackingPointList1 = ArrayList()
        trackingPointList2 = ArrayList()

        trackingPointList3 = ArrayList()
        trackingPointListE = ArrayList()


        trackingPointList1.add(previewTrackingPoint1_1)
        trackingPointList1.add(previewTrackingPoint1_2)
        trackingPointList1.add(previewTrackingPoint1_3)
        trackingPointList1.add(previewTrackingPoint1_4)
        trackingPointList1.add(previewTrackingPoint1_5)
        trackingPointList2.add(previewTrackingPoint2_1)
        trackingPointList2.add(previewTrackingPoint2_2)
        trackingPointList2.add(previewTrackingPoint2_3)
        trackingPointList2.add(previewTrackingPoint2_4)
        trackingPointList3.add(previewTrackingPoint3_1)
        trackingPointList3.add(previewTrackingPoint3_2)
        trackingPointList3.add(previewTrackingPoint3_3)
        trackingPointList3.add(previewTrackingPoint3_4)
        trackingPointListE.add(previewTrackingPointE_1)
        trackingPointListE.add(previewTrackingPointE_2)
        trackingPointListE.add(previewTrackingPointE_3)
        trackingPointListE.add(previewTrackingPointE_4)

        val spinnerMarkerDensity: Spinner = findViewById(R.id.spinner_marker_density)
        val spinnerMarkerSize: Spinner = findViewById(R.id.spinner_marker_size)
        val spinnerMarkerType: Spinner = findViewById(R.id.spinner_marker_type)
        val spinnerEdgeMarkers: Spinner = findViewById(R.id.spinner_edge_marker)

        spinnerMarkerType.setOnItemSelectedListener(this)
        spinnerMarkerDensity.setOnItemSelectedListener(this)
        spinnerMarkerSize.setOnItemSelectedListener(this)
        spinnerEdgeMarkers.setOnItemSelectedListener(this)


        ArrayAdapter.createFromResource(
            this,
            R.array.marker_density_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerMarkerDensity.adapter = adapter
        }
        spinnerMarkerDensity.setSelection(1)
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
            R.array.edge_size_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerMarkerSize.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.edge_markers_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEdgeMarkers.adapter = adapter
        }

        setupActivityLink()

    }
    private fun setupActivityLink() {
        val linkTextView = findViewById<TextView>(R.id.footer)
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    override fun onResume() {
        super.onResume()
        buttonStart.setOnClickListener {
            val intent = Intent(this, Trackingscreen::class.java)
            intent.putExtra("trackingValues", trackingValues)
            startActivity(intent)
        }

        buttonBackgroundColor.setOnClickListener {
            showBackgroundColorDialog(it)
        }

        buttonMarkerColor.setOnClickListener {
            showMarkerColorDialog(it)
        }

        
        fillValues()
        createPreview()
    }

    override fun onPause() {
        super.onPause()
        buttonBackgroundColor.setOnClickListener(null)
        buttonMarkerColor.setOnClickListener(null)
        buttonStart.setOnClickListener(null)
    }


    private fun fillValues(){
        buttonBackgroundColor.setBackgroundColor(Color.parseColor(trackingValues.backgroundColor))
        buttonMarkerColor.setBackgroundColor(Color.parseColor(trackingValues.markerColor))
    }




    private fun showMarkerColorDialog(view: View) {
        val builder = ColorPickerDialog.Builder(this)
            .setTitle("Marker Color")
            .setPreferenceName("Test")
            .setPositiveButton("Confirm",
                ColorEnvelopeListener { envelope, _ -> setMarkerColor(envelope)
                }
            )
            .setNegativeButton("Cancel"
            ) { dialogInterface, i -> dialogInterface.dismiss() }
        builder.colorPickerView.flagView = BubbleFlag(this).apply { flagMode = FlagMode.FADE }
        builder.show()
    }

    private fun showBackgroundColorDialog(view: View) {
        val builder = ColorPickerDialog.Builder(this)
            .setTitle("Background Color")
            .setPreferenceName("Test")
            .setPositiveButton("Confirm",
                ColorEnvelopeListener { envelope, _ -> setBackgroundColor(envelope)
                }
            )
            .setNegativeButton("Cancel"
            ) { dialogInterface, i -> dialogInterface.dismiss() }
        builder.colorPickerView.flagView = BubbleFlag(this).apply { flagMode = FlagMode.FADE }
        builder.show()
    }


    private fun setMarkerColor(envelope: ColorEnvelope) {
        buttonMarkerColor.setBackgroundColor(Color.parseColor("#${envelope.hexCode}"))
        trackingValues.markerColor="#${envelope.hexCode}"
        createPreview()
    }

    private fun setBackgroundColor(envelope: ColorEnvelope) {
        buttonBackgroundColor.setBackgroundColor(Color.parseColor("#${envelope.hexCode}"))
        trackingValues.backgroundColor="#${envelope.hexCode}"
        createPreview()
    }

    private fun createPreview(){
        cleanPreview()
        previewTrackingBackground.setBackgroundColor(Color.parseColor(trackingValues.backgroundColor))

        when (trackingValues.markerDensity) {
            "0" -> {}
            "1" -> {
                utilities.createMarker(trackingPointList1, trackingValues)
            }
            "2" -> {
                utilities.createMarker(trackingPointList1, trackingValues)
                utilities.createMarker(trackingPointList2, trackingValues)
            }
            "3" -> {
                utilities.createMarker(trackingPointList1, trackingValues)
                utilities.createMarker(trackingPointList2, trackingValues)
                utilities.createMarker(trackingPointList3, trackingValues)
            }
        }
        if (trackingValues.edgeMarker!="None"){
            utilities.createEdgeMarker(trackingPointListE, trackingValues)
        }
    }




    private fun cleanPreview() {
        for(x in this.trackingPointList1){
            x.setImageResource(0);
        }
        for(x in trackingPointList2){
            x.setImageResource(0);
        }
        for(x in trackingPointList3){
            x.setImageResource(0);
        }
        for(x in trackingPointListE){
            x.setImageResource(0);
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0 != null) {
            when (p0.id) {
                R.id.spinner_edge_marker -> {
                    trackingValues.edgeMarker= p0.getItemAtPosition(p2).toString()
                    createPreview()
                }
                R.id.spinner_marker_density -> {
                    trackingValues.markerDensity= p0.getItemAtPosition(p2).toString()
                    createPreview()
                }
                R.id.spinner_marker_size -> {
                    trackingValues.markerSize= p0.getItemAtPosition(p2).toString()
                    createPreview()
                }
                R.id.spinner_marker_type -> {
                    trackingValues.markerType= p0.getItemAtPosition(p2).toString()
                    createPreview()
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}

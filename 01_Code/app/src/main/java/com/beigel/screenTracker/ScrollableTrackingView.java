package com.beigel.screenTracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.core.content.ContextCompat;

/**
 * Custom View für unendlich scrollbare Tracking-Marker
 */
public class ScrollableTrackingView extends View {
    private Paint backgroundPaint;
    private Paint markerPaint;
    private TrackingValues trackingValues;

    // Scroll-Variablen
    private float scrollX = 0f;
    private float scrollY = 0f;
    private float lastTouchX;
    private float lastTouchY;
    private boolean isScrolling = false;
    private int touchSlop;

    // Marker-Eigenschaften
    private float markerSpacingX = 300f;  // Erhöht von 200f
    private float markerSpacingY = 250f;  // Erhöht von 150f
    private int markerSize = 40;
    private Drawable markerDrawable;

    public ScrollableTrackingView(Context context) {
        super(context);
        init();
    }

    public ScrollableTrackingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Touch-Konfiguration
        ViewConfiguration config = ViewConfiguration.get(getContext());
        touchSlop = config.getScaledTouchSlop();

        // Paint-Objekte initialisieren
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);

        markerPaint = new Paint();
        markerPaint.setColor(Color.WHITE);
        markerPaint.setAntiAlias(true);

        // Standard-Werte setzen
        trackingValues = new TrackingValues();
        updateMarkerDrawable();
    }

    public void setTrackingValues(TrackingValues values) {
        this.trackingValues = values;

        // Farben aktualisieren
        backgroundPaint.setColor(Color.parseColor(trackingValues.getBackgroundColor()));
        markerPaint.setColor(Color.parseColor(trackingValues.getMarkerColor()));

        // Marker-Größe aktualisieren
        markerSize = Utilities.getMarkerSize(trackingValues.getMarkerSize());

        // Marker-Drawable aktualisieren
        updateMarkerDrawable();

        invalidate();
    }

    private void updateMarkerDrawable() {
        int drawableRes = getMarkerDrawableResource();
        if (drawableRes != 0) {
            markerDrawable = ContextCompat.getDrawable(getContext(), drawableRes);
            if (markerDrawable != null) {
                markerDrawable.setTint(Color.parseColor(trackingValues.getMarkerColor()));
            }
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Hintergrund zeichnen
        canvas.drawColor(Color.parseColor(trackingValues.getBackgroundColor()));

        // Nur zeichnen, wenn Scroll-Marker aktiviert sind
        if (trackingValues.getScrollMarker() == TrackingValues.ScrollMarkerType.NONE) {
            return;
        }

        // Zuerst scrollbare Marker zeichnen
        drawScrollMarkers(canvas);

        // Dann feste Marker darüber zeichnen
        drawStaticMarkers(canvas);
    }

    private void drawScrollMarkers(Canvas canvas) {
        if (markerDrawable == null) return;

        int width = getWidth();
        int height = getHeight();

        TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

        if (scrollType == TrackingValues.ScrollMarkerType.VERTICAL) {
            drawVerticalMarkers(canvas, width, height);
        } else if (scrollType == TrackingValues.ScrollMarkerType.HORIZONTAL) {
            drawHorizontalMarkers(canvas, width, height);
        }
    }

    private void drawVerticalMarkers(Canvas canvas, int width, int height) {
        // Berechne Start- und End-Y-Positionen für sichtbaren Bereich
        float startY = scrollY - markerSpacingY;
        float endY = scrollY + height + markerSpacingY;

        // Marker in einem Raster zeichnen
        for (float y = startY - (startY % markerSpacingY); y <= endY; y += markerSpacingY) {
            for (float x = markerSpacingX / 2; x < width; x += markerSpacingX) {
                drawMarkerAt(canvas, x, y - scrollY);
            }
        }
    }

    private void drawHorizontalMarkers(Canvas canvas, int width, int height) {
        // Berechne Start- und End-X-Positionen für sichtbaren Bereich
        float startX = scrollX - markerSpacingX;
        float endX = scrollX + width + markerSpacingX;

        // Marker in einem Raster zeichnen
        for (float x = startX - (startX % markerSpacingX); x <= endX; x += markerSpacingX) {
            for (float y = markerSpacingY / 2; y < height; y += markerSpacingY) {
                drawMarkerAt(canvas, x - scrollX, y);
            }
        }
    }

    private void drawMarkerAt(Canvas canvas, float x, float y) {
        if (markerDrawable != null) {
            int halfSize = markerSize / 2;
            markerDrawable.setBounds(
                    (int)(x - halfSize),
                    (int)(y - halfSize),
                    (int)(x + halfSize),
                    (int)(y + halfSize)
            );
            markerDrawable.draw(canvas);
        }
    }

    /**
     * Zeichnet die festen Marker (normale Marker + Eckmarker)
     */
    private void drawStaticMarkers(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        // Normale Marker basierend auf Dichte zeichnen
        drawDensityMarkers(canvas, width, height);

        // Eckmarker zeichnen, falls aktiviert
        drawEdgeMarkers(canvas, width, height);
    }

    /**
     * Zeichnet die normalen Marker basierend auf der Dichte-Einstellung
     */
    private void drawDensityMarkers(Canvas canvas, int width, int height) {
        int density = trackingValues.getMarkerDensity();

        if (density == 0) return;

        // Positions-Definitionen basierend auf dem ursprünglichen Layout
        // Guidelines: left=10%, right=90%, top=5%, bottom=95%
        float leftGuideline = width * 0.10f;
        float rightGuideline = width * 0.90f;
        float topGuideline = height * 0.05f;
        float bottomGuideline = height * 0.95f;
        float centerX = width * 0.5f;
        float centerY = height * 0.5f;

        // Gruppe 1 Marker (immer bei Dichte >= 1)
        if (density >= 1) {
            drawMarkerAt(canvas, leftGuideline, topGuideline);     // trackingPoint_1_1
            drawMarkerAt(canvas, rightGuideline, topGuideline);    // trackingPoint_1_2
            drawMarkerAt(canvas, leftGuideline, bottomGuideline);  // trackingPoint_1_3
            drawMarkerAt(canvas, rightGuideline, bottomGuideline); // trackingPoint_1_4
            drawMarkerAt(canvas, centerX, centerY);                // trackingPoint_1_5
        }

        // Gruppe 2 Marker (bei Dichte >= 2)
        if (density >= 2) {
            drawMarkerAt(canvas, centerX, topGuideline);           // trackingPoint_2_1
            drawMarkerAt(canvas, rightGuideline, centerY);         // trackingPoint_2_2
            drawMarkerAt(canvas, centerX, bottomGuideline);        // trackingPoint_2_3
            drawMarkerAt(canvas, leftGuideline, centerY);          // trackingPoint_2_4
        }

        // Gruppe 3 Marker (bei Dichte >= 3)
        if (density >= 3) {
            float quarterX1 = leftGuideline + (centerX - leftGuideline) * 0.5f;
            float quarterX2 = centerX + (rightGuideline - centerX) * 0.5f;
            float quarterY1 = topGuideline + (centerY - topGuideline) * 0.5f;
            float quarterY2 = centerY + (bottomGuideline - centerY) * 0.5f;

            drawMarkerAt(canvas, quarterX1, quarterY1);            // trackingPoint_3_1
            drawMarkerAt(canvas, quarterX2, quarterY1);            // trackingPoint_3_2
            drawMarkerAt(canvas, quarterX2, quarterY2);            // trackingPoint_3_3
            drawMarkerAt(canvas, quarterX1, quarterY2);            // trackingPoint_3_4
        }
    }

    /**
     * Zeichnet die Eckmarker, falls aktiviert - genau wie in der Preview
     */
    private void drawEdgeMarkers(Canvas canvas, int width, int height) {
        if (trackingValues.getEdgeMarker() == TrackingValues.EdgeMarkerType.NONE) {
            return;
        }

        // Edge-Marker-Drawable laden
        Drawable edgeDrawable = getEdgeMarkerDrawable();
        if (edgeDrawable == null) return;

        int edgeSize = 25; // Gleiche Größe wie in der Preview

        // Ecken definieren - genau wie im Layout positioning
        int[][] corners = {
                {0, 0},                    // Oben links
                {width, 0},                // Oben rechts
                {0, height},               // Unten links
                {width, height}            // Unten rechts
        };

        for (int[] corner : corners) {
            int x = corner[0];
            int y = corner[1];

            edgeDrawable.setBounds(
                    x - edgeSize/2,
                    y - edgeSize/2,
                    x + edgeSize/2,
                    y + edgeSize/2
            );
            edgeDrawable.draw(canvas);
        }
    }

    /**
     * Gibt das entsprechende Edge-Marker-Drawable zurück
     */
    private Drawable getEdgeMarkerDrawable() {
        int drawableRes = 0;

        switch (trackingValues.getEdgeMarker()) {
            case CORNER:
                drawableRes = R.drawable.ic_marker_cross_edge;
                break;
            case SEMICIRCLE:
                drawableRes = R.drawable.ic_marker_circle_edge;
                break;
            default:
                return null;
        }

        Drawable drawable = ContextCompat.getDrawable(getContext(), drawableRes);
        if (drawable != null) {
            drawable.setTint(Color.parseColor(trackingValues.getMarkerColor()));
        }
        return drawable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Nur scrollen, wenn Scroll-Marker aktiviert sind
        if (trackingValues.getScrollMarker() == TrackingValues.ScrollMarkerType.NONE) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                isScrolling = false;
                return true;

            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - lastTouchX;
                float deltaY = event.getY() - lastTouchY;

                // Prüfen ob Scroll-Bewegung groß genug ist
                if (!isScrolling) {
                    if (Math.abs(deltaX) > touchSlop || Math.abs(deltaY) > touchSlop) {
                        isScrolling = true;
                    }
                }

                if (isScrolling) {
                    TrackingValues.ScrollMarkerType scrollType = trackingValues.getScrollMarker();

                    if (scrollType == TrackingValues.ScrollMarkerType.VERTICAL) {
                        // Nur vertikales Scrollen
                        scrollY -= deltaY;
                        // Unendliches Scrollen durch Modulo
                        scrollY = scrollY % (markerSpacingY * 10);
                        if (scrollY < 0) scrollY += (markerSpacingY * 10);
                    } else if (scrollType == TrackingValues.ScrollMarkerType.HORIZONTAL) {
                        // Nur horizontales Scrollen
                        scrollX -= deltaX;
                        // Unendliches Scrollen durch Modulo
                        scrollX = scrollX % (markerSpacingX * 10);
                        if (scrollX < 0) scrollX += (markerSpacingX * 10);
                    }

                    invalidate();
                }

                lastTouchX = event.getX();
                lastTouchY = event.getY();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!isScrolling) {
                    // Single Tap - Activity beenden
                    if (getContext() instanceof Trackingscreen) {
                        ((Trackingscreen) getContext()).finish();
                    }
                }
                isScrolling = false;
                return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Setzt die Abstände zwischen den Markern
     */
    public void setMarkerSpacing(float spacingX, float spacingY) {
        // Anpassung basierend auf der Dichte der festen Marker
        float densityFactor = getDensityAdjustmentFactor();

        this.markerSpacingX = spacingX * densityFactor;
        this.markerSpacingY = spacingY * densityFactor;
        invalidate();
    }

    /**
     * Berechnet einen Anpassungsfaktor basierend auf der Dichte der festen Marker
     */
    private float getDensityAdjustmentFactor() {
        int fixedMarkerDensity = trackingValues.getMarkerDensity();

        switch (fixedMarkerDensity) {
            case 0:
                return 0.8f; // Weniger Abstand wenn keine festen Marker
            case 1:
                return 1.0f; // Normal
            case 2:
                return 1.3f; // Mehr Abstand bei mittlerer Dichte
            case 3:
                return 1.6f; // Noch mehr Abstand bei hoher Dichte
            default:
                return 1.0f;
        }
    }

    /**
     * Gibt die aktuelle Scroll-Position zurück
     */
    public float[] getScrollPosition() {
        return new float[]{scrollX, scrollY};
    }

    /**
     * Setzt die Scroll-Position
     */
    public void setScrollPosition(float x, float y) {
        this.scrollX = x;
        this.scrollY = y;
        invalidate();
    }
}
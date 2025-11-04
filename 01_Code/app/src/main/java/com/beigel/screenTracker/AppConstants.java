package com.beigel.screenTracker;

/**
 * Zentralisierte Konstanten für die Screentrackr-App
 * Ersetzt verstreute Magic Numbers und String-Literale
 */
public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("Constants class");
    }

    // ========== COLORS ==========
    public static final String DEFAULT_BACKGROUND_COLOR = "#000000";
    public static final String DEFAULT_MARKER_COLOR = "#FFFFFF";
    public static final String PRIMARY_COLOR = "#00FF00";

    // ========== MARKER SIZES ==========
    public static final class MarkerSizes {
        public static final int SIZE_1 = 40;
        public static final int SIZE_2 = 50;
        public static final int SIZE_3 = 60;
        public static final int SIZE_4 = 70;
        public static final int SIZE_5 = 80;
        public static final int DEFAULT_SIZE = SIZE_1;

        // Edge Marker Größen (separate Skalierung)
        public static final int EDGE_SIZE_1 = 25;  // Original Größe
        public static final int EDGE_SIZE_2 = 40;
        public static final int EDGE_SIZE_3 = 60;
        public static final int EDGE_SIZE_4 = 80;
        public static final int EDGE_SIZE_5 = 100;
        public static final int DEFAULT_EDGE_SIZE = EDGE_SIZE_1;
    }

    // ========== SCROLL SYSTEM ==========
    public static final class Scrolling {
        public static final int MARKER_SPACING = 800;
        public static final int MAX_MARKERS = 200;
        public static final int CLEANUP_DISTANCE = MARKER_SPACING * 6;
        public static final int GENERATION_BUFFER = MARKER_SPACING * 4;

        // Momentum Scrolling - ANGEPASST FÜR LANGSAMERES SCROLLEN
        public static final float DECELERATION = 12000f;       // Höher = langsamer (war 3500f im Code)
        public static final int MOMENTUM_DURATION = 2500;     // Längere Dauer = langsameres Scrollen (war 1200ms)
        public static final float MIN_VELOCITY = 200f;        // Höhere Mindestgeschwindigkeit (war 100f)
        public static final float INTERPOLATOR_FACTOR = 3.0f; // DecelerateInterpolator Faktor

        // Base Positions
        public static final float[] VERTICAL_BASE_X = {-0.25f, 0.25f};
        public static final float[] VERTICAL_BASE_Y = {0.0f, 0.0f};
        public static final float[] HORIZONTAL_BASE_X = {0.0f, 0.0f};
        public static final float[] HORIZONTAL_BASE_Y = {-0.25f, 0.25f};
    }

    // ========== SHARED PREFERENCES ==========
    public static final class Prefs {
        public static final String NAME = "ScreentrackrPrefs";

        public static final String KEY_BACKGROUND_COLOR = "backgroundColor";
        public static final String KEY_MARKER_COLOR = "markerColor";
        public static final String KEY_MARKER_DENSITY = "markerDensity";
        public static final String KEY_MARKER_SIZE = "markerSize";
        public static final String KEY_MARKER_TYPE = "markerType";
        public static final String KEY_EDGE_MARKER = "edgeMarker";
        public static final String KEY_EDGE_MARKER_SIZE = "edgeMarkerSize";

        public static final String KEY_SCROLL_MARKER = "scrollMarker";
    }

    // ========== UI CONSTANTS ==========
    public static final class UI {
        public static final int EDGE_MARKER_SIZE = 25;
        public static final float COLOR_BRIGHTNESS_THRESHOLD = 0.5f;

        // Layout Percentages
        public static final float GUIDELINE_LEFT = 0.10f;
        public static final float GUIDELINE_RIGHT = 0.90f;
        public static final float GUIDELINE_TOP = 0.05f;
        public static final float GUIDELINE_BOTTOM = 0.95f;
    }

    // ========== MARKER DRAWABLES ==========
    public static final class MarkerDrawables {
        public static final int PIE = R.drawable.ic_marker_pie;
        public static final int CIRCLE = R.drawable.ic_marker_circle;
        public static final int TRIANGLE = R.drawable.ic_marker_triangle;
        public static final int CROSS = R.drawable.ic_marker_cross;

        // Edge Markers
        public static final int EDGE_CORNER = R.drawable.ic_marker_cross_edge;
        public static final int EDGE_SEMICIRCLE = R.drawable.ic_marker_circle_edge;
    }

    // ========== VALIDATION ==========
    public static final class Validation {
        public static final String HEX_COLOR_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$";

        public static final int MIN_MARKER_DENSITY = 0;
        public static final int MAX_MARKER_DENSITY = 3;

        public static final int MIN_MARKER_SIZE = 1;
        public static final int MAX_MARKER_SIZE = 5;

        public static final int MIN_EDGE_MARKER_SIZE = 1;
        public static final int MAX_EDGE_MARKER_SIZE = 5;
    }

    // ========== URLS ==========
    public static final class Urls {
        public static final String BEIGEL_STORE = "https://play.google.com/store/apps/developer?id=Beigel";
        public static final String OVERMIND_STUDIOS = "https://www.overmind-studios.de/";
        public static final String BROWSER_VERSION = "https://www.overmind-studios.de/screentrackr";
    }

    // ========== LOGGING TAGS ==========
    public static final class LogTags {
        public static final String MAIN = "Screentrackr_Main";
        public static final String TRACKING = "Screentrackr_Tracking";
        public static final String MARKERS = "Screentrackr_Markers";
        public static final String SCROLL = "Screentrackr_Scroll";
        public static final String SETTINGS = "Screentrackr_Settings";
    }
}
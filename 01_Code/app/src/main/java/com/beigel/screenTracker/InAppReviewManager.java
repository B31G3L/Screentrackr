package com.beigel.screenTracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.gms.tasks.Task;
/**
 * Moderner In-App Review Manager mit Google Play In-App Review API
 * - Zeigt natives Google Play Bewertungs-Popup INNERHALB der App
 * - Kein Verlassen der App notwendig
 * - Intelligentes Timing basierend auf Nutzungsverhalten
 * - Respektiert Google Play Kontingente
 */
public class InAppReviewManager {

    private static final String TAG = "InAppReviewManager";
    private static final String PREFS_NAME = "InAppReviewPrefs";

    // Tracking Keys
    private static final String KEY_LAUNCH_COUNT = "launch_count";
    private static final String KEY_FIRST_LAUNCH = "first_launch_time";
    private static final String KEY_LAST_REVIEW_REQUEST = "last_review_request";
    private static final String KEY_REVIEW_COMPLETED = "review_completed";
    private static final String KEY_NEVER_SHOW = "never_show_again";

    // Konfiguration - Wann soll die Bewertung erscheinen?
    private static final int MIN_LAUNCHES_UNTIL_PROMPT = 3;      // Nach 3 App-Starts
    private static final long MIN_DAYS_UNTIL_PROMPT = 2;         // Nach 2 Tagen
    private static final long MIN_DAYS_BETWEEN_PROMPTS = 90;     // 90 Tage zwischen Aufforderungen

    private final Context context;
    private final SharedPreferences prefs;
    private final ReviewManager reviewManager;

    public InAppReviewManager(@NonNull Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.reviewManager = ReviewManagerFactory.create(context);
    }

    /**
     * Wird bei jedem App-Start aufgerufen
     * Prüft automatisch, ob die Bewertungsaufforderung gezeigt werden soll
     */
    public void onAppLaunched() {
        // Launch Count erhöhen
        incrementLaunchCount();

        // Ersten Launch-Zeitpunkt speichern
        ensureFirstLaunchTimeSet();

        // Prüfen ob Bedingungen erfüllt sind und Review anzeigen
        if (shouldShowReviewPrompt()) {
            requestReview();
        }
    }

    /**
     * Zeigt die Bewertungsaufforderung SOFORT (für Testing oder nach spezifischen Events)
     * Ignoriert normale Timing-Bedingungen
     */
    public void showReviewPromptNow() {
        if (prefs.getBoolean(KEY_NEVER_SHOW, false)) {
            Log.d(TAG, "Review wurde vom Nutzer permanent abgelehnt");
            return;
        }

        Log.d(TAG, "Manueller Review-Request");
        requestReview();
    }

    /**
     * Prüft ob alle Bedingungen für die Bewertungsaufforderung erfüllt sind
     */
    public boolean shouldShowReviewPrompt() {
        // Nie mehr anzeigen?
        if (prefs.getBoolean(KEY_NEVER_SHOW, false)) {
            Log.d(TAG, "Review wurde permanent deaktiviert");
            return false;
        }

        // Bereits bewertet?
        if (prefs.getBoolean(KEY_REVIEW_COMPLETED, false)) {
            Log.d(TAG, "Review bereits abgeschlossen");
            return false;
        }

        // Genug App-Starts?
        long launchCount = prefs.getLong(KEY_LAUNCH_COUNT, 0);
        if (launchCount < MIN_LAUNCHES_UNTIL_PROMPT) {
            Log.d(TAG, String.format("Nicht genug Launches: %d/%d", launchCount, MIN_LAUNCHES_UNTIL_PROMPT));
            return false;
        }

        // Genug Tage seit erstem Start?
        long firstLaunch = prefs.getLong(KEY_FIRST_LAUNCH, 0);
        long daysSinceFirstLaunch = (System.currentTimeMillis() - firstLaunch) / (1000 * 60 * 60 * 24);
        if (daysSinceFirstLaunch < MIN_DAYS_UNTIL_PROMPT) {
            Log.d(TAG, String.format("Nicht genug Tage: %d/%d", daysSinceFirstLaunch, MIN_DAYS_UNTIL_PROMPT));
            return false;
        }

        // Genug Zeit seit letzter Aufforderung?
        long lastRequest = prefs.getLong(KEY_LAST_REVIEW_REQUEST, 0);
        if (lastRequest > 0) {
            long daysSinceLastRequest = (System.currentTimeMillis() - lastRequest) / (1000 * 60 * 60 * 24);
            if (daysSinceLastRequest < MIN_DAYS_BETWEEN_PROMPTS) {
                Log.d(TAG, String.format("Zu früh nach letztem Request: %d/%d Tage",
                        daysSinceLastRequest, MIN_DAYS_BETWEEN_PROMPTS));
                return false;
            }
        }

        Log.d(TAG, "Alle Bedingungen erfüllt - zeige Review");
        return true;
    }

    /**
     * Fordert die Bewertung mit der Google Play In-App Review API an
     */
    private void requestReview() {
        if (!(context instanceof Activity)) {
            Log.e(TAG, "Context ist keine Activity - Review nicht möglich");
            return;
        }

        Activity activity = (Activity) context;

        // Review-Flow vorbereiten
        Task<ReviewInfo> reviewInfoTask = reviewManager.requestReviewFlow();

        reviewInfoTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // ReviewInfo erhalten - starte den Review-Flow
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> reviewFlowTask = reviewManager.launchReviewFlow(activity, reviewInfo);

                reviewFlowTask.addOnCompleteListener(flowTask -> {
                    // Review-Flow wurde angezeigt (egal ob Nutzer bewertet hat oder nicht)
                    onReviewFlowCompleted(flowTask.isSuccessful());
                });

                Log.d(TAG, "In-App Review Flow gestartet");

            } else {
                // Fehler beim Anfordern der ReviewInfo
                Log.w(TAG, "Review-Request fehlgeschlagen", task.getException());
                onReviewFlowCompleted(false);
            }
        });

        // Zeitpunkt der letzten Aufforderung speichern
        prefs.edit()
                .putLong(KEY_LAST_REVIEW_REQUEST, System.currentTimeMillis())
                .apply();
    }

    /**
     * Wird aufgerufen nachdem der Review-Flow abgeschlossen wurde
     */
    private void onReviewFlowCompleted(boolean successful) {
        if (successful) {
            Log.d(TAG, "Review-Flow erfolgreich abgeschlossen");

            // Als abgeschlossen markieren (verhindert erneute Aufforderungen)
            prefs.edit()
                    .putBoolean(KEY_REVIEW_COMPLETED, true)
                    .apply();
        } else {
            Log.d(TAG, "Review-Flow wurde abgebrochen oder fehlgeschlagen");
        }
    }

    /**
     * Erhöht den Launch Counter
     */
    private void incrementLaunchCount() {
        long currentCount = prefs.getLong(KEY_LAUNCH_COUNT, 0);
        prefs.edit()
                .putLong(KEY_LAUNCH_COUNT, currentCount + 1)
                .apply();
    }

    /**
     * Speichert den Zeitpunkt des ersten App-Starts
     */
    private void ensureFirstLaunchTimeSet() {
        if (prefs.getLong(KEY_FIRST_LAUNCH, 0) == 0) {
            prefs.edit()
                    .putLong(KEY_FIRST_LAUNCH, System.currentTimeMillis())
                    .apply();
        }
    }

    // ========== UTILITY METHODS ==========

    /**
     * Setzt alle Tracking-Daten zurück (nur für Testing!)
     */
    public void resetForTesting() {
        prefs.edit()
                .remove(KEY_LAUNCH_COUNT)
                .remove(KEY_FIRST_LAUNCH)
                .remove(KEY_LAST_REVIEW_REQUEST)
                .remove(KEY_REVIEW_COMPLETED)
                .remove(KEY_NEVER_SHOW)
                .apply();
        Log.d(TAG, "Review-Tracking zurückgesetzt");
    }

    /**
     * Gibt Debug-Informationen aus
     */
    public void printDebugInfo() {
        long launchCount = prefs.getLong(KEY_LAUNCH_COUNT, 0);
        long firstLaunch = prefs.getLong(KEY_FIRST_LAUNCH, 0);
        long lastRequest = prefs.getLong(KEY_LAST_REVIEW_REQUEST, 0);
        boolean completed = prefs.getBoolean(KEY_REVIEW_COMPLETED, false);
        boolean neverShow = prefs.getBoolean(KEY_NEVER_SHOW, false);

        long daysSinceFirstLaunch = firstLaunch > 0 ?
                (System.currentTimeMillis() - firstLaunch) / (1000 * 60 * 60 * 24) : 0;
        long daysSinceLastRequest = lastRequest > 0 ?
                (System.currentTimeMillis() - lastRequest) / (1000 * 60 * 60 * 24) : 0;

        Log.d(TAG, "========== In-App Review Debug Info ==========");
        Log.d(TAG, "Launch Count: " + launchCount + " (Min: " + MIN_LAUNCHES_UNTIL_PROMPT + ")");
        Log.d(TAG, "Days since first launch: " + daysSinceFirstLaunch + " (Min: " + MIN_DAYS_UNTIL_PROMPT + ")");
        Log.d(TAG, "Days since last request: " + daysSinceLastRequest + " (Min: " + MIN_DAYS_BETWEEN_PROMPTS + ")");
        Log.d(TAG, "Review completed: " + completed);
        Log.d(TAG, "Never show again: " + neverShow);
        Log.d(TAG, "Should show prompt: " + shouldShowReviewPrompt());
        Log.d(TAG, "=============================================");
    }

    /**
     * Simuliert mehrere App-Starts (nur für Testing!)
     */
    public void simulateLaunches(int count) {
        for (int i = 0; i < count; i++) {
            incrementLaunchCount();
        }
        Log.d(TAG, "Simuliert " + count + " App-Starts");
        printDebugInfo();
    }

    /**
     * Simuliert vergangene Zeit (nur für Testing!)
     */
    public void simulateDaysAgo(int days) {
        long simulatedFirstLaunch = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);
        prefs.edit()
                .putLong(KEY_FIRST_LAUNCH, simulatedFirstLaunch)
                .apply();
        Log.d(TAG, "Erster Launch simuliert vor " + days + " Tagen");
        printDebugInfo();
    }
}
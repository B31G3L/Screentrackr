# Screentrackr

## Übersicht
Screentrackr ist eine Android-Anwendung zur Anzeige anpassbarer visueller Marker auf dem Bildschirm. Diese Marker sind besonders nützlich für:
- Videoaufnahmen und Screencasts
- Tracking-Referenzpunkte für Postproduktion
- Visuelle Guides für Präsentationen und Tutorials

## Features
- Anpassbare Marker-Farben
- Multiple Marker-Typen (Kreuz, Kreis, Dreieck, Tortenstück)
- Einstellbare Marker-Dichte und -Größe
- Optionale Eckmarker
- Benutzerdefinierbare Hintergrundfarbe
- Vollbildmodus für ungestörte Nutzung

## Screenshots
![Hauptbildschirm](screenshots/main_screen.png)
![Tracking-Bildschirm](screenshots/tracking_screen.png)

## Installation
### Anforderungen
- Android 8.0 (API-Level 26) oder höher
- Mindestens 10 MB freier Speicherplatz

### Google Play Store
Die App ist im [Google Play Store](https://play.google.com/store/apps/details?id=com.beigel.screenTracker) verfügbar.

### Manuelles Bauen
1. Klonen Sie das Repository:
   ```
   git clone https://github.com/b31g3l/screentrackrApp.git
   ```
2. Öffnen Sie das Projekt in Android Studio
3. Bauen Sie die APK mit `Build > Build Bundle(s) / APK(s) > Build APK(s)`
4. Die APK finden Sie unter `app/build/outputs/apk/debug/app-debug.apk`

## Verwendung
1. Starten Sie die App
2. Passen Sie die Marker-Einstellungen an:
   - Hintergrundfarbe
   - Marker-Farbe
   - Marker-Dichte (0-3)
   - Marker-Größe (1-5)
   - Marker-Typ (Kreuz, Kreis, Dreieck, Tortenstück)
   - Eckmarker (Keine, Ecken, Halbkreise)
3. Drücken Sie auf "Start", um in den Vollbildmodus zu wechseln
4. Zum Beenden den Bildschirm von unten nach oben wischen

## Konfiguration
Die App speichert Ihre Einstellungen nicht zwischen den Sitzungen. Jedes Mal, wenn Sie die App starten, werden die Standardeinstellungen geladen.

## Für Entwickler

### Projektstruktur
```
com.beigel.screenTracker/
├── MainActivity.java         # Haupteinstellungsbildschirm
├── Trackingscreen.java       # Vollbild-Tracking-Ansicht
├── TrackingValues.java       # Datenmodell für Tracking-Einstellungen
└── Utilities.java            # Hilfsfunktionen für UI-Erstellung
```

### Bauen und Testen
Das Projekt verwendet Gradle als Build-System:
```
./gradlew assembleDebug    # Debug-APK erstellen
./gradlew installDebug     # Debug-APK auf verbundenem Gerät installieren
./gradlew test             # Unit-Tests ausführen
```

### Build-Konfigurationen
- `debug`: Entwicklungsversion mit Debug-Symbolen
- `release`: Optimierte, signierte Version für Veröffentlichung

## Mitwirken
Beiträge zum Projekt sind willkommen! Bitte folgen Sie diesen Schritten:
1. Forken Sie das Repository
2. Erstellen Sie einen Feature-Branch (`git checkout -b feature/amazing-feature`)
3. Committen Sie Ihre Änderungen (`git commit -m 'Füge eine tolle Funktion hinzu'`)
4. Pushen Sie den Branch (`git push origin feature/amazing-feature`)
5. Öffnen Sie einen Pull Request

## Lizenz
Dieses Projekt ist unter der MIT-Lizenz lizenziert - siehe die [LICENSE](LICENSE) Datei für Details.

## Kontakt
Entwickler: [Beigel](https://b31g3l.github.io/)
In Zusammenarbeit mit: [Overmind Studios](https://www.overmind-studios.de/)

## Danksagungen
- [ColorPickerView](https://github.com/skydoves/ColorPickerView) für die Farbauswahl-Komponente
- [Android Material Components](https://github.com/material-components/material-components-android) für die UI-Elemente
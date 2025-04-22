# Screentrackr

Screentrackr ist eine Android-App, mit der man visuelle Marker auf dem Bildschirm platzieren kann. Dies ist besonders nützlich für Post-Produktion, Video-Tutorials und Screen-Recordings.

![Screentrackr Logo](app/src/main/res/drawable/inapplogo.png)

## Features

- Anpassbare Marker-Typen (Kreuz, Kreis, Dreieck, Tortenstück)
- Einstellbare Marker-Dichte
- Verschiedene Marker-Größen
- Benutzerdefinierte Marker- und Hintergrundfarben
- Eckmarker-Optionen
- Vollbildmodus für ungestörte Aufnahmen

## Voraussetzungen

- Android 8.0 (API Level 26) oder höher
- Android Studio 4.0 oder höher (für Entwicklung)

## Installation

### Aus dem Play Store
1. Öffnen Sie den Google Play Store
2. Suchen Sie nach "Screentrackr"
3. Installieren Sie die App

### Manuell für Entwickler
1. Klonen Sie dieses Repository:
   ```bash
   git clone https://github.com/b31g3l/screentrackrApp.git
   ```
2. Öffnen Sie das Projekt in Android Studio
3. Bauen und installieren Sie die App auf Ihrem Gerät

## Nutzung

1. Starten Sie die App
2. Konfigurieren Sie die Marker nach Ihren Wünschen:
   - Wählen Sie die Hintergrundfarbe
   - Wählen Sie die Marker-Farbe
   - Stellen Sie die Marker-Dichte ein (0-3)
   - Wählen Sie die Marker-Größe (1-5)
   - Wählen Sie den Marker-Typ (Kreuz, Kreis, Dreieck, Tortenstück)
   - Aktivieren Sie bei Bedarf Eckmarker
3. Drücken Sie auf "Start", um den Vollbildmodus zu aktivieren
4. Wischen Sie von unten nach oben, um zum Einstellungsbildschirm zurückzukehren

## Projektstruktur

```
com.beigel.screenTracker/
├── MainActivity.java       # Hauptbildschirm mit Einstellungen
├── Trackingscreen.java     # Vollbildmodus zur Anzeige der Marker
├── TrackingValues.java     # Datenmodell für die Einstellungen
└── Utilities.java          # Hilfsfunktionen zur Marker-Erstellung
```

## Beitragen

Wir freuen uns über Beiträge zur Verbesserung von Screentrackr! So können Sie helfen:

1. Forken Sie das Repository
2. Erstellen Sie einen Feature-Branch (`git checkout -b feature/neue-funktion`)
3. Committen Sie Ihre Änderungen (`git commit -am 'Neue Funktion hinzugefügt'`)
4. Pushen Sie den Branch (`git push origin feature/neue-funktion`)
5. Erstellen Sie einen Pull Request


## Verwendete Bibliotheken

- [ColorPickerView](https://github.com/skydoves/ColorPickerView) - Für die Farbauswahl
- [AndroidX](https://developer.android.com/jetpack/androidx) - Für moderne Android-Entwicklung

## Für Entwickler

### Build-Typen

- `debug` - Für Entwicklung und Tests
- `release` - Optimierte Version für die Veröffentlichung

### Ordnerstruktur

- `01_Code/` - Enthält den Android-Projektcode
- `02_KEYSTORE/` - Enthält Signing-Informationen (wird nicht im Repository gespeichert)

### Git-Einstellungen

Wir verwenden eine angepasste `.gitignore`-Datei, um sicherzustellen, dass keine generierten Dateien, Schlüssel oder IDE-spezifischen Konfigurationen im Repository landen.

## Lizenz

Dieses Projekt ist unter der MIT-Lizenz lizenziert. Weitere Details finden Sie in der [LICENSE](LICENSE)-Datei.

## Kontakt

Entwickelt von [Beigel](https://b31g3l.github.io/) in Zusammenarbeit mit [Overmind Studios](https://www.overmind-studios.de/)
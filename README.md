# DSGVO für WebUntis

Ein JavaFX-Programm zur Konvertierung von ASV-BW CSV-Exporten in ein datenschutzkonformes Profilbild-Verzeichnis für WebUntis.

## Beschreibung

Dieses Tool hilft Bildungseinrichtungen dabei, die Anforderungen der DSGVO zu erfüllen, indem es ASV-BW-Exportdateien so verarbeitet, dass nur die für WebUntis relevanten und datenschutzkonformen Informationen für Profilbilder extrahiert und in einem geeigneten Ordnerformat organisiert werden. Es bietet eine einfache Benutzeroberfläche zur Auswahl der CSV-Datei und zur Initiierung des Konvertierungsprozesses.

## Benötigt

*   **Java 11 oder höher**
    Das Programm wurde mit Java 11 entwickelt. Stellen Sie sicher, dass eine kompatible Java-Laufzeitumgebung (JRE) oder ein Java Development Kit (JDK) auf Ihrem System installiert ist.

## Installation und Ausführung

### Von Quellcode bauen

1.  **Voraussetzungen**: Stellen Sie sicher, dass [Apache Maven](https://maven.apache.org/download.cgi) installiert ist.
2.  **Klonen Sie das Repository**:
    ```bash
    git clone https://github.com/tinsever/dsgvo4wu.git
    cd dsgvo4wu
    ```
    (Ersetzen Sie `IhrBenutzername` durch den tatsächlichen Benutzernamen/Organisation, sobald das Repository existiert.)
3.  **Bauen Sie das Projekt**:
    ```bash
    mvn clean install
    ```
    Dieser Befehl kompiliert das Programm und erstellt eine ausführbare JAR-Datei im `target/` Verzeichnis. Die JAR-Datei wird voraussichtlich `dsgvo4wu-1.0.0.jar` oder ähnlich benannt sein.

### Ausführen

Nach dem Bauen (oder wenn Sie eine vorkompilierte JAR-Datei haben), können Sie das Programm wie folgt ausführen:

```bash
java -jar target/dsgvo4wu-1.0.0.jar
```
*Hinweis: Der genaue Dateiname kann je nach Build-Prozess variieren. Prüfen Sie den `target/` Ordner auf die generierte JAR-Datei.*

## Verwendung

1.  **ASV-BW Export**: Exportieren Sie die relevanten Schülerdaten aus ASV-BW im CSV-Format. Stellen Sie sicher, dass die Exportdatei die für die Profilbilder benötigten Felder enthält (z.B. Nachname, Vorname, Klasse, ggf. ID).
2.  **Programm starten**: Führen Sie das `dsgvo4wu.jar` Programm aus.
3.  **CSV-Datei auswählen**: In der Benutzeroberfläche des Programms wählen Sie die zuvor exportierte ASV-BW CSV-Datei aus.
4.  **Zielordner festlegen**: Wählen Sie den Ordner, in dem die konvertierten Profilbilder gespeichert werden sollen.
5.  **Konvertierung starten**: Klicken Sie auf den Konvertierungs-Button.
## Entwicklung

Dieses Projekt verwendet:
*   **JavaFX**: Für die grafische Benutzeroberfläche.
*   **Apache Commons CSV**: Zum einfachen Parsen von CSV-Dateien.
*   **Maven**: Als Build-Automatisierungstool.

package com.dsgvo4wu;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Paths;

public class DSGVOApp {

    private Stage primaryStage;
    private TextField csvFilePathField;
    private TextField outputFolderNameField;

    public DSGVOApp(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void start() {
        primaryStage.setTitle("DSGVO Profilbilder Generator");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Detaillierte Beschreibung für die CSV-Eingabe
        Label csvDescriptionLabel = new Label("Eingabe durch csv-Datei mit Semikolon getrennten Spalten:");
        GridPane.setConstraints(csvDescriptionLabel, 0, 0, 3, 1);

        Label csvHeaderLabel = new Label("Benutzername;DSGVOextern;DSGVOintern;ID");
        csvHeaderLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10pt;");
        GridPane.setConstraints(csvHeaderLabel, 0, 1, 3, 1);

        // CSV-Datei Auswahl
        Label csvFileLabel = new Label("Pfad zur CSV-Datei:");
        GridPane.setConstraints(csvFileLabel, 0, 2);

        csvFilePathField = new TextField();
        csvFilePathField.setPromptText("Wählen Sie eine CSV-Datei aus");
        GridPane.setConstraints(csvFilePathField, 1, 2);

        Button browseButton = new Button("Durchsuchen");
        browseButton.setOnAction(e -> browseCsvFile());
        GridPane.setConstraints(browseButton, 2, 2);

        // Ausgabeordner Information
        Label outputInfoLabel = new Label("Ausgabe geschieht im Verzeichnis der CSV-Datei als Ordner");
        GridPane.setConstraints(outputInfoLabel, 0, 3, 3, 1);

        // Ordnername Eingabe
        Label folderNameLabel = new Label("Ordnername:");
        GridPane.setConstraints(folderNameLabel, 0, 4);

        outputFolderNameField = new TextField("DSGVO4WebUntis_$[timestamp]");
        GridPane.setConstraints(outputFolderNameField, 1, 4, 2, 1);

        // Erklärung für den Platzhalter
        Label placeholderExplanationLabel = new Label("Verwenden Sie $[timestamp] als Platzhalter für das aktuelle Datum und die Uhrzeit.");
        GridPane.setConstraints(placeholderExplanationLabel, 0, 5, 3, 1);

        // Start-Button
        Button startButton = new Button("Starten");
        startButton.setOnAction(e -> processCsv());
        GridPane.setConstraints(startButton, 1, 6);

        grid.getChildren().addAll(csvDescriptionLabel, csvHeaderLabel, csvFileLabel, csvFilePathField, browseButton,
                outputInfoLabel, folderNameLabel, outputFolderNameField, placeholderExplanationLabel, startButton);

        Scene scene = new Scene(grid, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Prüfe die Bilder direkt beim Start der GUI
        Main.checkImagesExist();
    }

    private void browseCsvFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            csvFilePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void processCsv() {
        String csvFile = csvFilePathField.getText();
        String outputDirTemplate = outputFolderNameField.getText();

        if (csvFile.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Fehler", "Bitte wählen Sie eine CSV-Datei aus.");
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String outputDirName = outputDirTemplate.replace("$[timestamp]", timestamp);

        File csv = new File(csvFile);
        String csvDir = csv.getParent();
        String outputDir = Paths.get(csvDir, outputDirName).toString();

        try {
            DSGVOProcessor.processCsv(csvFile, outputDir);
            showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Verarbeitung abgeschlossen. Ergebnisse im Ordner: " + outputDir);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Fehler", "Ein Fehler ist aufgetreten: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}

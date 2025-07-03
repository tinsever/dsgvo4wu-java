package com.dsgvo4wu;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class DSGVOProcessor {

    public static final String GRUENES_BILD = Main.getImagePath("gruen.png");
    public static final String GELBES_BILD = Main.getImagePath("gelb.png");
    public static final String ROTES_BILD = Main.getImagePath("rot.png");
    public static final String GRAUES_BILD = Main.getImagePath("grau.png");

    public static void processCsv(String csvFile, String outputDir) {
        String zeitstempel = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-ddHHmmss"));

        try {
            Files.createDirectories(Paths.get(outputDir));
        } catch (IOException e) {
            System.err.println("Fehler beim Erstellen des Ausgabeordners: " + e.getMessage());
            return;
        }

        String fehlerFile = "000_Fehler_" + zeitstempel + ".csv";
        Path fehlerPath = Paths.get(outputDir, fehlerFile);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setHeader("Benutzername", "DSGVO_extern", "DSGVO_intern", "ID")
                .setSkipHeaderRecord(true)
                .build();

        try (Reader in = new FileReader(csvFile);
             CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT.builder().setDelimiter(';').setHeader().build());
             CSVPrinter fehlerCsvPrinter = new CSVPrinter(new FileWriter(fehlerPath.toFile()), csvFormat)) {

            fehlerCsvPrinter.printRecord("Benutzername", "DSGVO_extern", "DSGVO_intern", "ID");

            for (CSVRecord row : parser) {
                String extern = row.get("DSGVO_extern") != null ? row.get("DSGVO_extern").trim().toLowerCase() : "";
                String intern = row.get("DSGVO_intern") != null ? row.get("DSGVO_intern").trim().toLowerCase() : "";
                String idNutzer = row.get("ID");
                String benutzername = row.get("Benutzername");

                String bild;
                if (extern.equals("ja") && intern.equals("ja")) {
                    bild = GRUENES_BILD;
                } else if (extern.equals("nein") && intern.equals("ja")) {
                    bild = GELBES_BILD;
                } else if (extern.equals("nein") && intern.equals("nein")) {
                    bild = ROTES_BILD;
                } else {
                    bild = GRAUES_BILD;
                    fehlerCsvPrinter.printRecord(benutzername, row.get("DSGVO_extern"), row.get("DSGVO_intern"), idNutzer);
                }

                if (idNutzer != null && !idNutzer.trim().isEmpty()) {
                    Path zielPfad = Paths.get(outputDir, idNutzer.trim() + ".png");
                    if (bild != null) {
                        Path sourcePath = Paths.get(bild);
                        if (Files.exists(sourcePath)) {
                            Files.copy(sourcePath, zielPfad, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
            System.out.println("Verarbeitung abgeschlossen. Ergebnisse im Ordner: " + outputDir);

        } catch (IOException e) {
            System.err.println("Fehler bei der CSV-Verarbeitung: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runCli() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Geben Sie den Pfad zur CSV-Datei ein: ");
        String csvFile = scanner.nextLine();
        Path csvPath = Paths.get(csvFile);
        String csvDir = csvPath.getParent().toString();
        String outputDir = Paths.get(csvDir, "DSGVO4WebUntis_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-ddHHmmss"))).toString();

        Main.checkImagesExist();

        processCsv(csvFile, outputDir);
        scanner.close();
    }
}
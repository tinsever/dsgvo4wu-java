package com.dsgvo4wu;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--cli")) {
            DSGVOProcessor.runCli();
        } else {
            launch(args);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        DSGVOApp app = new DSGVOApp(primaryStage);
        app.start();
    }

    public static String getImagePath(String imageName) {
        try {
            java.net.URL resourceUrl = Main.class.getResource("/bilder/" + imageName);
            if (resourceUrl == null) {
                // Fallback for IDE execution
                File file = new File("src/main/resources/bilder/" + imageName);
                if (file.exists()) {
                    return file.getAbsolutePath();
                }
                return null;
            }

            if ("jar".equals(resourceUrl.getProtocol())) {
                // Extract from JAR to a temp file
                try (InputStream in = Main.class.getResourceAsStream("/bilder/" + imageName)) {
                    if (in == null) return null;
                    File tempFile = File.createTempFile("dsgvo_", "_" + imageName);
                    tempFile.deleteOnExit();
                    Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    return tempFile.getAbsolutePath();
                }
            } else {
                // It's a file on the filesystem (e.g., running from IDE)
                return Paths.get(resourceUrl.toURI()).toFile().getAbsolutePath();
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void checkImagesExist() {
        List<String> imageNames = Arrays.asList(
                "gruen.png",
                "gelb.png",
                "rot.png",
                "grau.png"
        );

        StringBuilder missingFiles = new StringBuilder();
        for (String imageName : imageNames) {
            // We check the resource stream directly, which is more reliable.
            try (InputStream is = Main.class.getResourceAsStream("/bilder/" + imageName)) {
                if (is == null) {
                    missingFiles.append(imageName).append("\n");
                }
            } catch (IOException e) {
                missingFiles.append(imageName).append(" (error reading)\n");
            }
        }

        if (missingFiles.length() > 0) {
            String message = "Fehler: Die folgenden Bilddateien fehlen im JAR:\n" + missingFiles.toString();
            System.err.println(message);
            // In a real JavaFX app, you'd show an Alert here.
            System.exit(1);
        }
    }
}
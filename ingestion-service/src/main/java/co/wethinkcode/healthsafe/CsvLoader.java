package co.wethinkcode.healthsafe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class CsvLoader {
    private final String fileName;

    public CsvLoader(String fileName) {
        this.fileName = fileName;
    }

    public List<String> loadLines() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: " + fileName);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            // Converts all lines into a List of Strings
            return reader.lines().toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file: " + fileName, e);
        }

    }
}
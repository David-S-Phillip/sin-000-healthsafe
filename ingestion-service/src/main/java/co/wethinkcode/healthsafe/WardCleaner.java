package co.wethinkcode.healthsafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WardCleaner {

    private static final Set<String> PLACEHOLDERS = Set.of(
            "N/A", "TBD", "UNKNOWN", "-", "NAN", "NULL", "NONE"
    );

    public List<WardRecord> cleanAll(List<String> rawLines) {
        List<WardRecord> cleanedRecords = new ArrayList<>();
        if (rawLines == null || rawLines.isEmpty()) {
            return cleanedRecords;
        }

        for (String line : rawLines) {
            if (line == null || line.isBlank()) continue;

            // Skip CSV header row if present
            if (line.toLowerCase().startsWith("ward_id") || line.toLowerCase().startsWith("wardid")) {
                continue;
            }

            cleanedRecords.add(cleanSingleLine(line));
        }

        return cleanedRecords;
    }

    public WardRecord cleanSingleLine(String line) {
        String[] fields = line.split(",", -1);

        String wardId = cleanText(fields, 0);
        String wing = cleanText(fields, 1);
        String department = cleanText(fields, 2);
        String rawBeds = cleanText(fields, 3);

        // Normalize text formats
        wardId = (wardId != null) ? wardId.toUpperCase() : null;
        wing = (wing != null) ? capitalizeWords(wing) : null;
        department = (department != null) ? capitalizeWords(department) : null;

        Integer bedsAvailable = null;
        String notes = null;

        if (rawBeds != null) {
            try {
                bedsAvailable = Integer.parseInt(rawBeds);
            } catch (NumberFormatException e) {
                bedsAvailable = null;
                notes = "bedsAvailable was non-numeric ('" + rawBeds + "') — flagged for follow-up";
            }
        }

        return new WardRecord(wardId, wing, department, bedsAvailable, notes);
    }

    private String cleanText(String[] fields, int index) {
        if (index >= fields.length) return null;

        String val = fields[index].trim();
        if (val.isEmpty() || PLACEHOLDERS.contains(val.toUpperCase())) {
            return null;
        }

        // Collapse internal multiple spaces into single space
        return val.replaceAll("\\s+", " ");
    }

    private String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) return input;

        String[] words = input.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }
}
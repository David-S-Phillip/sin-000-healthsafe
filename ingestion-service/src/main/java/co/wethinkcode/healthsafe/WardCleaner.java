package co.wethinkcode.healthsafe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WardCleaner {

    private static final Set<String> PLACEHOLDERS = Set.of(
            "N/A", "TBD", "UNKNOWN", "-", "NAN", "NULL", "NONE"
    );

    public List<WardRecord> cleanAll(List<String> rawLines) {
        if (rawLines == null || rawLines.isEmpty()) {
            return List.of();
        }

        // Map maintains insertion order while deduplicating by wardId
        Map<String, WardRecord> recordMap = new LinkedHashMap<>();

        for (String line : rawLines) {
            if (line == null || line.isBlank()) continue;

            if (line.toLowerCase().startsWith("ward_id") || line.toLowerCase().startsWith("wardid")) {
                continue;
            }

            WardRecord incoming = cleanSingleLine(line);
            if (incoming.wardId() == null) continue;

            if (!recordMap.containsKey(incoming.wardId())) {
                recordMap.put(incoming.wardId(), incoming);
            } else {
                // Duplicate found: merge missing fields & add duplicate note
                WardRecord existing = recordMap.get(incoming.wardId());
                recordMap.put(incoming.wardId(), mergeRecords(existing, incoming));
            }
        }

        return new ArrayList<>(recordMap.values());
    }

    public WardRecord cleanSingleLine(String line) {
        String[] fields = line.split(",", -1);

        String wardId = cleanText(fields, 0);
        String wing = cleanText(fields, 1);
        String department = cleanText(fields, 2);
        String rawBeds = cleanText(fields, 3);

        wardId = (wardId != null) ? wardId.toUpperCase() : null;
        wing = (wing != null) ? capitalizeWords(wing) : null;
        department = (department != null) ? capitalizeWords(department) : null;

        Integer bedsAvailable = null;
        String notes = null;

        if (rawBeds != null) {
            try {
                int parsed = Integer.parseInt(rawBeds);
                if (parsed < 0) {
                    notes = "bedsAvailable was negative ('" + rawBeds + "') — flagged for follow-up";
                } else {
                    bedsAvailable = parsed;
                }
            } catch (NumberFormatException e) {
                notes = "bedsAvailable was non-numeric ('" + rawBeds + "') — flagged for follow-up";
            }
        }

        return new WardRecord(wardId, wing, department, bedsAvailable, notes);
    }

    private WardRecord mergeRecords(WardRecord existing, WardRecord incoming) {
        String mergedWing = (existing.wing() != null) ? existing.wing() : incoming.wing();
        String mergedDept = (existing.getDepartment() != null) ? existing.getDepartment() : incoming.getDepartment();
        Integer mergedBeds = (existing.bedsAvailable() != null) ? existing.bedsAvailable() : incoming.bedsAvailable();

        String mergedNotes = existing.notes();
        if (mergedNotes == null) {
            mergedNotes = "Duplicate record detected; merged available fields.";
        } else {
            mergedNotes += " | Duplicate record detected; merged available fields.";
        }

        return new WardRecord(existing.wardId(), mergedWing, mergedDept, mergedBeds, mergedNotes);
    }

    private String cleanText(String[] fields, int index) {
        if (index >= fields.length) return null;

        String val = fields[index].trim();
        if (val.isEmpty() || PLACEHOLDERS.contains(val.toUpperCase())) {
            return null;
        }

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
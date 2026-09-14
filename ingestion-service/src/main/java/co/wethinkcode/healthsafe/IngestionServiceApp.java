package co.wethinkcode.healthsafe;

import io.javalin.Javalin;

import java.util.List;

public class IngestionServiceApp {

    public static void main(String[] args) {

        CsvLoader loader = new CsvLoader("wards-outdated.csv");
        WardCleaner cleaner = new WardCleaner();

        // Load and clean records at startup
        List<String> rawLines = loader.loadLines();
        List<WardRecord> cleanedWards = cleaner.cleanAll(rawLines);

        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.

        // Expose cleaned wards as JSON for ward-service
        app.get("/wards", ctx -> ctx.json(cleanedWards));
    }
}

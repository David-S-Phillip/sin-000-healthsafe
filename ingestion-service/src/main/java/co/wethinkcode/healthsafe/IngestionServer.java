package co.wethinkcode.healthsafe;
import io.javalin.Javalin;

import java.util.Collections;
import java.util.List;

public class IngestionServer {

    private final Javalin app;
    private final List<WardRecord> cleanedWards;

    public IngestionServer(){
        CsvLoader loader = new CsvLoader("wards-outdated.csv");
        WardCleaner cleaner = new WardCleaner();
        this.cleanedWards = Collections.unmodifiableList(cleaner.cleanAll(loader.loadLines()));

        this.app = Javalin.create();
        this.app.get("/health", ctx -> ctx.result("OK"));
        this.app.get("/wards", ctx -> ctx.json(cleanedWards));
    }

    public void start(int port){
        this.app.start(port);
    }

    public void stop(){
        this.app.stop();
    }
}

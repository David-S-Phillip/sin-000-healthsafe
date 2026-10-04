package co.wethinkcode.healthsafe.client;
import co.wethinkcode.healthsafe.WardRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ClientRequest {

    private final String targetUrl;
    private final HttpClient client;
    private final ObjectMapper mapper;

    // the constructor takes a endpoint and port number connection
    public ClientRequest(String targetUrl) {
        this.targetUrl = targetUrl;
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    // The method sends the request to ingestion-service and returns a list of WardRecord instances bruv
    public List<WardRecord> fetchWards() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Deserialize and return in one step
            // new thing I learned, Type erasure, it occurs at the end of the compilation stage, the reason we use an array here is bc arrays data types are permanently attached to it and bc Jackson is doing its work while the program is running in the jvm.
            WardRecord[] wardArray = mapper.readValue(response.body(), WardRecord[].class);
            return List.of(wardArray);

        } catch (Exception e) {
            System.out.println("Failed to fetch data. Is ingestion-service running on port 7030?");
            System.out.println("Error details: " + e.getMessage());
            e.printStackTrace(); // THIS will tell us exactly which line crashed

            return new ArrayList<>();
        }
    }

}
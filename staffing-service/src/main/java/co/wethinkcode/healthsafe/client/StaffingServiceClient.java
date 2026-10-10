package co.wethinkcode.healthsafe.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StaffingServiceClient {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public StaffingServiceClient(){
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public boolean checkWardExists(String wardId) {
        try {
            HttpResponse<String> response = httpResponse("http://localhost:7031/wards/" + wardId);

            return response.statusCode() == 200;
        } catch(Exception e){
            return false;
        }
    }

    public int alertLevel(){
        try {
            HttpResponse<String> response = httpResponse("http://localhost:7032/alert-level");

            if (response.statusCode() == 200){
                JsonNode jsonNode = mapper.readTree(response.body());

                return jsonNode.get("level").asInt();
            } else {
                return -1;
            }
        } catch(Exception e) {
                return -1;
            }
        }



    // this is a helper method, it throws an exception, so any method that implements it must use a try and catch block brudda
    private HttpResponse<String> httpResponse(String apiEndpoint) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response;
    }


}

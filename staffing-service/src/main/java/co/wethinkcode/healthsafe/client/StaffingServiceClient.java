package co.wethinkcode.healthsafe.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StaffingServiceClient {

    private final HttpClient httpClient;

    public StaffingServiceClient(){
        this.httpClient = HttpClient.newHttpClient();
    }

    public boolean checkWardExists(String wardId) {
        try {
            HttpResponse<String> response = httpResponse("http://localhost:7032/alert-level");

            if (response.statusCode() == 200){
                return true;
            }
        } catch(Exception e){
            return false;
        }
        return false;
    }

    public String alertLevel(){
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:7032/alert-level"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200){
                return response.body();
            }else{
                return "error server returned " + response.statusCode();
            }
        }catch (Exception e){
            return "error connecting to aler-level-service";
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

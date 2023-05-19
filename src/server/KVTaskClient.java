package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private HttpClient httpClient;
    private String KVServerAddress = "http://localhost:8078";
    private String apiToken;
    public KVTaskClient() throws IOException, InterruptedException {
        httpClient = HttpClient.newHttpClient();
        register();
    }

    public String getClientApi(){
        return this.apiToken;
    }
    public void setApi(String api){
        this.apiToken = api;
    }

    public void register() throws IOException, InterruptedException {
        URI uri = URI.create(KVServerAddress + "/register");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, handler);

        if (response.statusCode() == 200){
            apiToken = response.body().toString();
        } else {
            System.out.println("registration filed, try again");
        }
    }

    public void put(String taskClass, String taskDetails) throws IOException, InterruptedException {
        System.out.println("11" + taskDetails);
        URI uri = URI.create(KVServerAddress + "/save/" + taskClass + "?API_TOKEN=" + apiToken);

        httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskDetails))
                .uri(uri)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201 || response.statusCode() == 202){
            System.out.println(response.statusCode() + ", task added");
        } else {
            System.out.println(response.statusCode() + ", task not added");
        }
    }

    public String load(String taskClass) throws IOException, InterruptedException {
        URI uri = URI.create(KVServerAddress + "/load/" + taskClass + "?API_TOKEN=" + apiToken);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();

        httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200){
            return response.body().toString();
        }
        return "null";
    }
}

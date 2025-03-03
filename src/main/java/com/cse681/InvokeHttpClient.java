package com.cse681;

import java.lang.String;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class InvokeHttpClient {
    // instantiate a http client attribute - HttpClient is thread safe
    // this ensures all objects created from InvokeHttpClient will reuse the same Http Client
    private static final HttpClient client = HttpClient.newHttpClient();

    // issue a GET request against a supplied URL
    public static String sendRequest(String url){
        try{
            // create a request string
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Accept", "application/json")
                    .GET().build();
            // issue a response against string
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // validate response's status code
            if (response.statusCode() == 200){
                return response.body();
            } else {
                throw new RuntimeException("HTTP GET Request failed with status code: " + response.statusCode());
            }
        } catch (Exception e){
            throw new RuntimeException("Error during GET request: " + e.getMessage());
        }
    }
}

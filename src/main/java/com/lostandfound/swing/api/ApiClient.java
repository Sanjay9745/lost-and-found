package com.lostandfound.swing.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    private static String authToken = null;

    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static ApiResponse post(String endpoint, Object requestBody) {
        return sendRequest("POST", endpoint, requestBody);
    }

    public static ApiResponse get(String endpoint) {
        return sendRequest("GET", endpoint, null);
    }

    public static ApiResponse put(String endpoint, Object requestBody) {
        return sendRequest("PUT", endpoint, requestBody);
    }

    public static ApiResponse delete(String endpoint) {
        return sendRequest("DELETE", endpoint, null);
    }

    private static ApiResponse sendRequest(String method, String endpoint, Object requestBody) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json");
            
            if (authToken != null && !endpoint.startsWith("/auth/login")) {
                conn.setRequestProperty("Authorization", "Bearer " + authToken);
            }

            if (requestBody != null) {
                conn.setDoOutput(true);
                String jsonInput = objectMapper.writeValueAsString(requestBody);
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            int responseCode = conn.getResponseCode();
            
            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            br.close();

            return new ApiResponse(responseCode, response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(500, "{\"message\":\"Connection error: " + e.getMessage() + "\"}");
        }
    }

    public static class ApiResponse {
        private final int statusCode;
        private final String body;

        public ApiResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }

        public <T> T parseBody(Class<T> clazz) throws Exception {
            return objectMapper.readValue(body, clazz);
        }
    }
}

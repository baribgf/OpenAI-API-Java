package com.bari.openaiapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class OpenaiAPI {
    private HttpURLConnection conn;
    private String ApiKey;
    private String model = "text-davinci-003";
    private int maxTokens = 1024;
    private double temperature = 0.5;
    private URL modelUrl = new URL("https://api.openai.com/v1/completions");

    public OpenaiAPI(String apiKey) throws IOException {
        this.ApiKey = apiKey;
        conn = (HttpURLConnection) modelUrl.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.addRequestProperty("Content-Type", "application/json");
        conn.addRequestProperty("Authorization", "Bearer " + ApiKey);
    }

    /**
     * Creates a request to OpenAI API
     */
    public OpenaiAPI create(String prompt) throws IOException {
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("prompt", prompt);
        dataMap.put("model", this.model);
        dataMap.put("max_tokens", this.maxTokens);
        dataMap.put("temperature", this.temperature);
        writeToOutputStream(dataMap);
        return this;
    }

    public HashMap<String, Object> getResponse() throws IOException {
        if (this.conn.getResponseCode() != 200)
            throw new RuntimeException("Request must be created first");

        return parseJSON(readInputStream(conn.getInputStream()));
    }

    @SuppressWarnings("unchecked")
    public String getResponseText() throws IOException {
        ArrayList<Object> choices = (ArrayList<Object>) getResponse().get("choices");
        HashMap<String, Object> firstChoice = (HashMap<String, Object>) choices.get(0);
        String text = firstChoice.get("text").toString();
        return text.strip();
    }

    private String readInputStream(InputStream inStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response.toString();
    }

    private void writeToOutputStream(HashMap<String, Object> dataMap) throws IOException {
        OutputStream os = conn.getOutputStream();
        os.write(toJSON(dataMap).getBytes());
        os.flush();
        os.close();
    }

    private String toJSON(HashMap<String, Object> dataMap) {
        JSONObject jsonData = new JSONObject();
        for (Map.Entry<String, Object> param : dataMap.entrySet()) {
            jsonData.put(param.getKey(), param.getValue());
        }
        return jsonData.toString();
    }

    private HashMap<String, Object> parseJSON(String json) {
        return (HashMap<String, Object>) new JSONObject(json).toMap();
    }

    public void setAPIKey(String apiKey) {
        this.ApiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}

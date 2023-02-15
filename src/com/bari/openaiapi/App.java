package com.bari.openaiapi;

public class App {
    public static void main(String[] args) throws Exception {
        String apiKey = "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";
        OpenaiAPI api = new OpenaiAPI(apiKey);
        System.out.println(api.create("create a hello world program in java").getResponseText());
    }
}

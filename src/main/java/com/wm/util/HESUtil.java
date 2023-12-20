package com.wm.util;

import java.util.HashMap;
import java.util.Map;

public class HESUtil {

	public static Map<String, String> createHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        
        return headers;
    }

    public static Map<String, Object> createErrorResponse(int statusCode, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("headers", createHeaders());
        response.put("body", "{\"message\":\"" + message + "\"}");
        return response;
    }
}

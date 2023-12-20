
package com.wm.lamdaFunctions;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wm.beans.HESUser;
import com.wm.service.HESActionService;
import com.wm.serviceImpl.HESActionServiceImpl;
import com.wm.service.HESUserService;
import com.wm.serviceImpl.HESUserServiceImpl;
import com.wm.util.HESUtil;

public class LambdaRequestHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	private static final Logger logger = Logger.getLogger(LambdaRequestHandler.class.getName());
	 private static final String OKTA_URL_ENV_VAR = "OKTA_URL";
	 private static final String OKTA_KEY_ENV_VAR = "OKTA_KEY";
	 
	 String OKTAURL = System.getenv(OKTA_URL_ENV_VAR);
     String KEY = System.getenv(OKTA_KEY_ENV_VAR);

	HESActionService hESActionService = new HESActionServiceImpl();
	HESUserService hESUserService = new HESUserServiceImpl();

	public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
	    
	    String resource = (String) input.get("resource");
	    
	    String httpMethod = (String) input.get("httpMethod");
	    logger.info("Received " + httpMethod + " request to End Point: " + resource);

    	String bodyString = (String) input.get("body");
	    Map<String, Object> bodyMap = null;
	    if(bodyString != null) {
		try {
			bodyMap = new ObjectMapper().readValue(bodyString, new TypeReference<Map<String, Object>>() {});
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    }
	    
	    if (resource.contains("actions")) {
	    	if ("GET".equalsIgnoreCase(httpMethod)) {
	            Map<String, Object> actionListJson = hESActionService.getActionList();
	            logger.info("Received response: " + actionListJson);
	            return actionListJson;
	        } else if ("POST".equalsIgnoreCase(httpMethod)) {
	            
	            int actionId = Integer.parseInt(String.valueOf(bodyMap.get("actionid")));
	            Map<String, Object> response = null;

	            switch (actionId) {
	                case 1000:
	                    response = hESActionService.postDisableAllCheckinRules();
	                    break;
	                case 1001:
	                    response = hESActionService.postEnableAllChekinRules();
	                    break;
	                default:
	                    throw new IllegalArgumentException("Invalid action ID: " + actionId);
	            }

	            logger.info("Received response: " + response);
	            return response;
	        }
	    }
	    
	    if (resource.contains("users")) {
	        String oktaUserID = null;

	        ObjectMapper objectMapper = new ObjectMapper();
	        if (bodyMap != null) {
	            oktaUserID = String.valueOf(bodyMap.get("oktaUserId"));
	        }
	        Map<String, String> config = new HashMap<>();
	        config.put("oktaDomain", OKTAURL);
	        config.put("oktaToken", KEY);
	        config.put("oktaUserID", oktaUserID);

	        Object responseData;
	        String responseKey;

	        if (oktaUserID == null || oktaUserID.isEmpty()) {
	            // Get user list
	            List<HESUser> users = hESUserService.getUserList(config);
	            responseData = users;
	            responseKey = "users";
	        } else {
	            // Get a single user
	            HESUser user = hESUserService.getUser(config);
	            responseData = user;
	            responseKey = "user";
	        }

	        logger.info("Received response: " + responseData);

	        Map<String, Object> result = new HashMap<>();
	        result.put("headers", HESUtil.createHeaders());
	        result.put("statusCode", 200);

	        try {
	            String jsonString = objectMapper.writeValueAsString(responseData);
	            logger.info("ReceivedRessponse: " + jsonString);
	            result.put("body", jsonString);
	        } catch (JsonProcessingException e) {
	            // Handle the exception (e.g., log it or return an error message)
	            result.put(responseKey, "Error converting to JSON: " + e.getMessage());
	        }

	        return result;
	    }
	    

	    return null;
	}
}

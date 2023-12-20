package com.wm.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.api.PolicyApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.model.LifecycleStatus;
import com.okta.sdk.resource.model.Policy;
import com.okta.sdk.resource.model.PolicyRule;
import com.okta.sdk.resource.model.PolicyType;
import com.wm.beans.HESAction;
import com.wm.service.HESActionService;
import com.wm.util.Creds;
import com.wm.util.HESUtil;

import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class HESActionServiceImpl implements HESActionService{
	private static final String OKTA_URL_ENV_VAR = "OKTA_URL";
	 private static final String OKTA_KEY_ENV_VAR = "OKTA_KEY";
	 
	 String OKTAURL = System.getenv(OKTA_URL_ENV_VAR);
    String KEY = System.getenv(OKTA_KEY_ENV_VAR);
	private static final Logger logger = Logger.getLogger(HESActionServiceImpl.class.getName());
	//Logger logger = LoggerFactory.getLogger(HESActionServiceImpl.class);

	@Override
	public Map<String, Object> postDisableAllCheckinRules() {
		try {
			ApiClient client = Clients.builder()
					.setOrgUrl(OKTAURL) 
					.setClientCredentials(new TokenClientCredentials(KEY))
					.build();

			PolicyApi policyApi = new PolicyApi(client);

			List<Policy> policies = policyApi.listPolicies(
					PolicyType.ACCESS_POLICY.name(), 
					LifecycleStatus.ACTIVE.name(),
					"true");

			for (Policy policy : policies) {
				List<PolicyRule>rules = policyApi.listPolicyRules(policy.getId());
				for (PolicyRule rule :rules) {
					if(rule.getName().startsWith("HES_Clocked")){
						logger.info(policy.getName()+ " " + rule.getName() + " " + rule.getStatus());
						policyApi.deactivatePolicyRule(policy.getId(),rule.getId());
						PolicyRule updatedRule = policyApi.getPolicyRule(policy.getId(), rule.getId());
			            logger.info(policy.getName() + " " + updatedRule.getName() + " " + updatedRule.getStatus());
						//logger.info(policy.getName()+ " " + rule.getName() + " " + rule.getStatus());
					}
				}
			}
			Map<String, Object> oktaResponse = new HashMap<>();
			oktaResponse.put("statusCode", 200);
			oktaResponse.put("headers", HESUtil.createHeaders());

			return oktaResponse;
		} catch (Exception e) {
			// Handle exceptions, e.g., network issues, request errors
			return HESUtil.createErrorResponse(500, e.getMessage());
		}
	}

	@Override
	public Map<String, Object> postEnableAllChekinRules() {
		try {
			ApiClient client = Clients.builder()
					.setOrgUrl(OKTAURL) 
					.setClientCredentials(new TokenClientCredentials(KEY))
					.build();

			PolicyApi policyApi = new PolicyApi(client);

			List<Policy> policies = policyApi.listPolicies(
					PolicyType.ACCESS_POLICY.name(), 
					LifecycleStatus.ACTIVE.name(),
					"true");

			for (Policy policy : policies) {
				List<PolicyRule>rules = policyApi.listPolicyRules(policy.getId());
				for (PolicyRule rule :rules) {
					if(rule.getName().startsWith("HES_Clocked")){
						logger.info(policy.getName()+ " " + rule.getName() + " " + rule.getStatus());
						policyApi.activatePolicyRule(policy.getId(),rule.getId());
						PolicyRule updatedRule = policyApi.getPolicyRule(policy.getId(), rule.getId());
			            logger.info(policy.getName() + " " + updatedRule.getName() + " " + updatedRule.getStatus());
						//logger.info(policy.getName()+ " " + rule.getName() + " " + rule.getStatus());
					}
				}
			}
			Map<String, Object> oktaResponse = new HashMap<>();
			oktaResponse.put("statusCode", 200);
			oktaResponse.put("headers", HESUtil.createHeaders());

			return oktaResponse;
		} catch (Exception e) {
			// Handle exceptions, e.g., network issues, request errors
			return HESUtil.createErrorResponse(500, e.getMessage());
		}
	}
	
	@Override
	public Map<String, Object> getActionList() {
	    DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
	            .dynamoDbClient(
	                    DynamoDbClient.builder()
	                            .region(Region.US_EAST_1)
	                            .credentialsProvider(StaticCredentialsProvider.create(
	                            		AwsBasicCredentials.create(Creds.ACCKEY,
                                                Creds.ACCSECRET)))
	                            .build())
	            .build();
	    DynamoDbTable<HESAction> tHESActionTable = enhancedClient.table("HESActions",
	            TableSchema.fromBean(HESAction.class));
	    List<HESAction> actionList = new ArrayList<>();
	    // The HESAction table exists already and has items
	    PageIterable<HESAction> scannedItems = tHESActionTable.scan();
	    for (Page<HESAction> page : scannedItems) {
	        actionList.addAll(page.items());
	    }
	    // Convert the list to a JSON string
	    ObjectMapper objectMapper = new ObjectMapper();
	    String jsonString;
	    try {
	        jsonString = objectMapper.writeValueAsString(actionList);
	    } catch (JsonProcessingException e) {
	        // Handle the exception (e.g., log it or return an error message)
	        jsonString = "Error converting to JSON: " + e.getMessage();
	    }
	    Map<String, Object> oktaResponse = new HashMap<>();
        oktaResponse.put("statusCode", 200);
        oktaResponse.put("headers", HESUtil.createHeaders());
        oktaResponse.put("body", jsonString);
	    return oktaResponse;
	}


}

package com.wm.serviceImpl;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.ArrayList;

import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.api.UserApi;
import com.okta.sdk.resource.client.ApiClient;
import com.okta.sdk.resource.model.User;
import com.wm.beans.HESUser;
import com.wm.service.HESUserService;

public class HESUserServiceImpl implements HESUserService{
	
	private static final Logger logger = Logger.getLogger(HESUserServiceImpl.class.getName());
	
	private UserApi getUserApi(Map<String, String> config) {
        String oktaDomain = config.get("oktaDomain");
        String oktaToken = config.get("oktaToken");

        ApiClient client = Clients.builder()
                .setOrgUrl(oktaDomain)
                .setClientCredentials(new TokenClientCredentials(oktaToken))
                .build();

        return new UserApi(client);
    }

	@Override
	public List<HESUser> getUserList(Map<String, String> config) {
		 UserApi userApi = getUserApi(config);
		 

         logger.info("Received response: " + userApi);
	        List<User> users = userApi.listUsers(null, null, null, null, null, null, null);

	        List<HESUser> oktaUsers = new ArrayList<HESUser>();
	        for (User user : users) {
	        	 logger.info("Received response: " + user.getId());
	            oktaUsers.add(
	                    HESUser.builder()
	                            .id(user.getId())
	                            .firstName(user.getProfile().getFirstName())
	                            .lastName(user.getProfile().getLastName())
	                            .hesClockedIn(
	                                    String.valueOf(user.getProfile().getAdditionalProperties().get("hes_clockedin")))
	                            .hesClockedInTS(
	                                    String.valueOf(user.getProfile().getAdditionalProperties().get("hes_clockedints")))
	                            .build());
	        }
	        return oktaUsers;
	}

	@Override
	public HESUser getUser(Map<String, String> config) {
		 UserApi userApi = getUserApi(config);

	        User user = userApi.getUser(config.get("oktaUserID"));
	        logger.info("Receivednse: " + user);
	        return HESUser.builder()
	                .id(user.getId())
	                .firstName(user.getProfile().getFirstName())
	                .lastName(user.getProfile().getLastName())
	                .hesClockedIn(String.valueOf(user.getProfile().getAdditionalProperties().get("hes_clockedin")))
	                .hesClockedInTS(String.valueOf(user.getProfile().getAdditionalProperties().get("hes_clockedints")))
	                .build();

	}

}

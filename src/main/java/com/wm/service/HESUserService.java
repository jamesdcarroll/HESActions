package com.wm.service;

import java.util.List;
import java.util.Map;

import com.wm.beans.HESUser;

public interface HESUserService {
	
	public List<HESUser> getUserList(Map<String, String> config);

   public  HESUser getUser(Map<String, String> config);

}

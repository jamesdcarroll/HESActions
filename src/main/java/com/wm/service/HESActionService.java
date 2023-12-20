package com.wm.service;

import java.util.Map;

public interface HESActionService {
	
	public Map<String, Object> postDisableAllCheckinRules();
	public Map<String, Object> postEnableAllChekinRules();
	public Map<String, Object> getActionList();

}

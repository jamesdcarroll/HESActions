package com.wm.beans;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class HESAction {
    private String actionID;
    private String description;
    private String name;
    
    @DynamoDbPartitionKey
    public String getActionID() {
        return actionID;
    }
    public void setActionID(String actionID) {
        this.actionID = actionID;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String asJSON(){
        String ret = "{";
        ret+= "\"name\":\"" + this.name + "\",";
        ret+= "\"actionID\":\"" + this.actionID + "\",";
        ret+= "\"description\":\"" + this.description + "\"";
        ret+= "},";

        return ret;
    }

}

package com.leaderrun.esb;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.leaderrun.esb.event.BusinessEvent;

public interface BusinessEventListener {

	boolean onMessage(List<BusinessEvent<JsonNode>> messages);
	
}

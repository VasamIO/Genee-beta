package com.genee.event;

import java.util.HashMap;
import java.util.Map;

public class EventData {

	private Map<String, Object> data;

	public EventData() {
		data = new HashMap<>();
	}

	public void set(String key, Object value) {
		data.put(key, value);
	}

	public Object get(String key) {
		return data.get(key);
	}

}

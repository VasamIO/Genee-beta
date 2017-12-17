package com.genee.event;

import java.util.HashMap;
import java.util.Map;

import com.genee.service.Service;
import com.genee.service.ServiceList;
import com.genee.utils.logging.Logger;

public class EventMap {

	private Map<EventType, ServiceList> map;
	// private List<Service> registeredServiceList = new ArrayList<>();

	public EventMap() {
		map = new HashMap<>();
	}

	public void add(EventType eventCode, Service service) {
		ServiceList serviceList = map.get(eventCode);
		if (serviceList != null) {
			if (serviceList.get(service.getId()) == null) {
				serviceList.add(service);
				// registeredServiceList.add(service);
			} else {
				Logger.log(Logger.TYPE.SERVERE, "Duplicate Service registration is happening for : " + service.getName());
			}
		} else {
			serviceList = new ServiceList();
			serviceList.add(service);
			map.put(eventCode, serviceList);
		}
	}

	public ServiceList get(EventType eventType) {
		return map.get(eventType);
	}

}

package com.genee.event;

import java.util.HashMap;
import java.util.Map;

import com.genee.event.EventPool.Execution;
import com.genee.service.Service;
import com.genee.service.ServiceList;
import com.genee.utils.logging.Logger;
import com.genee.utils.logging.Logger.TYPE;

public class EventBus {

	private final EventMap eventMap;
	private static Map<String, String> availableServices;

	public EventBus() {
		eventMap = new EventMap();
		availableServices = new HashMap<>();
	}

	public void register(EventType eventCode, Service service) {
		Logger.log(TYPE.EVENT_REGISTERED, service.getName(), eventCode.toString());
		eventMap.add(eventCode, service);
	}

	public void addService(Service service) {
		availableServices.put(service.getId(), service.getName());
	}

	public static String getServiceName(String id) {
		return availableServices.get(id);
	}

	public Map<String, String> getAvaiableServices() {
		return availableServices;
	}

	public void rise(Event event) {
		synchronized (event) {
			Logger.log(TYPE.EVENT_RISED, event);
			ServiceList serviceList = eventMap.get(event.getType());
			if (serviceList == null) {
				Logger.log(TYPE.SERVERE, "No Events are registred with: " + event.getType());
				return;
			}
			for (Service service : serviceList) {
				EventPool.newAsyncProcess(service.getName(), "StartService", new Execution() {
					@Override
					public void execute() throws Exception {
						service.onEventCaught(event);
					}
				});
			}
		}
	}
}

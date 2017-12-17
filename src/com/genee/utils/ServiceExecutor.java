package com.genee.utils;

import com.genee.event.EventBus;
import com.genee.service.Service;
import com.genee.utils.logging.Logger;
import com.genee.utils.logging.Logger.TYPE;

public class ServiceExecutor {

	@SafeVarargs
	public static void executeServices(EventBus eventBus, Class<?>... services) {
		for (Class<?> entry : services) {
			Service service;
			try {
				service = (Service) entry.newInstance();
				service.setEventBus(eventBus);
				try {
					service.start();
					Logger.log(TYPE.INFO, service.getName() + " is initialized");
				} catch (Exception e) {
					Logger.log(TYPE.SERVERE, service.getName() + " is failed to initialized, " + e.getMessage());
				}
				service.register(eventBus);
				eventBus.addService(service);
			} catch (InstantiationException | IllegalAccessException e) {
				Logger.log(TYPE.SERVERE, "ExecuteServices falied to intialize one of the service :" + e.getMessage());
			}
		}
	}

}

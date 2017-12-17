package com.genee;

import com.genee.event.EventBus;
import com.genee.service.LEDService;
import com.genee.service.WakeUpService;
import com.genee.utils.ServiceExecutor;
import com.genee.utils.logging.Logger;
import com.genee.utils.logging.Logger.TYPE;

public class App {

	public static void start() {
		Logger.log(TYPE.INFO, "App started");
		EventBus eventBus = new EventBus();
		ServiceExecutor.executeServices(eventBus, WakeUpService.class, LEDService.class);
		// Enable ThreadMonitor when debugging/CPU monitoring is required
		// ThreadMonitor.start();
	}

	public static void main(String[] args) {
		App.start();
	}

}

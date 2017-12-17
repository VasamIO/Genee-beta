package com.genee.service;

import com.genee.event.Event;
import com.genee.event.EventBus;
import com.genee.event.EventType;
import com.genee.utils.Util;
import com.genee.utils.logging.Logger;
import com.genee.utils.logging.Logger.TYPE;

public class LEDService implements Service {

	public final String NAME = "LEDService";
	public final String ID = Util.getUniqueId();
	EventBus eventBus;

	@Override
	public String getName() {
		return this.NAME;
	}

	@Override
	public String getId() {
		return this.ID;
	}

	@Override
	public void register(EventBus eventBus) {
		eventBus.register(EventType.LISTENING_WAKEUP_WORD, this);
		eventBus.register(EventType.WAKEUP_EVENT, this);
	}

	@Override
	public void onEventCaught(Event event) {
		if (event.getType() == EventType.LISTENING_WAKEUP_WORD) {
			System.out.println("LED Blinking for listening wakeup word...");
		} else if (event.getType() == EventType.WAKEUP_EVENT) {
			System.out.println("LED Blinking...");
			Event startWakeupEvent = new Event(getName(), EventType.START_WAKEUP_EVENT);
			eventBus.rise(startWakeupEvent);
		} else {
			Logger.log(TYPE.SERVERE, "LEDService is worngly listening to event :" + event.getType());
		}
	}

	@Override
	public void start() throws Exception {

	}

	@Override
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void shutDown() {

	}

}

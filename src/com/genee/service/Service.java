package com.genee.service;

import com.genee.event.Event;
import com.genee.event.EventBus;

public interface Service {

	public String getName();

	public String getId();

	public void register(EventBus eventBus);

	public void onEventCaught(Event event);

	public void start() throws Exception;

	public void setEventBus(EventBus eventBus);

	public void shutDown();
}

package com.genee.service;

import java.io.IOException;

import com.genee.event.Event;
import com.genee.event.EventBus;
import com.genee.event.EventPool;
import com.genee.event.EventPool.Execution;
import com.genee.event.EventType;
import com.genee.utils.Util;
import com.genee.utils.logging.Logger;
import com.genee.utils.logging.Logger.TYPE;
import com.genee.utils.speech.SphnixLiveSpeech;
import com.genee.utils.speech.WakeUpSpeech;

public class WakeUpService implements Service {

	public final String NAME = "WakeUpService";
	public final String ID = Util.getUniqueId();

	private EventBus eventBus;
	WakeUpSpeech wakeUpSpeech;

	public WakeUpService() {
		try {
			wakeUpSpeech = new SphnixLiveSpeech();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
		eventBus.register(EventType.START_WAKEUP_EVENT, this);
	}

	@Override
	public void onEventCaught(Event event) {
		if(event.getType() == EventType.START_WAKEUP_EVENT) {
			try {
				start();
			} catch (Exception e) {
				Logger.log(TYPE.SERVERE, "Exception while START_WAKEUP_EVENT" + e.getMessage());
			}
		}
	}

	@Override
	public void start() throws Exception {
		EventPool.newAsyncProcess(this.getName(), "WakeUpSpeechListener", new Execution() {
			@Override
			public void execute() throws Exception {
				wakeUpSpeech.startListening();
				Event startListenEvent = new Event(getName(), EventType.LISTENING_WAKEUP_WORD);
				eventBus.rise(startListenEvent);
				String result;
				while (true) {
					result = wakeUpSpeech.getResults();
					if (result.contains("hello genie")) {
						break;
					}
				}
				Event wakeupEvent = new Event(getName(), EventType.WAKEUP_EVENT);
				eventBus.rise(wakeupEvent);
				shutDown();
			}
		});
	}

	@Override
	public void shutDown() {
		wakeUpSpeech.stopListening();
		Logger.log(TYPE.INFO, this.getName() + " is shutdown");
	}

	@Override
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

}

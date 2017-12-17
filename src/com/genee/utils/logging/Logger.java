package com.genee.utils.logging;

import com.genee.event.Event;

public class Logger {

	public enum TYPE {
		ERROR, SERVERE, DEBUG, INFO, EVENT_RISED, EVENT_REGISTERED
	}

	public static void log(TYPE type, Object... msgs) {
		Object msg = msgs[0];
		if (type == TYPE.EVENT_RISED) {
			Event event = (Event) msg;
			System.out.println("EVENT_RAISED: " + event.getType() + " by " + event.getOwner());
		} else if (type == TYPE.EVENT_REGISTERED) {
			System.out.println("EVENT_REGISTERED: " + msgs[1] + " with " + msg);
		} else if (type == TYPE.SERVERE) {
			System.err.println("SERVERE: " + msg);
		} else if (type == TYPE.ERROR) {
			System.err.println("ERROR: " + msg);
		} else {
			System.out.println(type + ": " + msg);
		}
	}

}

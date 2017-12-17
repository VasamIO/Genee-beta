package com.genee.event;

import java.util.HashMap;
import java.util.Map;

import com.genee.utils.logging.Logger;
import com.genee.utils.logging.Logger.TYPE;

public class EventPool {

	private static ThreadGroup eventPool = new ThreadGroup("EventPool");
	private static Map<String, ThreadGroup> threadGroups = new HashMap<>();

	public interface Execution {
		public void execute() throws Exception;
	}
	
	public static void list() {
		eventPool.list();
	}
	
	public static int activeThreadCount() {
		return eventPool.activeCount();
	}
	
	public static void stop(String threadGroupName) {
		threadGroups.get(threadGroupName).interrupt();
	}

	public static ThreadGroup getThreadGroup(String identifier) {
		if (threadGroups.containsKey(identifier)) {
			return threadGroups.get(identifier);
		}
		ThreadGroup threadGroup = new ThreadGroup(eventPool, identifier);
		threadGroups.put(identifier, threadGroup);
		return threadGroup;
	}

	public static void newAsyncProcess(String groupName, String identifier, Execution execution) {
		ThreadGroup threadGroup = getThreadGroup(groupName);
		Thread t = new Thread(threadGroup, new Runnable() {
			@Override
			public void run() {
				try {
					execution.execute();
				} catch (Exception e) {
					Logger.log(TYPE.SERVERE, identifier + " newAsyncProcess throws error," + e.getMessage());
				}
			}
		});
		t.setName(identifier);
		t.start();
	}

}

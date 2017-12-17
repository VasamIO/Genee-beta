package com.genee.utils;

import com.genee.event.EventPool;
import com.genee.event.EventPool.Execution;
import com.genee.utils.logging.Logger;
import com.genee.utils.logging.Logger.TYPE;

public class ThreadMonitor {

	public static void start() {
		EventPool.newAsyncProcess(ThreadMonitor.class.getSimpleName(), "CPUMonitor", new Execution() {
			@Override
			public void execute() throws Exception {
				while(true) {
					Logger.log(TYPE.INFO, "ThreadMonitor is running");
					EventPool.list();
					Logger.log(TYPE.INFO, "Running threads: " + EventPool.activeThreadCount());
					Thread.sleep(4000);
				}
			}
		});
	}

}

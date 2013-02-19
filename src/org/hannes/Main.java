package org.hannes;

import java.io.FileReader;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hannes.task.CreateRotationTask;


public class Main {

	/**
	 * Cached threadpool n shit
	 */
	private static final ExecutorService service = Executors.newCachedThreadPool();

	/**
	 * The scheduled executor service
	 */
	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public static void main(String[] args) throws Exception {
		/*
		 * Load the properties
		 */
		final Properties properties = new Properties();
		properties.load(new FileReader(".properties"));
		
		/*
		 * Schedule to update pictures err' 10 minits
		 */
		scheduler.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				service.submit(new CreateRotationTask(properties));
			}
			
		}, 0, 1, TimeUnit.MINUTES);
	}
	
	/**
	 * @return the service
	 */
	public static ExecutorService getService() {
		return service;
	}

}
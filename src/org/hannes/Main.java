package org.hannes;

import java.io.FileReader;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hannes.reddit.Subreddit;
import org.hannes.task.FetchImageTask;
import org.hannes.task.FetchSubredditTask;


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
		Properties properties = new Properties();
		properties.load(new FileReader(".properties"));
		
		/*
		 * List all the subreddits
		 */
		final String[] subredditNames = properties.getProperty("reddits").split(",");
		
		/*
		 * Get the depth of the search
		 */
		final int depth = Integer.valueOf(properties.get("depth").toString());
		
		/*
		 * Schedule to update pictures err' 10 minits
		 */
		scheduler.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				service.submit(new FetchSubredditTask(depth, subredditNames));
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
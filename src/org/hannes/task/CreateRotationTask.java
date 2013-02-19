package org.hannes.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.hannes.reddit.Entry;
import org.hannes.reddit.Subreddit;
import org.hannes.util.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CreateRotationTask implements Runnable {

	/**
	 * The user agent
	 */
	private static final String USER_AGENT = System.getProperty("java.version");

	/**
	 * The subreddits to be fetched
	 */
	private final String[] subredditNames;
	
	/**
	 * The depth (amount of posts to be included in the search)
	 */
	private final int depth;
	
	/**
	 * The duration of a wallpaper
	 */
	private final float duration;
	
	/**
	 * The duration of a wallpaper
	 */
	private final float transitionDuration;
	
	public CreateRotationTask(Properties properties) {
		this.depth = Integer.valueOf(properties.getProperty("depth"));
		this.subredditNames = properties.getProperty("reddits").split(",");
		this.duration = Float.valueOf(properties.getProperty("duration"));
		this.transitionDuration = Float.valueOf(properties.getProperty("transition_duration"));
	}

	@Override
	public void run() {
		try {
			/*
			 * Get a list of all the subreddits
			 */
			List<Subreddit> subreddits = new ArrayList<>();
			JSONParser parser = new JSONParser();
			
			for (String name : subredditNames) {
				/*
				 * Create the subreddit
				 */
				Subreddit subreddit = new Subreddit(name);
				
				/*
				 * Create the connection to the subreddit
				 */
				URL url = new URL("http://www.reddit.com/r/" + name + ".json");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("User-Agent", USER_AGENT);
				
				/*
				 * Get the JSON array containing information about the subreddits
				 */
				Object obj = parser.parse(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine());
				JSONArray array = (JSONArray) ((JSONObject) ((JSONObject) obj).get("data")).get("children");
				
				for (int i = 0; i < depth; i++) {
					JSONObject data = (JSONObject) ((JSONObject) array.get(i)).get("data");
					
					/*
					 * Get the entry information
					 */
					String imageUrl = data.get("url").toString();
					String id = data.get("id").toString();
					
					/*
					 * Add the entry
					 */
					subreddit.getEntries().add(new Entry(id, imageUrl, name));
				}
				subreddits.add(subreddit);
			}
			
			/*
			 * Get a list of all the images
			 */
			List<File> images = new ArrayList<>();
			for (Subreddit subreddit : subreddits) {
				for (Entry entry : subreddit.getEntries()) {
					URL url = Utils.getImageURL(entry.getUrl());
					if (url != null) {
						File path = new File("output/" + entry.getId() + ".png");
						if (!path.exists()) {
							ImageIO.write(ImageIO.read(url), "png", path);
						}
						images.add(path);
					}
				}
			}
			
			/*
			 * Create a slideshow file
			 */
			StringBuilder builder = new StringBuilder();
			builder.append("<background>").append("\n");
			for (int i = 0; i < images.size(); i++) {
				File image = images.get(i);
				
				builder.append("<static>").append("\n");
				builder.append("<duration>" + duration + "</duration>").append("\n");
				builder.append("<file>" + image.getAbsolutePath() + "</file>").append("\n");
				builder.append("</static>").append("\n");
	
				builder.append("<transition>").append("\n");
				builder.append("<duration>" + transitionDuration + "</duration>").append("\n");
				builder.append("<from>" + image.getAbsolutePath() + "</from>").append("\n");
				if (i + 1 >= images.size()) {
					builder.append("<to>" + images.get(0).getAbsolutePath() + "</to>").append("\n");
				} else {
					builder.append("<to>" + images.get(i + 1).getAbsolutePath() + "</to>").append("\n");
				}
				builder.append("</transition>").append("\n");
			}
			
			builder.append("</background>");
			
			FileWriter out = new FileWriter(new File("rotation.xml"));
			out.write(builder.toString());
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}

}

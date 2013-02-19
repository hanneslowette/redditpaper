package org.hannes.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.hannes.Main;
import org.hannes.reddit.Entry;
import org.hannes.reddit.Subreddit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Fetches the list of subreddits that the user wants pictures
 * from for his/her wallpaper rotation
 * 
 * @author red
 *
 */
public class FetchSubredditTask extends Task<List<Subreddit>> {

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
	 * 
	 * @param depth
	 *            The depth (amount of posts to be included in the search)
	 * @param subredditNames
	 */
	public FetchSubredditTask(int depth, String... subredditNames) {
		this.depth = depth;
		this.subredditNames = subredditNames;
	}

	@Override
	public List<Subreddit> execute() throws Exception {
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
		return subreddits;
	}

	@Override
	public void complete(List<Subreddit> subreddits) throws Exception {
		Main.getService().submit(new FetchImageTask(subreddits));
	}

}
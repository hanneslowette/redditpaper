package org.hannes.reddit;

/**
 * Represents a reddit entry
 * 
 * @author red
 *
 */
public class Entry {
	
	/**
	 * The id of the post (for upvotes/downvotes)
	 */
	private final String id;
	
	/**
	 * The url to the post
	 */
	private final String url;

	/**
	 * The subreddit the post is in
	 */
	private final String subreddit;

	public Entry(String id, String url, String subreddit) {
		this.id = id;
		this.url = url;
		this.subreddit = subreddit;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the subreddit
	 */
	public String getSubreddit() {
		return subreddit;
	}

}
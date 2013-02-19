package org.hannes.reddit;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a subreddit
 * 
 * @author red
 *
 */
public class Subreddit {

	/**
	 * The list of entries
	 */
	private List<Entry> entries = new ArrayList<>();

	/**
	 * The name of the subreddit
	 */
	private final String name;

	public Subreddit(String name) {
		this.name = name;
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	public String getName() {
		return name;
	}

}
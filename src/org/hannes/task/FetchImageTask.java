package org.hannes.task;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.hannes.reddit.Entry;
import org.hannes.reddit.Subreddit;
import org.hannes.util.Utils;

/**
 * Gets the images from a collection of subreddits
 * 
 * @author red
 *
 */
public class FetchImageTask extends Task<List<File>> {

	/**
	 * The list of subreddits to get the images from
	 */
	private final List<Subreddit> subreddits;

	public FetchImageTask(List<Subreddit> subreddits) {
		this.subreddits = subreddits;
	}

	@Override
	public List<File> execute() throws Exception {
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
		return images;
	}

	@Override
	public void complete(List<File> images) throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.append("<background>").append("\n");
		for (int i = 0; i < images.size(); i++) {
			File image = images.get(i);
			
			builder.append("<static>").append("\n");
			builder.append("<duration>10.0</duration>").append("\n");
			builder.append("<file>" + image.getAbsolutePath() + "</file>").append("\n");
			builder.append("</static>").append("\n");

			builder.append("<transition>").append("\n");
			builder.append("<duration>1.0</duration>").append("\n");
			builder.append("<from>" + image.getAbsolutePath() + "</from>").append("\n");
			if (i + 1 >= images.size()) {
				builder.append("<to>" + images.get(0).getAbsolutePath() + "</to>").append("\n");
			} else {
				builder.append("<to>" + images.get(i + 1).getAbsolutePath() + "</to>").append("\n");
			}
			builder.append("</transition>").append("\n");
		}
		
		builder.append("</background>");
		
		FileWriter out = new FileWriter(new File("/usr/share/backgrounds/rotation.xml"));
		out.write(builder.toString());
		out.close();
	}

}
package org.hannes.util;

import java.net.MalformedURLException;
import java.net.URL;

public class Utils {

	public static URL getImageURL(String url) throws MalformedURLException {
		if (url.endsWith(".gif") || url.endsWith(".png") || url.endsWith(".jpg")
				 || url.endsWith(".bmp")) {
			return new URL(url);
		}
		else if (url.contains("/a/")) {
			return null;
		}
		else if (url.contains("imgur")) {
			return new URL(url + ".png");
		}
		return null;
	}

	public static URL getImageURL(Object object) throws MalformedURLException {
		return getImageURL(object.toString());
	}

}
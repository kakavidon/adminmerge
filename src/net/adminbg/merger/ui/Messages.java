package net.adminbg.merger.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * Reads values from a properties file. Should be replaced with a file editable
 * by the user.
 * 
 * @author kakavidon
 * 
 */
public class Messages {

	public static final String BUNDLE_NAME = "net.adminbg.merger.ui.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}

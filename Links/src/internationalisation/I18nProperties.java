package internationalisation;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nProperties {

	private static final String ALTERNATIVE_TEXT = "missing";
	public static final String BUTTON_FRAMERATE_TOOLTIP = "button.framerate.tooltip";
	public static final String BUTTON_SPEED_TOOLTIP = "button.speed.tooltip";
	public static final String BUTTON_LOOP_TOOLTIP = "button.loop.tooltip";
	public static final String BUTTON_PLAY_TOOLTIP = "button.play.tooltip";
	public static final String BUTTON_SYNC_TOOLTIP = "button.sync.tooltip";
	public static final String BUTTON_INFO_TOOLTIP = "button.info.tooltip";
	public static final String BUTTON_STOP_TOOLTIP = "button.stop.tooltip";
	public static final String BUTTON_PREV_TOOLTIP = "button.prev.tooltip";
	public static final String BUTTON_NEXT_TOOLTIP = "button.next.tooltip";
	public static final String BUTTON_MOVING_TOOLTIP = "button.moving.tooltip";
	public static final String BUTTON_DRAW_TOOLTIP = "button.draw.tooltip";
	public static final String BUTTON_ZOOMPLUS_TOOLTIP = "button.zoomplus.tooltip";
	public static final String BUTTON_ZOOMMINUS_TOOLTIP = "button.zoomminus.tooltip";
	public static final String BUTTON_ZOOMRESET_TOOLTIP = "button.zoomreset.tooltip";
	public static final String BUTTON_SNAPRESET_TOOLTIP = "button.snapreset.tooltip";
	public static final String BUTTON_LINKS_TOOLTIP = "button.links.tooltip";

	private static I18nProperties instance = null;
	private ResourceBundle res;

	private I18nProperties() {

	}

	/**
	 * @return the instance or null if an error occurred during the properties
	 *         loading.
	 */
	public static I18nProperties getInstance() {
		if (null == instance) {
			instance = new I18nProperties();
			try {
				instance.loadProperties();
				if (null == instance.res) {
					instance = null;
					// TODO must log as war
				}
			} catch (Exception exp) {
				// TODO must log as error
			}
		}
		return instance;
	}

	private void loadProperties() {
		Locale locale = Locale.getDefault();
		res = ResourceBundle.getBundle("I18nPropertiesRessources", locale);
	}

	/***
	 * Return the correct content or the text "missing" else.
	 */
	public String getSafeText(String key) {
		String resultText = null;

		try {
			Object unSafeObject = res.getObject(key);
			if (unSafeObject instanceof String) {
				resultText = (String) unSafeObject;
			}
		} catch (Exception exp) {
			// TODO must log as war
		}

		if (null == resultText) {
			resultText = ALTERNATIVE_TEXT;
		}
		return resultText;
	}

}
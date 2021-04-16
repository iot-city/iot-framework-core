package org.iotcity.iot.framework.core.i18n;

import java.util.HashMap;
import java.util.Map;

import org.iotcity.iot.framework.core.util.helper.JavaHelper;
import org.iotcity.iot.framework.core.util.helper.StringHelper;

/**
 * Default locale text object.
 * @author Ardon
 */
public class DefaultLocaleText implements LocaleText {

	// --------------------------- Private fields ----------------------------

	/**
	 * The locale text name (e.g. "CORE").
	 */
	final String name;
	/**
	 * The locale text language key (e.g. "en_US", "zh_CN").
	 */
	final String lang;
	/**
	 * The text map with key and text value.
	 */
	final Map<Object, Object> texts;

	// --------------------------- Constructor ----------------------------

	/**
	 * Constructor for default locale text object.
	 * @param name The locale text name (e.g. "CORE").
	 * @param lang The locale text language key (e.g. "en_US", "zh_CN").
	 * @param texts The text map with key and text value.
	 */
	public DefaultLocaleText(String name, String lang, Map<Object, Object> texts) {
		this.name = name == null ? "" : name;
		this.lang = lang == null ? "" : lang;
		this.texts = texts == null ? new HashMap<>(JavaHelper.getMapInitialCapacity(1)) : texts;
	}

	// --------------------------- Private methods ----------------------------

	/**
	 * Gets default output text.
	 * @param key The text key (e.g. "locale.text.name").
	 * @return Replacement for not exists string.
	 */
	private final String getDefaultText(String key) {
		StringBuilder sb = new StringBuilder();
		sb.append("XXX [TEXT: ").append(name).append(" - ").append(lang).append(": \"").append(key).append("\" NOT EXISTS]");
		return sb.toString();
	}

	// --------------------------- Override methods ----------------------------

	@Override
	public boolean config(LocaleConfigText data, boolean reset) {
		if (data == null || data.texts == null) return false;
		if (reset) this.texts.clear();
		this.texts.putAll(data.texts);
		return true;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getLang() {
		return lang;
	}

	@Override
	public String text(String key, Object... params) {
		if (key == null) return getDefaultText(key);
		String text = (String) texts.get(key);
		if (text == null) return getDefaultText(key);
		if (params != null && params.length > 0) {
			return StringHelper.format(text, params);
		} else {
			return text;
		}
	}

}

package org.iotcity.iot.framework.core.i18n;

import java.util.Map;

/**
 * Default locale factory.
 * @author Ardon
 */
public class DefaultLocaleFacotry extends BaseLocaleFacotry {

	@Override
	protected LocaleText createLocaleText(String name, String lang, Map<Object, Object> texts) {
		return new BaseLocaleText(name, lang, texts);
	}

}

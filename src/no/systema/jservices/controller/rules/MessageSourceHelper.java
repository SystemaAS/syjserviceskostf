package no.systema.jservices.controller.rules;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/**
 * Help class accessing messages in message.properties
 * 
 * Using {@link Locale.getDefault()}
 * 
 * @author Fredrik Möller
 * @date Jan 23, 2019
 *
 */
@Service
public class MessageSourceHelper {

	@Autowired
	private ApplicationContext context;
	@Autowired
	CookieLocaleResolver localeResolver;

	/**
	 * Decode key/id into messages from message.properties.
	 * Using locale from CookieLocaleResolver
	 * 
	 * @param id, key in message.properties
	 * @param params
	 * @return decoded message
	 */
	public String getMessage(String id, Object[] params) {
		return context.getMessage(id, params, Locale.getDefault());
	}
	
}

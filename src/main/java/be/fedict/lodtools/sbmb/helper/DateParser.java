/*
 * Copyright (c) 2017, Bart Hanssens <bart.hanssens@fedict.be>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package be.fedict.lodtools.sbmb.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date parser helper class
 * 
 * @author Bart.Hanssens
 */
public class DateParser {
	private final static Logger LOG = LoggerFactory.getLogger(DateParser.class);
	
	private final static DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private final static Map<String, DateTimeFormatter> LONGS = new HashMap<>();
	
	static {
		LONGS.put("nl", DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("nl-BE")));
		LONGS.put("fr", DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("fr-BE")));
	}
	
	/**
	 * Parse short date string to LocalDate
	 * 
	 * @param str
	 * @return local date or null
	 */
	public static LocalDate parseShort(String str) {
		if (str == null) {
			return null;
		}
		
		try {
			return LocalDate.parse(str, SHORT);
		} catch (DateTimeParseException dte) {
			return null;
		}
	}
	
	/**
	 * Parse long date string to LocalDate
	 * 
	 * @param str string to parse
	 * @param lang language/locale code
	 * @return local date
	 * @throws DateTimeParseException 
	 */
	private static LocalDate parseLongLocale(String str, String lang) 
												throws DateTimeParseException{
		String d = str.toLowerCase().replaceAll("\\.", "");
		
		if (lang.equals("fr")) {
			d = d.replaceFirst("fevrier", "février")
					.replaceFirst("aout", "août")
					.replaceFirst("decembre", "décembre")
					.replaceFirst("1er", "1");
		}
		return LocalDate.parse(d, LONGS.get(lang));
	}
	
	/**
	 * Parse long date string to LocalDate
	 * 
	 * @param str string to parse
	 * @param lang language code guess
	 * @return local date or null
	 */
	public static LocalDate parseLong(String str, String lang) {
		if (str == null || lang == null) {
			return null;
		}
		
		LocalDate date = null;
		
		try {
			date = parseLongLocale(str, lang);
		} catch (DateTimeParseException dte) {
			for(String alt: LONGS.keySet()) {
				if (! alt.equals(lang) && date == null) {
					LOG.warn("Couldn't parse {} in {}, trying {}", str, lang, alt);
					// fallback 
					try {
						date = parseLongLocale(str, alt);
					} catch (DateTimeParseException dte2) {
						//
					}
				}
			}
		}
		return date;
	}
	
}

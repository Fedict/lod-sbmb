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

import be.fedict.lodtools.sbmb.*;
import be.fedict.lodtools.sbmb.helper.LegalDoc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	
	public static LocalDate parseShort(String str) throws DateTimeParseException {
		return LocalDate.parse(str, SHORT);
	}
	
	public static LocalDate parseLong(String str, String lang) {
		String d = str.toLowerCase();
		if (lang.equals("fr")) {
			d = d.replaceFirst("fe", "fé")
					.replaceFirst("ut", "ût")
					.replaceFirst("de", "dé");
		}
		return LocalDate.parse(d, LONGS.get(lang));
	}
}

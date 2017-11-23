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
package be.fedict.lodtools.sbmb;

import be.fedict.lodtools.sbmb.helper.DateParser;
import be.fedict.lodtools.sbmb.helper.LegalDoc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Connection.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse overview page
 * 
 * @author Bart.Hanssens
 */
public class PageParser {
	private final static Logger LOG = LoggerFactory.getLogger(PageParser.class);
			
	private LegalDoc parseDesc(Element td, LegalDoc doc, String lang) {
		Element rawtitle = td.selectFirst("font");
		
		String t = rawtitle.ownText();
		LOG.info("Found {}", t);

		String[] split = t.split("\\.? [-_] ", 2);
		if (split.length < 2) {
			split = t.split(". ", 2);
			if (split.length < 2) {
				LOG.error("Could not split title");
				return doc;
			}
		}
		String docstr = split[0];
		String title = split[1];
		
		LocalDate docdate = null;
		try  {
			docdate = DateParser.parseLong(docstr, lang);
		} catch (DateTimeParseException ex) {
			LOG.error("Could not parse doc date {}", ex.getMessage());
		}
		
		LocalDate pubdate = null;
		Element pubel = rawtitle.selectFirst("font b font");
		try  {
			pubdate = DateParser.parseShort(pubel.ownText());
		} catch (DateTimeParseException ex) {
			LOG.error("Could not parse pub date {}", ex.getMessage());
		}
		
		Element srcel = rawtitle.selectFirst("font font font b font");
		if (srcel == null) {
			LOG.warn("No publication source found");
		}
		String source = (srcel != null) ? srcel.ownText() : null;
		doc.setDesc(docdate, title, pubdate, source);
		return doc;
	}
	
	/**
	 * Parse links to Justel/MB in overview
	 * 
	 * @param td
	 * @param doc
	 * @return 
	 */
	private LegalDoc parseLinks(Element td, LegalDoc doc) {
		Elements links = td.select("a");
		if (links == null) {
			LOG.error("No links found");
			return doc;
		}
		for (Element link: links) {
			try {
				URL u = new URL(link.attr("href"));
				if (link.ownText().trim().equals("Justel")) {
					doc.setId(u.toString().replaceAll("/justel", ""));
					doc.setJustel(u);
				} else {
					doc.setSbmb(u);
				}
			} catch (MalformedURLException ex) {
				LOG.warn(ex.getMessage());
			}
		}
		return doc;
	}
	
	/**
	 * Parse html to list of legal doc
	 * 
	 * @param html HTML 
	 * @param lang language code
	 * @return
	 * @throws IOException 
	 */
	public List<LegalDoc> parse(String html, String lang) throws IOException {
		List<LegalDoc> l = new ArrayList();
		
		Document doc = Jsoup.parse(html);
	
		Elements rows = doc.select("tr");
		for (Element row: rows) {
			Element tdDesc = row.selectFirst("td:nth-child(2)");
			if (tdDesc == null) { // row separator
				continue;
			}
			LegalDoc legal = new LegalDoc();
			legal.setLang(lang);
			
			parseDesc(tdDesc, legal, lang);
			
			Element tdJust = row.selectFirst("td:nth-child(3)");
			if (tdJust == null) {
				LOG.error("No third column found {}", row.html());
			}
			parseLinks(tdJust, legal);
			
			l.add(legal);
		}
		return l;
	}
	
	
	/**
	 * Get HTML page
	 * 
	 * @param base base URL
	 * @param type legal type
	 * @param year year (1831 or later)
	 * @param lang language code
	 * @return body of page
	 * @throws IOException 
	 */
	public Document get(String base, String type, int year, String lang) throws IOException {	
		String url = base + "/"+ type + "/" + year;
		LOG.info("Using URL {}", url);

		Document doc = Jsoup.connect(url).ignoreHttpErrors(true).execute()
										.charset("ISO-8859-1").parse();
		doc.body().attr("lang", lang);
		return doc;
	}
}

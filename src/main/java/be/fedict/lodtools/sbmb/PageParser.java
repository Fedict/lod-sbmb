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
import java.util.ArrayList;
import java.util.List;

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

		String[] split = t.split(". - ", 2);
		if (split.length < 2) {
			LOG.error("Could not split title");
			return doc;
		}
		String docstr = split[0];
		String title = split[1];
		
		LocalDate docdate = DateParser.parseLong(docstr, lang);
		if (docdate == null) {
			LOG.error("Doc date not found");
		}
		
		Element pubel = rawtitle.selectFirst("font b font");
		LocalDate pubdate = DateParser.parseShort(pubel.ownText());
		if (pubdate == null) {
			LOG.error("Publication date not found");
		}
		
		Element source = rawtitle.selectFirst("font font font b font");
		
		doc.setDesc(docdate, title, pubdate, source.ownText());
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
	 * Pase page
	 * 
	 * @param base base URL
	 * @param type legal type
	 * @param year year (1845 or later)
	 * @param lang language code
	 * @return list of legal docs
	 * @throws MalformedURLException
	 * @throws IOException 
	 */
	public List<LegalDoc> parse(String base, String type, int year, String lang) 
									throws MalformedURLException, IOException {
		List<LegalDoc> l  = new ArrayList();
		
		String url = base + "/"+ type + "/" + year;
		LOG.info("Using URL {}", url);

		Document doc = Jsoup.connect(url).ignoreHttpErrors(true)
							.execute().charset("ISO-8859-1").parse();
		if (doc == null) {
			throw new IOException();
		}
		
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
}

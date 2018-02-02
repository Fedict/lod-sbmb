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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
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
	private final static Pattern TITLE
		= Pattern.compile("^((\\d{1,2}|1er)\\.? [a-zA-Z]+ \\d{4})([ ._-]+)(.+)$");
	private final static Whitelist SAFE = Whitelist.relaxed().addTags("font");

	/**
	 * Parse description from overview page
	 *
	 * @param td table cell
	 * @param doc legal doc
	 * @param lang language code
	 * @return true on success
	 */
	private boolean parseDesc(Element td, LegalDoc doc, String lang) {
		Element rawtitle = td.selectFirst("font");
		if (rawtitle == null) {
			LOG.error("No title found");
			return false;
		}
		String t = rawtitle.ownText();
		if (t.isEmpty()) {
			LOG.error("Empty text for raw title {}", rawtitle);
			return false;
		}
		LOG.debug("Found {}", t);

		String docstr = "";
		String title = t;

		Matcher m = TITLE.matcher(t);
		if (m.matches() && m.groupCount() == 4) {
			docstr = m.group(1);
			title = m.group(4);
		} else {
			LOG.error("Could not split title {}", t);
		}

		LocalDate docdate = DateParser.parseLong(docstr, lang);
		if (docdate == null) {
			LOG.error("Could not parse doc date {}", docstr);
		}

		Element pubel = rawtitle.selectFirst("font b font");
		if (pubel == null) {
			LOG.error("No publication element found");
			return false;
		}
		
		LocalDate pubdate = DateParser.parseShort(pubel.ownText());
		if (pubdate == null) {
			LOG.error("Could not parse pub date {}", pubel.ownText());
		}

		Element srcel = rawtitle.selectFirst("font font font b font");
		if (srcel == null) {
			LOG.warn("No publication source found for {}", t);
		}
		String source = (srcel != null) ? srcel.ownText() : null;
		doc.setDesc(docdate, title, pubdate, source);
		return true;
	}

	/**
	 * Parse links to Justel/MB from overview
	 *
	 * @param td html table cell
	 * @param doc legal document
	 * @return true on success
	 */
	private boolean parseLinks(Element td, LegalDoc doc) {
		Elements links = td.select("a");
		if (links == null || links.isEmpty()) {
			LOG.error("No links found {}", td.html());
			return false;
		}
		for (Element link : links) {
			try {
				URL u = new URL(link.attr("href"));
				if (link.ownText().trim().startsWith("Justel")) {
					doc.setId(u.toString().replaceFirst("/justel", ""));
					doc.setJustel(u);
				} else {
					doc.setSbmb(u);
				}
			} catch (MalformedURLException ex) {
				LOG.error(ex.getMessage());
			}
		}
		if (doc.getId() == null) {
			LOG.error("No links found");
			return false;
		}
		return true;
	}

	/**
	 * Convert html pages to a list of legal doc
	 *
	 * @param html HTML
	 * @param lang language code
	 * @return list of legal docs
	 * @throws IOException
	 */
	public List<LegalDoc> parse(String html, String lang) throws IOException {
		List<LegalDoc> l = new ArrayList();

		Document doc = Jsoup.parse(Jsoup.clean(html, SAFE));

		Elements rows = doc.select("tr");
		for (Element row : rows) {
			Element tdDesc = row.selectFirst("td:nth-child(2)");
			if (tdDesc == null) { // row separator
				continue;
			}
			LegalDoc legal = new LegalDoc();
			legal.setLang(lang);

			if (parseDesc(tdDesc, legal, lang) == false) {
				continue;
			}

			Element tdJust = row.selectFirst("td:nth-child(3)");
			if (tdJust == null) {
				LOG.error("No third column found {}", row.html());
				continue;
			}
			if (parseLinks(tdJust, legal) == false) {
				continue;
			}

			l.add(legal);
		}
		return l;
	}

	/**
	 * Get HTML page
	 *
	 * @param base base URL
	 * @param type legal type
	 * @param year year (1800 or later)
	 * @param lang language code
	 * @return body of page
	 * @throws IOException
	 */
	public Document get(String base, String type, int year, String lang) throws IOException {
		String url = base + "/" + type + "/" + year;
		LOG.info("Using URL {}", url);

		Document doc = Jsoup.connect(url).ignoreHttpErrors(true)
							.timeout(60_000).maxBodySize(8_000_000)
							.execute()
							.charset("ISO-8859-1").parse();
		doc.body().attr("lang", lang);
		System.err.println(doc);
		return doc;
	}
}

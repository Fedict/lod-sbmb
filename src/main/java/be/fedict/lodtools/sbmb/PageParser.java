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

import java.io.IOException;
import java.net.MalformedURLException;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Parse overview page
 * 
 * @author Bart.Hanssens
 */
public class PageParser {
	private final String baseNL;
	private final String baseFR;


	/**
	 * 
	 * @param year
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException 
	 */
	public Model parse(int year) throws MalformedURLException, IOException {
		Model m = new LinkedHashModel();
		String url = baseNL + year;
		Document doc = Jsoup.connect(url).ignoreHttpErrors(true).get();
		
		if (doc == null) {
			throw new IOException();
		}
		
		Elements rows = doc.select("tr");
		for (Element row: rows) {
			Element tdDesc = row.selectFirst("td:nth-child(2)");
			if (tdDesc == null) { // row separator
				continue;
			}
			Element title = tdDesc.selectFirst("font");
			System.out.println(title.ownText());

			Element pubdate = title.selectFirst("font b font");
			System.out.println(pubdate.ownText());
			
			Element source = title.selectFirst("font font font b font");
			if (source == null) { // wrong encoding
				source = title.selectFirst("font fot font b font");
			}
			System.out.println(source.ownText());
			
			Element tdJust = row.selectFirst("td:nth-child(3)");
			Elements links = tdJust.select("a");	
		}
		return m;
	}
	
	/**
	 * 
	 * @param baseNL
	 * @param baseFR 
	 */
	public PageParser(String baseNL, String baseFR) {
		this.baseNL = baseNL;
		this.baseFR = baseFR;
	}
}

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

import java.net.URL;
import java.time.LocalDate;

/**
 * Helper class
 * 
 * @author Bart.Hanssens
 */
public class LegalDoc {
	private String id;
	private String localId;
	private String title;
	private String source;
	private String lang;
	private URL sbmb;
	private URL justel; 
	private LocalDate pubDate;
	private LocalDate docDate;

	/**
	 * Get identifier
	 * 
	 * @return identifier
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set identifier
	 * 
	 * @param id identifier 
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Get local identifier ("NUMAC")
	 * 
	 * @return identifier
	 */
	public String getLocalId() {
		return localId;
	}

	/**
	 * Set local identifier ("NUMAC")
	 * 
	 * @param id identifier 
	 */
	public void setLocalId(String localId) {
		this.localId = localId;
	}

	/**
	 * Get title
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set title
	 * 
	 * @param title title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get language code
	 * 
	 * @return language code
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * Set language code
	 * 
	 * @param lang language code
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	/**
	 * Get source of document (ministry, parliament...)
	 * 
	 * @return source 
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Set source of document (ministry, parliament...)
	 * 
	 * @param source source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Get link to publication in SB/MB
	 * 
	 * @return URL 
	 */
	public URL getSbmb() {
		return sbmb;
	}

	/**
	 * Set link to publication in SB/MB
	 * 
	 * @param sbmb URL
	 */
	public void setSbmb(URL sbmb) {
		this.sbmb = sbmb;
	}

	/**
	 * Get link to publication in Justel
	 * 
	 * @return URL
	 */
	public URL getJustel() {
		return justel;
	}

	/**
	 * Set link to publication in Justel
	 * 
	 * @param justel URL
	 */
	public void setJustel(URL justel) {
		this.justel = justel;
	}

	/**
	 * Get publication date
	 * 
	 * @return date
	 */
	public LocalDate getPubDate() {
		return pubDate;
	}

	/**
	 * Set publication date
	 * 
	 * @param pubDate date
	 */
	public void setPubDate(LocalDate pubDate) {
		this.pubDate = pubDate;
	}

	/**
	 * Get document approval date
	 * 
	 * @return date 
	 */
	public LocalDate getDocDate() {
		return docDate;
	}

	/**
	 * Set document approval date
	 * 
	 * @param docDate date
	 */
	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}
	
	public void setDesc(LocalDate docDate, String title, LocalDate pubDate, String source) {
		setTitle(title);
		setDocDate(docDate);
		setPubDate(pubDate);
		setSource(source);
	}
}

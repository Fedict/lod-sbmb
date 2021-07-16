/*
 * Copyright (c) 2018, Bart Hanssens <bart.hanssens@fedict.be>
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

import be.fedict.lodtools.sbmb.helper.LegalDoc;
import com.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writer legaldoc objects to file system.
 * 
 * @author Bart.Hanssens
 */
public class LegalDocWriterCSV implements LegalDocWriter {
	private final static Logger LOG = LoggerFactory.getLogger(LegalDocWriterCSV.class);
	
	/**
	 * Convert LocalDate to date value
	 * 
	 * @param d local date
	 * @return string
	 */
	private String toDate(LocalDate d) {
		if (d == null) {
			return null;
		}
		return d.format(DateTimeFormatter.ISO_DATE);
	}
	
	/**
	 * Write (titles of) legal documents to a file
	 * 
	 * @param docs list of legaldocs
	 * @param outfile output file
	 * @param year year
	 * @param type common type
	 * @param types language-specific types
	 * @throws IOException 
	 */
	@Override
	public void write(List<LegalDoc> docs, Path outfile, int year, String type, 
								Map<String,String> types) throws IOException {
		if (docs.isEmpty()) {
			LOG.warn("Nothing to write for {}", year);
			return;
		} 
		
		File dir = outfile.getParent().toFile();
		if (!dir.exists() && !dir.mkdirs()) {
			LOG.error("Directory {} not writable", dir);
		}

		try (BufferedWriter w = Files.newBufferedWriter(outfile);
			CSVWriter csv = new CSVWriter(w)) {
			
			String[] header = { "ID", "JUSTEL", "DOCTYPE", "NUMAC",
								"DOCDATE", "PUBDATE", "LANG", 
								"TYPE" , "SOURCE", "TITLE" };	
			csv.writeNext(header);
			
			for (LegalDoc doc: docs) {
				String[] row = {
					doc.getId(), doc.getJustel().toString(), type, doc.getLocalId(),
					toDate(doc.getDocDate()), toDate(doc.getPubDate()), doc.getLang(),
					types.get(doc.getLang()), doc.getSource(), doc.getTitle() };
				csv.writeNext(row);
			}
		}
	}
}

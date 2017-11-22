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

import be.fedict.lodtools.sbmb.helper.ELI;
import be.fedict.lodtools.sbmb.helper.LegalDoc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writer legaldoc objects to file system.
 * 
 * @author Bart.Hanssens
 */
public class LegalDocWriter {
	private final static Logger LOG = LoggerFactory.getLogger(LegalDocWriter.class);
	
	private final static ValueFactory F = SimpleValueFactory.getInstance();

	private final static Map<String,IRI> LANGS = new HashMap<>();
	private final static IRI SBMB = F.createIRI("http://org.belgif.be/cbe/org/0307_614_813#id");
		
	static {
		LANGS.put("nl", F.createIRI("http://publications.europa.eu/resource/authority/language/NED"));
		LANGS.put("fr", F.createIRI("http://publications.europa.eu/resource/authority/language/FRA"));
	}
	
	private Value toDate(LocalDate d) {
		Date date = new Date(d.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
		return F.createLiteral(date);
	}
	
	/**
	 * Write (titles of) legal documents to a file
	 * 
	 * @param docs
	 * @param outdir output directory
	 * @throws IOException 
	 */
	public void write(List<LegalDoc> docs, File outdir) throws IOException {
		Path p = Paths.get(outdir.toString(), "out.nt");
		
		
		try (BufferedWriter w = Files.newBufferedWriter(p)) {
			Model m = new LinkedHashModel();
			for (LegalDoc doc: docs) {
				IRI id = F.createIRI(doc.getId());
				IRI justel = F.createIRI(doc.getJustel().toString());
				
				m.add(id, RDF.TYPE, ELI.LEGAL_RESOURCE);
				m.add(id, ELI.IS_REALISED_BY, justel);
				m.add(justel, RDF.TYPE, ELI.LEGAL_EXPRESSION);
				m.add(justel, ELI.REALISES, id);
				m.add(justel, ELI.LANGUAGE, LANGS.get(doc.getLang()));
				m.add(justel, ELI.TITLE, F.createLiteral(doc.getTitle()));
				m.add(justel, ELI.DATE_DOCUMENT, toDate(doc.getDocDate()));
				m.add(justel, ELI.DATE_PUBLICATION, toDate(doc.getPubDate()));
			}
			Rio.write(m, w, RDFFormat.NTRIPLES);
		}
	}

}
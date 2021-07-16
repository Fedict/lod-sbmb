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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

/**
 * (Part of) ELI vocabulary
 * 
 * @author Bart.Hanssens
 */
public class ELI {
	public static final String NAMESPACE = "http://data.europa.eu/eli/ontology#";
	public static final String PREFIX = "eli";
	
	public static final Namespace NS = new SimpleNamespace(PREFIX, NAMESPACE);

	public static final IRI DATE_DOCUMENT;
	public static final IRI DATE_PUBLICATION;
	public static final IRI ID_LOCAL;
	public static final IRI EMBODIES;
	public static final IRI FORMAT_PROP;
	public static final IRI IS_EMBODIED_BY;
	public static final IRI IS_REALIZED_BY;
	public static final IRI LANGUAGE;
	public static final IRI LEGALVALUE;
	public static final IRI PUBLISHER;
	public static final IRI PUBLISHER_AGENT;
	public static final IRI REALIZES;
	public static final IRI RESPONSIBILITY_OF;
	public static final IRI TITLE;
	public static final IRI TYPE_DOCUMENT;

	public static final IRI FORMAT;
	public static final IRI LEGAL_EXPRESSION;
	public static final IRI LEGAL_RESOURCE;
	public static final IRI LEGALVALUE_OFFICIAL;
	
	static {
		final ValueFactory F = SimpleValueFactory.getInstance();
		
		DATE_DOCUMENT = F.createIRI(NAMESPACE, "date_document");
		DATE_PUBLICATION = F.createIRI(NAMESPACE, "date_publication");
		ID_LOCAL = F.createIRI(NAMESPACE, "id_local");
		EMBODIES = F.createIRI(NAMESPACE, "embodies");
		FORMAT_PROP = F.createIRI(NAMESPACE, "format");
		IS_EMBODIED_BY = F.createIRI(NAMESPACE, "is_embodied_by");
		IS_REALIZED_BY = F.createIRI(NAMESPACE, "is_realized_by");
		LANGUAGE = F.createIRI(NAMESPACE, "language");
		LEGALVALUE = F.createIRI(NAMESPACE, "legal_value");
		PUBLISHER = F.createIRI(NAMESPACE, "publisher");
		PUBLISHER_AGENT = F.createIRI(NAMESPACE, "publisher_agent");
		REALIZES = F.createIRI(NAMESPACE, "realizes");
		RESPONSIBILITY_OF = F.createIRI(NAMESPACE, "responsibility_of");
		TITLE = F.createIRI(NAMESPACE, "title");
		TYPE_DOCUMENT = F.createIRI(NAMESPACE, "type_document");
		
		FORMAT = F.createIRI(NAMESPACE, "Format");
		LEGAL_EXPRESSION = F.createIRI(NAMESPACE, "LegalExpression");		
		LEGAL_RESOURCE = F.createIRI(NAMESPACE, "LegalResource");
		LEGALVALUE_OFFICIAL = F.createIRI(NAMESPACE, "LegalValue-official");
	}
	
}

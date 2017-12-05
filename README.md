# LOD tools for Belgian Staatsblad / Moniteur belge

## Overview

Command-line tool for converting the list of titles of published legal resources 
(currently limited to primary legislation: laws, decrees, ordonnances) on 
[http://www.ejustice.just.fgov.be/eli](http://www.ejustice.just.fgov.be/eli) to linked data.

The titles and publication dates are collected from the ejustice website, stored in a local cache file,
and then converted into RDF and stored as N-Tripes files

## Requires

Java 8 JRE for running the tool (maven and Java 8 JDK for building)

## Usage

For example, this will get the titles of all laws published between start year (`-s`) 2014 and end-year (`-e`) 2017.
The base URL (`-b`) of the site is http://www.ejustice.just.fgov.be/eli, the Dutch document type (`-n`) is called `wet` 
and the French (`-f`) `loi`, and both will mapped to a document type (`-t`) `LAW`
Wait time (`-w`) between two requests will be a random amount of time between (10 and 2*10)

'''
java -jar lod-sbmb.jar -g -s 2014 -e 2017 -n wet -f loi -w 10 -b http://www.ejustice.just.fgov.be/eli -t LAW -o out\const
'''

## Ontology

See also the
[http://publications.europa.eu/mdr/eli/index.html](technical specifications page of the European Legislation Identifier)

Note that Belgium uses _2_ URIs / ELIs for the same eli:LegalResource (French and Dutch). This tools 'links' them using owl:sameAs.

## Libraries

Uses the [http://rdf4j.org](RDF4j) and [http://www.mapdb.org/](MapDB) open source libraries.





# SB/MB linked data


> Converting titles of the Belgian Official Journal

---

## What are ELI's ?

- [European Legislation Identifier](http://eur-lex.europa.eu/eli-register/about.html)
- Country-specific "template" for legislation
  - Laws, decrees...
- Unique identifiers + [RDF ontology](http://publications.europa.eu/mdr/eli/index.html)

@fa[exclamation-triangle] Belgium issues both a NL and FR URI

---

## ELI ontology

URIs for core concepts:

- LegalResource: the legal "thing"
- LegalExpression: specific version and/or language
- Format: paper, XML, PDF.. edition of Expression

+++

## Additional info


- ELI [Implementation guide](https://publications.europa.eu/documents/2050822/2138819/ELI+-+A+Technical+Implementation+Guide.pdf)
- SB/MB [help on ELI](http://www.ejustice.just.fgov.be/eli)

---

## Example

"Wet tot invoering van een aftrek voor innovatie-inkomsten"
```
http://www.ejustice.just.fgov.be/eli/wet/2017/02/09/2017029171
```

---

## Technology

- Java open source
    - [RDF4J](http://rdf4j.org/), [JSoup](https://jsoup.org/)
- Small bash scripts and cron job

---

## How it works

- SB/MB publishes daily lists of titles
- Scraper tool converts them to RDF files
- Script sends zipped files to upload tool
- Data is loaded into OntoText GraphDB

---

## Limitation

- Legislation itself is not linked data
  - Requires more processing or change legacy system


---

## Thank you

Questions ? 

@fa[twitter] @BartHanssens

@fa[envelope] [opendata@belgium.be](mailto:opendata@belgium.be)
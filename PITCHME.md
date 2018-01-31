# Staatsblad/Moniteur Belge linked data


> Converting titles of the Belgian Official Journal

---

## What are ELI's ?

- [European Legislation Identifier](http://eur-lex.europa.eu/eli-register/about.html)
- Country-specific "template" for legislation
  - Laws, decrees...
- Unique identifiers + [RDF ontology](http://publications.europa.eu/mdr/eli/index.html)

@fa[exclamation-triangle] Belgium issues a NL and FR URI

---

## ELI ontology

URIs for core concepts:

- LegalResource
- LegalExpression
- Format

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

## Thank you

Questions ? 

@fa[twitter] @BartHanssens

@fa[envelope] [opendata@belgium.be](mailto:opendata@belgium.be)
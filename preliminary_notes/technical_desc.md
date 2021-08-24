---
bibliography: ref.bib
csl: apa.csl
---


<!--
 pandoc technical_desc.md --citeproc -M date="`date "+%B %e, %Y"`" -o technical_desc.pdf
-->

Constructing our own data warehouse will be critical to the success of our project.
We will develop our own Extract, Transform, Load (ETL) procedure [@9780764567575] to stream the flow of data directly into a carefully designed relational database.

The extraction will require to download the available datasets ([RePORT](https://exporter.nih.gov/), [NSF award](https://www.nsf.gov/awardsearch/download.jsp), [PubMed Database](https://ncbiinsights.ncbi.nlm.nih.gov/2017/06/22/pubmed-available-for-download-without-license/), [UMETRICS](https://iris.isr.umich.edu/research-data/), [Clinicaltrials.gov](https://clinicaltrials.gov/), [STATT](https://autm.net/surveys-and-tools/databases/statt)), and to convert them from `XML` or `CSV` to tables in a relational database (`SQL`).
High-quality extraction and conversion will be performed using state-of-the-art [MySQL's `XML` functions](https://dev.mysql.com/doc/refman/8.0/en/xml-functions.html), [Microsoft's `XML` Bulk Load](https://docs.microsoft.com/en-us/sql/relational-databases/sqlxml-annotated-xsd-schemas-xpath-queries/bulk-load-xml/performing-bulk-load-of-xml-data-sqlxml-4-0?view=sql-server-2017) or pureXML's built-in shredding capacities [@9780138150471; @Lee02effectiveschema; @DBLP:books/ios/omelayenkoK03/LeeMC03].
Each dataset comes with a precise, documented schema, that will be leveraged, if needed using genetic algorithms [@10.1007/978-3-540-30134-9_33], manual annotations [@Flexible], or semantics constraints [@LV2006245] to insure the best possible quality of the extracted data.

The transformation will mainly consist in identifying records, or data sets, representing the same real-world entity (e.g., author, lab, university).
This extremely well-known problem, called record linkage, entity resolution or matching, is extensively studied [@Winkler99thestate;@Winkler06overviewof;@4016511], but numerous challenges remain.
For instance, the absence of a best match algorithm in all generality [@KOPCKE2010197] will require from us to experiment, to get an effective and efficient combination of different techniques.
Preliminary study of the datasets suggests that:

- "Blocking strategies" [@WangPei; @Christen; @10.1007/11546849_21] will be needed to reduce the search space.
- Since direct identifiers are available and of good quality, deterministic methods should have good results [@Dusetzina2014; @Howe2006;@On2014], and efficient matching algorithm [@Benjelloun2009] should give good results.
- Overlaps and pre-existing linkages in the datasets will give us "hints" that can be exploited to find matching records [@6155721].
- We will be able to use linkage at multiple levels, and to benefits from the technologies of group linkage [@DBLP:conf/icde/OnKLS07].

Even if no privacy issues are foreseen, should they arise, then methods to link datasets without disclosing sensitive information will be used [@Kum; @Karapiperis; @238281; @DBLP:conf/icde/VidanageRCS19;@VATSALAN2013946].
The latest development in benchmarking will be used to asses the quality of the linkage [@FERRANTE2012165; @Hand2018; @KOPCKE2010197], but our ultimate unit of measure will be our goal [@AdilTengku].

Indeed, the linkage will be directed toward the filling of a carefully crafted entity–relationship model that will be designed by all the team members.
The "evidence" entities (publications, awards, etc.) will be in relationship with our "producer" entities (PIs, projects, etc.) through numerous relationships that will allow us to model a large variety of situations.

![](fig/Fig.pdf)

Loading the extracted, transformed data will require to preserve backtracking possibilities: the origin of the data and the reason why entities were matched needs to be precisely documented, so that corrections can be applied when new datasets or better matching algorithms become available.
Weekly and monthly updates of the datasets will be propagated to our warehouse using similar or improved techniques, and will enrich our basis for analysis with almost real-time information.
Cohorts of principal investigators will then be extracted and updated from this data warehouse using subgraph patterns [@6816710] and techniques to identify cliques in large networks [@CONTE2019104464; @Rossi:2014:FMC:2567948.2577283]

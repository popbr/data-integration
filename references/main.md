---
documentclass: scrartcl
papersize: letter
fontsize: 11pt
geometry: "margin=1truein, truedimen, twoside=false, ignoreall"
link-citations: true
bibliography: citations.bib
csl: templates/theoretical-computer-science.csl
lang: en
---
<!-- Fill the information below as you see fit, and add it to the header above, between the "---". -->
<!--
author: 
institute: Augusta University
keywords:
- 
- 
title: 'Title'
subtitle: |
    | A subtitle
    | on multiple lines.
-->


<!--
Normally, you can compile this file into a pdf using

pandoc --pdf-engine=xelatex --citeproc -o main.pdf main.md
-->

# Workflow

#. Find reference / article,
#. Add it to citations.bib, (to obtain citation from webpages, you can use <https://zbib.org/>, look for "export" -> "download bibtex", and more generally, refer to <https://tex.stackexchange.com/questions/143/what-are-good-sites-to-find-citations-in-bibtex-format>),
#. Reference it below and add a few notes about that article.

# Example

From [@noauthor_understanding_2020], I have learned that…

# References 

::: {#refs}
:::

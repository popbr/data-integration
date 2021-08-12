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

From [@noauthor_understanding_2020], I learned about the "data analysis project lifecycle",  which is the timeline a DAP (data analysis project) follows. There are 6 steps

From [@university_404_nodate], I found a wealth of projects in data analytics that I can peruse and get a better foundation of the ways my project/paper can be done. For example, the "Learning to skim text" project was one of my favorites, was one about teaching an AI to be able to skim through text to be able to better aggregate data for data aquisition.

From [@bharadwa_7_2021], I learned of the "7 steps to a sucessful data science project". From step 1, the problem statement, I believe that my problem statement will be something along the lines of "Is it possible to measure the effectiveness of a researcher by finding and examining papers/research and the amount of usage it see". This will likely be changed after August 12th.


# References 

::: {#refs}
:::

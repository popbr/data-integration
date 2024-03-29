# *Data Integration for the Study of Outstanding Productivity in Biomedical Research*

This repository serves to share two programs:

- [Installation_Test](Installation_Test) helps in assessing whenever your set-up is correctly configured to run our main program,
- [Project/Database-IO](Project/Database-IO) is our main program, whose purpose is to process a variety of data sources (`xml`, `cvs`, `json`, …), insert relevant attributes into an SQL database, and then output the resulting data into an `xml` file.

## A repo for the Honors Thesis/Prospectus Project

This project is authored by Dr. [Clément Aubert](https://spots.augusta.edu/caubert/) and Noah Sleeper for the use in Mr. Sleeper's Honors Thesis at *[Augusta University](https://www.augusta.edu/)*. It is intended to work with Dr. [Andrew Balas](https://www.augusta.edu/faculty/directory/view.php?id=ebalas)'s research at Augusta University in looking at ways to generate qualitative measures of academic researchers/instutions/entities.

This project may be downloaded for the purposes of academic research or inquery, but please note that this project is only a tool for the collection and examination of databases and the data within said databases. This project and its authors do not claim ownership over any files downloaded or any data handled by the project, nor do they claim 100% accuracy in conclusions drawn from any results user may recieve.

This project relies on the use of different [technologies](#technologies) to function. All use of these programs/entities falls under fair use.

## Data Integration for the Study of Outstanding Productivity in Biomedical Research

The purpose, motivation and challenges behind this project are presented in

C. Aubert, A. Balas, T. Townsend, N. Sleeper, C.J. Tran, _Data integration for the study of outstanding productivity in biomedical research_, in: Proceedings of the 15th International Conference on [Current Research Information Systems (Cris2022)](https://cris2022.srce.hr/), Procedia Computer Science, Volume 211, 2022, pp. 196-200. <http://hdl.handle.net/11366/1987>, <https://doi.org/10.1016/j.procs.2022.10.191>.

## Technologies

- Microsoft Excel, 
- Maven, 
- Apache IO,
- MySQL Server 8.0
- mySQL Workbench 8.0

For a full list of dependencies, please have a look at the [POM.xml](Project/Database-IO/pom.xml) file.


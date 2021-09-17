DROP SCHEMA IF EXISTS Prospectus_DB;
CREATE SCHEMA Prospectus_DB;
USE Prospectus_DB;

CREATE TABLE CompanyDB (
  Name VARCHAR(255) PRIMARY KEY,
  Website VARCHAR(255) CHECK (Website LIKE "https://%")
);

CREATE TABLE Project (
  IC_Name VARCHAR(255) PRIMARY KEY,
  ORG_NAME VARCHAR(255),
  PROJECT_TITLE VARCHAR(500)
);

-- CREATE TABLE VACCINE (
  -- Name VARCHAR(50) PRIMARY KEY,
  -- Manufacturer VARCHAR(50) NOT NULL,
  -- FOREIGN KEY (Manufacturer) REFERENCES COMPANY (NAME) ON
    -- UPDATE CASCADE
-- );

-- CREATE TABLE EFFICACY (
  -- DiseaseName VARCHAR(50),
  -- VaccineName VARCHAR(50),
  -- Efficacy DECIMAl(5, 2),
  -- PRIMARY KEY (DiseaseName, VaccineName),
  -- FOREIGN KEY (DiseaseName) REFERENCES DISEASE (NAME),
  -- FOREIGN KEY (VaccineName) REFERENCES VACCINE (NAME)
-- );

INSERT INTO CompanyDB
VALUES (
  "NSF Award Search and Download NSF",
  "https://www.nsf.gov/awardsearch/download.jsp");

INSERT INTO Project
VALUES (
  "EUNICE KENNEDY SHRIVER NATIONAL INSTITUTE OF CHILD HEALTH & HUMAN DEVELOPMENT",
  "RESEARCH INST NATIONWIDE CHILDREN'S HOSP",
  "Longitudinal Assessment of Driving After Mild TBI in Teens");
  
  SELECT * FROM CompanyDB;
On Dr. Aubert's set-up, this gives

```
javac -cp .:poi-5.0.0/poi-5.0.0.jar:poi-5.0.0/poi-examples-5.0.0.jar:poi-5.0.0/poi-excelant-5.0.0.jar:poi-5.0.0/poi-integration-5.0.0.jar:poi-5.0.0/poi-ooxml-5.0.0.jar:poi-5.0.0/poi-ooxml-full-5.0.0.jar:poi-5.0.0/poi-ooxml-lite-5.0.0.jar:poi-5.0.0/poi-scratchpad-5.0.0.jar:poi-5.0.0/lib/commons-codec-1.15.jar:poi-5.0.0/lib/commons-collections4-4.4.jar:poi-5.0.0/lib/commons-math3-3.6.1.jar:poi-5.0.0/lib/SparseBitSet-1.2.jar:poi-5.0.0/ooxml-lib/commons-compress-1.20.jar:poi-5.0.0/ooxml-lib/curvesapi-1.06.jar:poi-5.0.0/ooxml-lib/xmlbeans-4.0.0.jar  Prospectus_Write_to_Excel.java 
```

```
java -cp .:mysql-connector-java-8.0.26.jar:poi-5.0.0/poi-5.0.0.jar:poi-5.0.0/poi-examples-5.0.0.jar:poi-5.0.0/poi-excelant-5.0.0.jar:poi-5.0.0/poi-integration-5.0.0.jar:poi-5.0.0/poi-ooxml-5.0.0.jar:poi-5.0.0/poi-ooxml-full-5.0.0.jar:poi-5.0.0/poi-ooxml-lite-5.0.0.jar:poi-5.0.0/poi-scratchpad-5.0.0.jar:poi-5.0.0/lib/commons-codec-1.15.jar:poi-5.0.0/lib/commons-collections4-4.4.jar:poi-5.0.0/lib/commons-math3-3.6.1.jar:poi-5.0.0/lib/SparseBitSet-1.2.jar:poi-5.0.0/ooxml-lib/commons-compress-1.20.jar:poi-5.0.0/ooxml-lib/curvesapi-1.06.jar:poi-5.0.0/ooxml-lib/xmlbeans-4.0.0.jar  Prospectus_Write_to_Excel
```


For connecting to SQL databases, one must add a driver to Eclipse. This can be done by:
1. Right-Click on the project from inside Eclipse.
2. There, you will go: "Build Path" --> "Configure Build Path"
3. Click on "Classpath", then "Add External JARS"
4. Go to where your SQL-connector-java-XXXX-.jar is located, and add that.
5. Apply the changes, close, then you should be good to run
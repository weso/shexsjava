shexjava
========

ShEx client using Java

This repository contains a simple command line Java program that runs the ShEx validator. 

# Create a standalone Jar

```
mvn clean compile package
```

# Validating example

The following line validates a turtle file against a ShEx schema

```
java -jar target/shexjava.jar -d examples/issue.ttl -s examples/issue.shex
```

It runs ShEx and validates the nodes that have been declared to belong to a Shape (sh:scopeNode declaration).

The output is something like:

```
Valid. Result: 1 Results
Solution  1: 
 { <http://example.org/x> -> +<http://example.org/IssueShape> | 
   <http://example.org/john> -> +<http://example.org/UserShape> | 
 }
```





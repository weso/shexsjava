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


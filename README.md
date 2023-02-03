shexs-java
========

Examples of a simple program that runs ShEx-s in Java

This repository contains a simple command line Java program that runs the [ShEx-s](https://github.com/weso/shex-s/) library.

The code uses [Maven](https://maven.apache.org/) to manage the project. 

# Run the tests

```
mvn test
``` 



# Create a standalone Jar

```
mvn clean compile package
```

# Validating example

The following line validates a turtle file against a ShEx schema using a shape map. 

```sh
java -jar target/shexsjava-1.0-shaded.jar -s examples/issue.shex -d examples/issue.ttl -m examples/issue.shapeMap
```

It runs ShEx and validates the nodes according to the shapeMap.

The output is a result shape map (in Json format):

```json
Result:[
  {
    "node" : "<http://example.org/x>",
    "shape" : "<http://example.org/IssueShape>",
    "status" : "conformant",
    "appInfo" : "Shaclex",
    "reason" : ":unassigned == :unassigned"
  },
  {
    "node" : "<http://example.org/john>",
    "shape" : "<http://example.org/UserShape>",
    "status" : "conformant",
    "appInfo" : "Shaclex",
    "reason" : "John Smith has datatype xsd:string\n<mailto:john@example.org> is an IRI"
  }
]
```

It is also possible to validate data in other formats, like JSON-LD:

```
java -jar target/shexsjava-1.0-shaded.jar -d examples/wot.lsonld -df JSON-LD -s examples/wot.shex -m examples/wot.shapeMap
```

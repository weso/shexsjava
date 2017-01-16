package es.weso.shexjava;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import es.weso.schema.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.rdf.model.Model;
import scala.collection.JavaConverters;
import scala.collection.JavaConverters.*;

import scala.Option;


import es.weso.rdf.RDFReader;
import es.weso.rdf.jena.RDFAsJenaModel;
import scala.util.Try;

public class Validator {
	
	Logger log = Logger.getLogger(Validator.class.getName());

	// Create a none, see: http://stackoverflow.com/questions/1997433/how-to-use-scala-none-from-java-code
	Option<String> none = Option.apply(null);

	public void validate(Model dataModel, Schema schema) throws Exception {
		Option<String> none = Option.apply(null); // Create a none
		RDFReader rdf = new RDFAsJenaModel(dataModel);
		Result result = schema.validate(rdf,"TARGETDECLS",none,none, rdf.getPrefixMap(), schema.pm());
		if (result.isValid()) {
			log.info("Result is valid");
			System.out.println("Valid. Result: " + result.show());
			List<Solution> solutions = JavaConverters.seqAsJavaList(result.solutions());
			solutions.forEach((solution) ->
					System.out.println(solution.show())
			);
		} else {
			System.out.println("Not valid");
			List<ErrorInfo> errors = JavaConverters.seqAsJavaList(result.errors());
			errors.forEach((e) ->
					System.out.println(e.show())
			);
		}
	} 

	public void validate(String dataFile, String schemaFile, String schemaFormat, String processor) throws Exception {
		log.info("Reading data file " + dataFile);
		Model dataModel = RDFDataMgr.loadModel(dataFile);
//		Model dataModel =  RDFDataMgr.loadModel(dataFile);
		log.info("Model read. Size = " + dataModel.size());
		
		
		log.info("Reading shapes file " + schemaFile + " with format " + schemaFormat);
		Schema schema = readSchema(schemaFile,schemaFormat, processor);
		
		log.info("Schema read" + schema.serialize(schemaFormat));

		validate(dataModel,schema);
	}
	
	public Schema readSchema(String schemaFile, String format, String processor) throws Exception {
		String contents = new String(Files.readAllBytes(Paths.get(schemaFile)));
		return Schemas.fromString(contents,format,processor,none).get();
	}

}
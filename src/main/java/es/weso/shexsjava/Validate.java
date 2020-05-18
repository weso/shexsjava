package es.weso.shexsjava;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import cats.effect.IO;
import es.weso.rdf.RDFReader;
import es.weso.rdf.jena.RDFAsJenaModel;
import es.weso.rdf.nodes.IRI;
import es.weso.shapeMaps.ResultShapeMap;
import es.weso.shapeMaps.ShapeMap;
import es.weso.shex.ResolvedSchema;
import es.weso.shex.Schema;
import es.weso.shex.validator.Validator;
import es.weso.utils.eitherios.EitherIOUtils;
import scala.Option;

public class Validate {

    Logger log = Logger.getLogger(Validate.class.getName());

    // none object is required to pass no base
    Option<IRI> none = Option.empty();
    Option<RDFReader> noneRDF = Option.empty();

    /**
     * This method performs the validation given data files paths and 
     * returns the result as a ShapeMap.
     * 
     * @param dataFile 			RDF data file path
     * @param dataFormat		RDF format
     * @param schemaFile		Schema file path
     * @param schemaFormat		Schema format
     * @param shapeMapFile		ShapeMap file path
     * @param shapeMapFormat 	ShapeMap format
     * @return ResultShapeMap 	Validation result as a ShapeMap
     */
    public IO<ResultShapeMap> validate(String dataFile,
            String dataFormat,
            String schemaFile,
            String schemaFormat,
            String shapeMapFile,
            String shapeMapFormat) {

			return readRDF(dataFile, dataFormat).flatMap(rdf ->
			getContents(schemaFile).flatMap(schemaStr ->
			Schema.fromString(schemaStr,schemaFormat,none, noneRDF).flatMap(schema ->
			getContents(shapeMapFile).flatMap(shapeMapStr ->
			EitherIOUtils.eitherStr2IO(ShapeMap.fromString(shapeMapStr.toString(),
			       shapeMapFormat,
			       none,
			       rdf.getPrefixMap(),
			       schema.prefixMap())).flatMap(shapeMap ->
			ShapeMap.fixShapeMap(shapeMap,
			       rdf,
			       rdf.getPrefixMap(),
			       schema.prefixMap()).flatMap(fixedShapeMap ->
			ResolvedSchema.resolve(schema,none).flatMap(resolvedSchema ->
			Validator.validate(resolvedSchema,fixedShapeMap,rdf).flatMap(result ->
			result.toResultShapeMap()
			))))))));
	}
    
    /**
     * This method performs the validation given data files paths and the expected output.
     * Returns the result as a ResultValidation which  represents an object
     * with the validation result shapeMap and the expected result shapeMap. 
     * Unlike the previous one, this method use an ontology for the validation. 
     * Also this method doesn´t support different formats for the inputs.
     * 
     * @param dataFile		RDF data file path (The data must be written in Turtle syntax)
     * @param ontologyFile	Ontology file paht (The data must be written in Turtle syntax)
     * @param schemaFile	Schema file path   (The data must be written in ShEx syntax)
     * @param shapeMapFile	ShapeMap file path (The data must be written in ShapeMap syntax)
     * @return ResultValidation (result ShapeMap and expected ShapeMap)
     */
    public IO<ResultValidation> validate(String dataFile,
            String ontologyFile,
            String schemaFile,
            String shapeMapFile,
            String expectedShapeMapFile) {
    	
		return readRDF(dataFile, "TURTLE").flatMap(rdfData ->
		readRDF(ontologyFile, "TURTLE").flatMap(ontologyData ->
		rdfData.merge(ontologyData).flatMap(merged ->
		getContents(schemaFile).flatMap(schemaStr ->
		Schema.fromString(schemaStr,"SHEXC",none, noneRDF).flatMap(schema ->
		getShapeMap(shapeMapFile, merged, schema).flatMap(shapeMap ->
		ShapeMap.fixShapeMap(shapeMap,merged,merged.getPrefixMap(),schema.prefixMap()).flatMap(fixedShapeMap ->
		ResolvedSchema.resolve(schema,none).flatMap(resolvedSchema ->
		getShapeMap(expectedShapeMapFile, merged, schema).flatMap(expectedShapeMap ->
		Validator.validate(resolvedSchema,fixedShapeMap,merged).flatMap(result ->
		result.toResultShapeMap().flatMap(resultShapeMap ->
		IO.pure(new ResultValidation(resultShapeMap,expectedShapeMap)))
		))))))))));
	}
    
    /**
     * This method performs the validation given the data as a String.
     * Uses an ontology for the validation. Pass this parameter as an 
     * empty string in order not to use an ontology. This method doesn´t 
     * support different formats for the inputs.
     * Returns the result as a ShapeMap.
     * 
     * @param dataStr		  RDF data in Turtle syntax
     * @param ontologyStr	  Ontology in Turtle syntax (or empty string to not use an ontology)
     * @param schemaStr		  Schema in ShEx syntax
     * @param shapeMapStr	  ShapeMap in ShapeMap syntax
     * @return ResultShapeMap Validation result as a ShapeMap
     */
    public IO<ResultShapeMap> validateStr(String dataStr,
                                         String ontologyStr,
                                         String schemaStr,
                                         String shapeMapStr) {
        return readRDFStr(dataStr, "TURTLE").flatMap(rdfData ->
               readRDFStr(ontologyStr, "TURTLE").flatMap(ontologyData ->
               rdfData.merge(ontologyData).flatMap(merged ->
               Schema.fromString(schemaStr,"SHEXC",none, noneRDF).flatMap(schema ->
               EitherIOUtils.eitherStr2IO(ShapeMap.fromString(shapeMapStr,"Compact", none,merged.getPrefixMap(),schema.prefixMap())).flatMap(shapeMap ->
               ShapeMap.fixShapeMap(shapeMap,merged,merged.getPrefixMap(),schema.prefixMap()).flatMap(fixedShapeMap ->
               ResolvedSchema.resolve(schema,none).flatMap(resolvedSchema ->
               Validator.validate(resolvedSchema,fixedShapeMap,merged).flatMap(result ->
               result.toResultShapeMap().flatMap(resultShapeMap ->
                IO.pure(resultShapeMap)
               )))))))));
    }

    public IO<ShapeMap> getShapeMap(String shapeMapFile, RDFReader rdf, Schema schema) {
        return getContents(shapeMapFile).flatMap(shapeMapStr ->
               EitherIOUtils.eitherStr2IO(ShapeMap.fromString(shapeMapStr,"Compact",none,rdf.getPrefixMap(),schema.prefixMap()))
        ).handleErrorWith(e -> IO.raiseError(new RuntimeException("Cannot parse shapeMap from file: " + shapeMapFile + ": " + e.getMessage())));
    }

    public IO<RDFAsJenaModel> readRDF(String fileName, String format) {
        // log.info("Reading data file " + fileName);
        return RDFAsJenaModel.fromFile(Paths.get(fileName).toFile(), format, none).handleErrorWith(e ->
                IO.raiseError(new RuntimeException("Cannot parse RDF from file: " + fileName + ":" + e.getMessage()))
        );
    }

    public IO<RDFAsJenaModel> readRDFStr(String str, String format) {
        return RDFAsJenaModel.fromChars(str, format, none).handleErrorWith(e ->
                IO.raiseError(new RuntimeException("Cannot parse RDF from str: " + str  + ":" + e.getMessage()))
        );
    }

    public IO<String> getContents(String fileName) {
        try {
            Path path = Paths.get(fileName);
            List<String> lines = Files.readAllLines(path);
            String str = String.join("\n", lines);
            return IO.apply(() -> str);
        } catch (IOException e) {
            return IO.raiseError(e);
        }
    }

}

    


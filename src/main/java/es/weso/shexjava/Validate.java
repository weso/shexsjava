package es.weso.shexjava;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import es.weso.shapeMaps.FixedShapeMap;
import es.weso.shapeMaps.ResultShapeMap;
import es.weso.shapeMaps.ShapeMap;
import es.weso.shex.Schema;
import es.weso.shex.validator.Validator;
import es.weso.utils.FileUtils;
import io.circe.Json;
import org.apache.jena.rdf.model.Model;
import scala.collection.JavaConverters;
import scala.Option;

import es.weso.rdf.jena.RDFAsJenaModel;
import scala.util.Either;
import scala.util.Left;
import scala.util.Right;

public class Validate {

    Logger log = Logger.getLogger(Validate.class.getName());

    // none object is required to pass no base
    Option<String> base = Option.empty();

    public Either<String,Result> validate(String dataFile,
                         String dataFormat,
                         String schemaFile,
                         String schemaFormat,
                         String shapeMapFile,
                         String shapeMapFormat) {
     return readRDF(dataFile, dataFormat).flatMap(rdf ->
            FileUtils.getContents(schemaFile).flatMap(schemaStr ->
            Schema.fromString(schemaStr,schemaFormat,base).flatMap(schema ->
            FileUtils.getContents(shapeMapFile).flatMap(shapeMapStr ->
            ShapeMap.fromString(shapeMapStr.toString(),
                    shapeMapFormat,
                    base,
                    rdf.getPrefixMap(),
                    schema.prefixMap()).flatMap(shapeMap ->
            ShapeMap.fixShapeMap(shapeMap,
                    rdf,
                    rdf.getPrefixMap(),
                    schema.prefixMap()).flatMap(fixedShapeMap ->
            Validator.validate(schema,
                    fixedShapeMap,
                    rdf).flatMap(resultShapeMap ->
              new Right<String,Result>(new Result(schema, rdf, shapeMap, resultShapeMap))
            )))))));
    }

    public Either<String, RDFAsJenaModel> readRDF(String fileName, String format) {
        try {
            log.info("Reading data file " + fileName);
            File file = Paths.get(fileName).toFile();
            return RDFAsJenaModel.fromFile(file, format, base);
        } catch (Exception e) {
            return new Left<String,RDFAsJenaModel>("Error reading file " + fileName + ":" + e.getMessage());
        }
    }

}
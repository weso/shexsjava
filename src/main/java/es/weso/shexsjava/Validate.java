package es.weso.shexsjava;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import es.weso.rdf.RDFReader;
import es.weso.rdf.nodes.IRI;
import es.weso.shapeMaps.ResultShapeMap;
import es.weso.shapeMaps.ShapeMap;
import es.weso.shex.ResolvedSchema;
import es.weso.shex.Schema;
import es.weso.shex.validator.Validator;
import scala.Option;

import es.weso.rdf.jena.RDFAsJenaModel;
import cats.effect.IO;
import es.weso.utils.eitherios.*;

public class Validate {

    Logger log = Logger.getLogger(Validate.class.getName());

    // none object is required to pass no base
    Option<IRI> none = Option.empty();
    Option<RDFReader> noneRDF = Option.empty();

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

    public IO<RDFAsJenaModel> readRDF(String fileName, String format) {
            log.info("Reading data file " + fileName);
            File file = Paths.get(fileName).toFile();
            return RDFAsJenaModel.fromFile(file, format, none);
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
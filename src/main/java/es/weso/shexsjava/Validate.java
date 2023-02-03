package es.weso.shexsjava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import es.weso.shapemaps.ResultShapeMap;
import es.weso.shexsjena.ShExsJenaValidator;
import es.weso.shexsjena.ShExsJenaValidatorBuilder;

public class Validate {

    Logger log = Logger.getLogger(Validate.class.getName());

    ResultShapeMap validate(String dataFile,
                            String dataFormat,
                            String schemaFile,
                            String schemaFormat,
                            String shapeMapFile,
                            String shapeMapFormat) throws IOException, FileNotFoundException {
      FileInputStream is = new FileInputStream(schemaFile);            
      ShExsJenaValidator validator = 
         ShExsJenaValidatorBuilder.fromInputStreamSync(is,"ShExC");
      
      Model model = ModelFactory.createDefaultModel().read(dataFile, dataFormat);
      String shapeMapStr = Files.readString(Path.of(shapeMapFile));

      ResultShapeMap result = validator.validateShapeMapSync(model, shapeMapStr);
      return result;   
    }
}

    


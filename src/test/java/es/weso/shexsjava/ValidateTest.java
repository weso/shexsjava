package es.weso.shexsjava;

import cats.data.EitherT;
import cats.effect.IO;
import es.weso.rdf.nodes.IRI;
import es.weso.schema.Result;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Assert;
import org.junit.Test;

import es.weso.rdf.PrefixMap;
import es.weso.rdf.jena.RDFAsJenaModel;
import es.weso.schema.Schema;
import es.weso.schema.Schemas;
import es.weso.shapeMaps.ShapeMap;
import scala.Option;
import scala.collection.immutable.Map;
import scala.util.Either;
import scala.util.Try;
import es.weso.utils.IOUtils;

public class ValidateTest {


	private static final String SHACLEX = "SHACLex";
	private static final Option<IRI> NONE_Iri = Option.apply(null);
    private static final Option<String> NONE = Option.apply(null);
    private static final String NOShapeMap = "";
    private static final String TRIGGER_MODE = "TargetDecls";
    
	private es.weso.schema.Result validate(Model rdfModel, Model shaclModel) {
  	        RDFAsJenaModel rdf = RDFAsJenaModel.apply(rdfModel,NONE_Iri,NONE_Iri);
            PrefixMap nodeMap = rdf.getPrefixMap();
            RDFAsJenaModel shacl = RDFAsJenaModel.apply(shaclModel,NONE_Iri,NONE_Iri);
            return Schemas.fromRDFIO(shacl, SHACLEX).flatMap(schema ->
                   schema.validate(rdf,TRIGGER_MODE,NOShapeMap,NONE, NONE,nodeMap,schema.pm())).unsafeRunSync();
    }
	
    @Test
    public void ValidateSimple() {
        try {
        	Model rdfModel = ModelFactory.createDefaultModel();
        	rdfModel.read("data1.ttl");
        	Model shaclModel = ModelFactory.createDefaultModel();
        	shaclModel.read("schema1.ttl");
        	es.weso.schema.Result result = validate(rdfModel,shaclModel);
        	Assert.assertEquals(result.isValid(),true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception thrown in validateSimple");
        }
    }
    
    @Test
    public void ValidateSimpleBad() {
        try {
        	Model rdfModel = ModelFactory.createDefaultModel();
        	rdfModel.read("data2.ttl");
        	Model shaclModel = ModelFactory.createDefaultModel();
        	shaclModel.read("schema1.ttl");
        	es.weso.schema.Result result = validate(rdfModel,shaclModel);
        	Assert.assertEquals(result.isValid(),false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception thrown in validateSimple");
        }
    }
}
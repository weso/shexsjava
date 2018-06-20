package es.weso.shexjava;

import es.weso.rdf.rdf4j.RDF4jMapper;
import es.weso.shapeMaps.*;
import es.weso.shex.validator.Validator;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.Assert;
import org.junit.Test;
import es.weso.rdf.jena.RDFAsJenaModel;
import es.weso.rdf.rdf4j.RDFAsRDF4jModel;
import es.weso.shex.Schema;
import scala.Option;
import scala.util.Either;

public class ValidateRdf4j {
	private static final Option<String> none = Option.empty();
	private static final RDFAsJenaModel emptyRDF = RDFAsJenaModel.apply();
	private static final Status Conformant = Conformant$.MODULE$ ;
    private static final Status NonConformant = NonConformant$.MODULE$ ;
    private static final Status Undefined = Undefined$.MODULE$ ;


    @Test
    public void ValidateListStatements() {
        try {
            // The next lines are just taken from: http://docs.rdf4j.org/rdf-tutorial/#_example_01_building_a_simple_model
        	ValueFactory vf = SimpleValueFactory.getInstance();
        	String ex = "http://example.org/";
        	IRI picasso = vf.createIRI(ex, "Picasso");
        	IRI artist = vf.createIRI(ex, "Artist");
        	Model model = new TreeModel();
        	model.add(picasso, RDF.TYPE, artist);
        	model.add(picasso, FOAF.FIRST_NAME, vf.createLiteral("Pablo"));
        	RDFAsRDF4jModel rdf = RDFAsRDF4jModel.apply(model);

        	// Simple ShEx schema string
        	String strSchema = "prefix foaf: <http://xmlns.com/foaf/0.1/> "
        			+ "prefix : <http://example.org/>"
        			+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#> "
        			+ ":ArtistShape { "
        			+ "  foaf:name xsd:string "
        			+ "}" 
        			;

        	// Query map to validate picasso as artistShape
        	String strShapeMap = "<http://example.org/Picasso>@:ArtistShape" ;

        	// Validation
        	Either<String,ResultShapeMap> result = Schema.fromString(strSchema, "ShExC" , none,emptyRDF).flatMap(schema ->
			ShapeMap.fromString(strShapeMap, "Compact", none, rdf.getPrefixMap(), schema.prefixMap()).flatMap(queryMap ->
			ShapeMap.fixShapeMap(queryMap,rdf,rdf.getPrefixMap(),schema.prefixMap()).flatMap(shapeMap -> {
                        System.out.println("RDF: " + rdf.serialize("TURTLE"));
                        System.out.println("Schema: " + Schema.serialize(schema,"ShExC",emptyRDF));
                        System.out.println("ShapeMap" + shapeMap.serialize("Compact") );
                        return Validator.validate(schema, shapeMap, rdf);
                    }
            )));

        	// Some checks
            System.out.println("Result: " + result);
        	Assert.assertEquals(result.isRight(),true);
        	ResultShapeMap resultMap = result.right().get();

         	ShapeMapLabel artistLabel = IRILabel.apply(RDF4jMapper.iri2iri(vf.createIRI(ex, "ArtistShape")));
        	Assert.assertEquals(resultMap.getInfo(RDF4jMapper.iri2iri(picasso), artistLabel).status(),Conformant);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception thrown in validateSimple");
        }
    }
}
package es.weso.shexsjava;

public class ValidateRdf4j {
/*
    private static final Option<String> none = Option.empty();
    private static final RDFAsRDF4jModel emptyRDF = RDFAsRDF4jModel.apply();
    private static final Status Conformant = Conformant$.MODULE$;
    private static final Status NonConformant = NonConformant$.MODULE$;
    private static final Status Undefined = Undefined$.MODULE$;
    private static final ValueFactory vf = SimpleValueFactory.getInstance();


    ShapeMapLabel mkLabel(String str) {
        return IRILabel.apply(RDF4jMapper.iri2iri(vf.createIRI(str)));
    }

    RDFNode mkNode(String str) {
        return RDF4jMapper.iri2iri(vf.createIRI(str));
    }

    @Test
    public void ValidateListStatements() {
        try {
            // The next lines are just taken from: http://docs.rdf4j.org/rdf-tutorial/#_example_01_building_a_simple_model
            String ex = "http://example.org/";
            IRI picasso = vf.createIRI(ex, "Picasso");
            IRI artist = vf.createIRI(ex, "Artist");
            Model model = new TreeModel();
            model.setNamespace("", "http://example.org/");
            model.setNamespace("foaf", "http://xmlns.com/foaf/0.1/");
            model.add(picasso, RDF.TYPE, artist);
            model.add(picasso, FOAF.FIRST_NAME, vf.createLiteral("Pablo"));
            RDFAsRDF4jModel rdf = RDFAsRDF4jModel.apply(model);

            // Simple ShEx schema string
            String strSchema = String.join("\n",
                    "prefix foaf: <http://xmlns.com/foaf/0.1/>",
                    "prefix : <http://example.org/>",
                    "prefix xsd: <http://www.w3.org/2001/XMLSchema#>",
                    ":ArtistShape {",
                    "  a [:Artist] ;",
                    "  foaf:firstName xsd:string",
                    "}",
                    ":CarShape {",
                    " a [:Car] ",
                    "}");

            // Query map to validate picasso as artistShape
            String strShapeMap = ":Picasso@:ArtistShape,:Picasso@:CarShape";

            // Validation
            Either<String, ResultShapeMap> result =
                    Schema.fromString(strSchema, "ShExC", none, emptyRDF).flatMap(schema ->
                    ShapeMap.fromString(strShapeMap, "Compact", none, rdf.getPrefixMap(), schema.prefixMap()).flatMap(queryMap ->
                    ShapeMap.fixShapeMap(queryMap, rdf, rdf.getPrefixMap(), schema.prefixMap()).flatMap(shapeMap -> Validator.validate(schema, shapeMap, rdf)
             )));

            Assert.assertEquals(result.isRight(), true);
            ResultShapeMap resultMap = result.right().get();
            RDFNode picassoNode = mkNode(ex + "Picasso");
            Assert.assertEquals(Conformant, resultMap.getInfo(picassoNode, mkLabel(ex + "ArtistShape")).status());
            Assert.assertEquals(NonConformant, resultMap.getInfo(picassoNode, mkLabel(ex + "CarShape")).status());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception thrown in validateSimple");
        }
    } */
}
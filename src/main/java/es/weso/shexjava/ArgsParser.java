package es.weso.shexjava;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ArgsParser {

	public String schema = "";
	public String data = "";
	public String processor = "shex";
	public String schemaFormat = "SHEXC";
	public Boolean printTime = false;
	public Boolean verbose = false;

	private static final Logger log = Logger.getLogger(ArgsParser.class.getName());
	
	private Options options = new Options();

	private void createOptions() {
		Boolean WithArg = true;
		Boolean NoArg = false;
		
		
		Option schemaOpt = new Option("s", "schema", WithArg, "Schema file or URI");
		schemaOpt.setRequired(true);
		options.addOption(schemaOpt);

		Option dataOpt = new Option("d", "data", WithArg, "Data file or URI");
		dataOpt.setRequired(true);
		options.addOption(dataOpt);
		
		options.addOption("h", "help", NoArg,"show help.");
		options.addOption("v", "verbose", NoArg,"Verbose mode");
		options.addOption("p", "processor", WithArg, "Processor: (shex by default)");
		options.addOption("f", "schemaFormat", WithArg, "Schema Format: (SHEXC by default)");
		options.addOption("t", "time", NoArg,"Print processing time at the end");
	}
	
	public ArgsParser(String...args) {
		createOptions();
		try {
			parse(args);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			help(options);
		}
	}

	public void parse(String... args) {
		log.info("Parsing args " + args);
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption("h"))
				help(options);

			if (cmd.hasOption("s")) {
				schema = cmd.getOptionValue("s");
			}

			if (cmd.hasOption("d")) {
				data = cmd.getOptionValue("d");
			}

			if (cmd.hasOption("p")) {
				processor = cmd.getOptionValue("p");
			}
			
			if (cmd.hasOption("f")) {
				schemaFormat = cmd.getOptionValue("f");
			}
			
			if (cmd.hasOption("v")) {
				verbose = true;
			}
			
			if (cmd.hasOption("t")) {
				printTime = true;
			}
			
		} catch (ParseException e) {
			log.log(Level.SEVERE, "Failed to parse comand line properties", e);
			help(options);
		}
		
	}

	private void help(Options options) {
		// This prints out some help
		HelpFormatter formater = new HelpFormatter();

		formater.printHelp("Main", options);
		System.exit(0);
	}
}
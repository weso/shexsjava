package es.weso.shexjava;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;


import org.joda.time.format.PeriodFormat;



public class Main {
	static boolean verbose = false;

	public static void main(String... args) {
		
		Logger log = Logger.getLogger(Main.class.getName());
		try {
			Instant start = Instant.now();
			ArgsParser options = new ArgsParser(args);
			
			verbose = options.verbose;
			
		switch (options.processor) {
			case "shex":
				ShExValidator validator = new ShExValidator();
				log.info("Data: " + options.data + ". Schema: " + options.schema);
				// ValidationResult result = 
				validator.validate(options.data,options.schema,options.schemaFormat);
/*				if (result.size() == 0) 
					System.out.println("Valid");
				else {
					result.write(System.out,"TURTLE");
				} */
				break;
			}
			
			
			Instant end = Instant.now();
			if (options.printTime) printTime(start,end);
		} catch (Exception e) {
			System.out.println("Exception raised: " + e.getMessage());
			e.printStackTrace();
		} 
		log.info("End of program");
	}

	static void wellcome() {
		verbose("Wellcome to ShEx client (Java)");
	}
	
	static void verbose(String msg) {
	  if (verbose) {
		System.out.println(msg);
	  }
	}
	
	static void printTime(Instant start,Instant end) {
		Period timeElapsed = new Period(start,end);
     	System.out.println("Time elapsed " + 
     			PeriodFormat.getDefault().print(timeElapsed));
	}

}

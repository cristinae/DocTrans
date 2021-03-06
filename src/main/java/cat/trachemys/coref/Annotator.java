/**
 * 
 */
package cat.trachemys.coref;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONException;

import cat.trachemys.coref.CorefererCommons.CorefDocs;
import cat.trachemys.generic.Check;
import cat.trachemys.generic.FileIO;

/**
 * Main class for pre-processing a folder with raw text documents and annotate it with lexical chains
 * 
 * @author cristinae
 * @since 29.04.2017
 */
public class Annotator {
	
	/** Language */
	private final String lang;
	/** Annotation object*/
	private final CorefererFactory corefererFactory;

	/**
	 * Constructor 
	 * @param language
	 */
	public Annotator(String language){
		Check.notNull(language);
		this.lang = language;
		corefererFactory = new CorefererFactory();				
	}
	

	private void annotateFile(String file, String extension, boolean json, boolean txt) {
		
		Coreferer cf = corefererFactory.loadCoreferer(lang);
		String text = cf.loadFile(file);
		CorefDocs cd = cf.annotateText(text);
		if (json){
			String jsonFile=file.replaceAll("."+extension+"$", ".json."+extension);
			try {
				FileIO.stringToFile(new File(jsonFile), cd.doc.toString(2), true);
			} catch (JSONException | IOException e) {
				System.out.println("An error occurred when writting the json file.");
				e.printStackTrace();
			}
			//FileIO.writeJson2File(cd.doc, jsonFile);
		}
		if (txt){
			String outputFile=file.replaceAll("."+extension+"$", ".coref."+extension);
			cf.writeCoreferences(cd, outputFile);
		}
	}

	/**
	 * Parses the command line arguments
	 * 	
	 * @param args
	 * 			Command line arguments 
	 * @return
	 */
	private static CommandLine parseArguments(String[] args)
	{	
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cLine = null;
		Options options= new Options();
		CommandLineParser parser = new BasicParser();

		options.addOption("l", "language", true, 
				"Language of the input text (en)");		
		options.addOption("e", "extension", true, 
				"Extension of the input documents (if different from the language)");		
		options.addOption("i", "input", true, 
				"Input folder to annotate -one file per raw document-");		
		options.addOption("j", "json", true, 
				"Save annotations in json file 1/0 (default: 1)");		
		options.addOption("o", "txt", true, 
				"Save document with correferences tagged 1/0 (default: 1)");		
		options.addOption("h", "help", false, "This help");

		try {			
		    cLine = parser.parse(options, args);
		} catch( ParseException exp ) {
			System.out.println("Unexpected exception :" + exp.getMessage() );			
		}	
		
		if (cLine.hasOption("h")) {
			formatter.printHelp(Annotator.class.getSimpleName(),options );
			System.exit(0);
		}
		
		if (cLine == null || !(cLine.hasOption("l")) ) {
			System.out.println("Please, set the language\n");
			formatter.printHelp(Annotator.class.getSimpleName(),options );
			System.exit(1);
		}		

		return cLine;		
	}


	/**
	 * Main function to run the class, serves as example
	 * 
	 * @param args 
	 * 		-l Language of the input text 
	 * 		-e Extension of the input documents
	 *      -i Input folder
	 *      -j save annotations in json file
	 *      -o create an output file with coreferences as tags to the source file
	 */
	public static void main(String[] args) {
		CommandLine cLine = parseArguments(args);
		
		// Language
		String language = cLine.getOptionValue("l");
		// Files extension
		String extension = language;
		if (cLine.hasOption("e")){
			extension = cLine.getOptionValue("e");
		} 	
		
		// Input folder
		File input = new File(cLine.getOptionValue("i"));
		
		// Output files
		boolean json = true;
		boolean txt = true;
		if (cLine.hasOption("j")){
			json = Integer.valueOf(cLine.getOptionValue("j")) != 0;
		} 
		if (cLine.hasOption("o")){
			txt = Integer.valueOf(cLine.getOptionValue("o")) != 0;
		} 
		
		// Run
		List<String> files = FileIO.getFilesExt(input, extension);
		for (String file : files) {
			Annotator ann = new Annotator(language);
			ann.annotateFile(file, extension, json, txt);
		}
		
	}
	

}

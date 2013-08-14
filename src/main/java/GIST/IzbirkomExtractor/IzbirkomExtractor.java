/**
 * Copyright © 2013 , UT-Battelle, LLC
 * All rights reserved
 *                                        
 * JavaParserOfAddresses, Version 1.0
 * http://github.com/sorokine/JavaParserOfAddresses
 * 
 * This program is freely distributed under UT-Batelle, LLC
 * open source license.  Read the file LICENSE.txt for details.
 */

package GIST.IzbirkomExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.io.FileUtils;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class IzbirkomExtractor {
	
	/**
	 * Main logger
	 */
	private final static Logger logger = Logger.getLogger("ALL");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// process command-line options
		Options options = new Options();
		options.addOption("n", "noaddr", false, "do not do any address matching (for testing)");
		options.addOption("i", "info", false, "create and populate address information table");
		options.addOption("h", "help", false, "this message");
		
		// database connection
		options.addOption("s", "server", true, "database server to connect to");
		options.addOption("d", "database", true, "OSM database name");
		options.addOption("u", "user", true, "OSM database user name");
		options.addOption("p", "pass", true, "OSM database password");
		
		// logging options
		options.addOption("l", "logdir", true, "log file directory (default './logs')");
		options.addOption("e", "loglevel", true, "log level (default 'FINEST')");
		
		// automatically generate the help statement
		HelpFormatter help_formatter = new HelpFormatter();
		
		// database URI for connection
    	String dburi = null;

    	// Information message for help screen
    	String info_msg = "IzbirkomExtractor [options] <html_directory>";
    	
		try {
 			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse( options, args);

			if (cmd.hasOption('h') || cmd.getArgs().length != 1) {
				help_formatter.printHelp( info_msg , options );
				System.exit(1);
			}

			/* prohibit n and i together */
			if (cmd.hasOption('n') && cmd.hasOption('i')) {
				System.err.println("Options 'n' and 'i' cannot be used together.");
				System.exit(1);
			}
			
			/* require database arguments without -n */
			if (cmd.hasOption('n') && (cmd.hasOption('s') || cmd.hasOption('d') || cmd.hasOption('u') || cmd.hasOption('p'))) {
				System.err.println("Options 'n' and does not need any databse parameters.");
				System.exit(1);
			}
			
			/* require all 4 database options to be used together */
			if (!cmd.hasOption('n') && !(cmd.hasOption('s') && cmd.hasOption('d') && cmd.hasOption('u') && cmd.hasOption('p'))) {
				System.err.println("For database access all of the following arguments have to be specified: server, database, user, pass");
				System.exit(1);
			}
			
			/* useful variables */
    	   	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm");
    	   	String dateString = formatter.format(new Date());
    	   	
			/* setup logging */
    	   	File logdir = new File( cmd.hasOption('l') ? cmd.getOptionValue('l') : "logs" ); 
    	   	FileUtils.forceMkdir(logdir);
    	   	File log_file_name = new File(logdir + "/" + IzbirkomExtractor.class.getName() + "-" + formatter.format(new Date()) + ".log");
    	   	FileHandler log_file = new FileHandler(log_file_name.getPath());

    	   	/* create "latest" link to currently created log file */
    	   	Path latest_log_link = Paths.get(logdir + "/latest");
    	   	Files.deleteIfExists(latest_log_link);
    	   	Files.createSymbolicLink(latest_log_link, Paths.get(log_file_name.getName())); 
    	   	
    	   	log_file.setFormatter(new SimpleFormatter());
			LogManager.getLogManager().reset(); // prevents logging to console
			logger.addHandler(log_file);
			logger.setLevel(cmd.hasOption('e') ? Level.parse(cmd.getOptionValue('e')) : Level.FINEST);  
			
            // open directory with HTML files and create file list
            File dir = new File(cmd.getArgs()[0]);
            if (!dir.isDirectory()) {
            	System.err.println("Unable to find directory '" + cmd.getArgs()[0] + "', exiting");
            	System.exit(1);
            }
            PathMatcher pmatcher = FileSystems.getDefault().getPathMatcher("glob:Сведения об избирательн* участк*.html");
            ArrayList<File> html_files = new ArrayList<>();
            for (Path file : Files.newDirectoryStream(dir.toPath())) 
				if (pmatcher.matches(file.getFileName()))
					html_files.add(file.toFile());
            if (html_files.size() == 0) {
            	System.err.println("No matching HTML files found in '" + dir.getAbsolutePath() + "', exiting");
            	System.exit(1);
            }

			// create csvResultSink
    	   	FileOutputStream csvout_file = new FileOutputStream("parsed_addresses-" + dateString + ".csv");
 	        OutputStreamWriter csvout = new OutputStreamWriter(csvout_file, "UTF-8");            
 	        ResultSink csvResultSink = new CSVResultSink( csvout, new CSVStrategy('|', '"', '#'));
 			
 			// Connect to DB and osmAddressMatcher
 	        AddressMatcher osmAddressMatcher;
 	        DBSink dbSink = null;
 	        DBInfoSink dbInfoSink = null;
 	        if (cmd.hasOption('n')) {
 	        	osmAddressMatcher = new DummyAddressMatcher();
 	        } else {
 	        	dburi = "jdbc:postgresql://" + cmd.getOptionValue('s') + "/" + cmd.getOptionValue('d');
 	        	Connection con = DriverManager.getConnection(dburi, cmd.getOptionValue('u'), cmd.getOptionValue('p'));
 	        	osmAddressMatcher = new OsmAddressMatcher(con);
 	        	dbSink = new DBSink(con);
 	        	if (cmd.hasOption('i')) dbInfoSink = new DBInfoSink(con);
 	        }
 	        
 	        /* create resultsinks */
 	        SinkMultiplexor sm = SinkMultiplexor.newSinkMultiplexor();
 	        sm.addResultSink(csvResultSink);
 	        if (dbSink != null) {
 	        	sm.addResultSink(dbSink);
 	        	if (dbInfoSink != null) sm.addResultSink(dbInfoSink);
 	        }
 	        
 			// create tableExtractor
            TableExtractor te = new TableExtractor( osmAddressMatcher, sm);

            // TODO: printout summary of options: processing date/time, host, directory of HTML files, jdbc uri, command line with parameters
            
 			// iterate through files
        	logger.info("Start processing " + html_files.size() + " files in " + dir);
            for (int i = 0; i < html_files.size(); i++) {
            	System.err.println("Parsing #" + i + ": " + html_files.get(i));
    			te.processHTMLfile(html_files.get(i));
			}
            
            System.err.println("Processed " + html_files.size() + " HTML files");
        	logger.info("Finished processing " + html_files.size() + " files in " + dir);
            
		} catch (ParseException e1) {
			System.err.println("Failed to parse CLI: " + e1.getMessage());
			help_formatter.printHelp( info_msg, options );
			System.exit(1);
		} catch (IOException e) {
			System.err.println("I/O Exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		} catch (SQLException e) {
			System.err.println("Database '" + dburi + "': " + e.getMessage());
			System.exit(1);
		} catch (ResultSinkException e) {
			System.err.println("Failed to initialize ResultSink: " + e.getMessage());
			System.exit(1);
		} catch (TableExtractorException e) {
			System.err.println("Failed to initialize Table Extractor: " + e.getMessage());
			System.exit(1);
		} catch (CloneNotSupportedException | IllegalAccessException | InstantiationException e) {
			System.err.println("Something really odd happened: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}

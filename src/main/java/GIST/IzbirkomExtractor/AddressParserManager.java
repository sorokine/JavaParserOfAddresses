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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.Reflections;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class AddressParserManager {
	
	/**
	 * set of parser classes that is loaded at run-time
	 */
	private TreeSet<AddressParser> parsers;
	
	public AddressParserManager() {
		
	}

	/**
	 * Load all available street address parsers (subclasses for AddressParser) from the classpath 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void loadAllParsers() throws InstantiationException, IllegalAccessException {
		
		Reflections reflections = new Reflections("GIST.IzbirkomExtractor");
		Set<Class<? extends GIST.IzbirkomExtractor.AddressParser>> parserClasses = 
				reflections.getSubTypesOf(GIST.IzbirkomExtractor.AddressParser.class);

		parsers = new TreeSet<AddressParser>();
		for (Class<? extends AddressParser> class1 : parserClasses) {

			/* make sure that the class is not abstract */
			if (Modifier.isAbstract((class1.getModifiers()))) continue;

			/* make sure that there are no parser with same prirority already in the set
			 * if, they are decrease priority until it fits */
			AddressParser parser_inst = (AddressParser)class1.newInstance();
			while (parsers.contains(parser_inst))
				parser_inst.incPriority();
			parsers.add(parser_inst);
		}
		
		int c=0;
		for (AddressParser parser : parsers) {
			System.out.println("" + ++c + "\t" + parser);
		}
	}
	
	/**
	 * Tries to parse the given string by all available address parsers.  
	 * 
	 * @param address_string
	 * @param station_address 
	 * @param org_address 
	 * @return
	 */
	public ArrayList<ParsedAddress> parseThroughAllParsers(String address_string, String org_address, String station_address) {
		ArrayList<ParsedAddress> pas = new ArrayList<ParsedAddress>();
		for(AddressParser parser : parsers) {
			ParsedAddress pa = parser.parse(address_string, org_address, station_address);
			if (pa != null) {
				pas.add(pa);
				if (parser.isTerminalParser())
					break;
			}
		}
		return pas;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AddressParserManager [parsers=" + parsers + "]";
	}

	/**
	 * This is for testing only, will be removed
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AddressParserManager apm = new AddressParserManager();
			apm.loadAllParsers();
			System.out.println(apm.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

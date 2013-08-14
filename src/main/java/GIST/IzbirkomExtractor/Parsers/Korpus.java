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

package GIST.IzbirkomExtractor.Parsers;

import java.util.ArrayList;
import java.util.regex.Matcher;

import GIST.IzbirkomExtractor.AddressParser;
import GIST.IzbirkomExtractor.IndividualAddress;
import GIST.IzbirkomExtractor.ParsedAddress;

/**
 * Extract addresses in the for of <Корпус> <numbers>
 * 
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class Korpus extends AddressParser {
	
	public Korpus() {
		setRegex("^Корпуса?:\\s*");
		setPriority(200);
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#parse(java.lang.String)
	 */
	@Override
	public ParsedAddress parse(String street_name_and_numbers, String org_address, String station_address) {
		Matcher m = regex.matcher(street_name_and_numbers);
		if (m.find()) 
			return newParsedAddress(street_name_and_numbers, org_address, station_address)
					.setListPart(street_name_and_numbers.substring(m.end()).trim());
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#enumerateIndividualAddresses(java.lang.String, java.lang.String)
	 */
	@Override
	public Iterable<IndividualAddress> enumerateIndividualAddresses(
			ParsedAddress pa) {
		ArrayList<IndividualAddress> addresses = new ArrayList<IndividualAddress>();
		
		String korpus_arr[] = pa.getListPart().split("\\s*,\\s*"); 
		for (String korpus : korpus_arr) {
			if (!korpus.isEmpty())
				addresses.add(pa.newIndividualAddress().setKorpus(korpus));
		}
		return addresses;
	}

	@Override
	public boolean isCompleteAddress(IndividualAddress ia) {
		return ia.getKorpus() != null;
	}

	@Override
	public boolean isTerminalParser() {
		return true;
	}

}

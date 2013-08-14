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
import java.util.regex.Pattern;

import GIST.IzbirkomExtractor.AddressParser;
import GIST.IzbirkomExtractor.IndividualAddress;
import GIST.IzbirkomExtractor.ParsedAddress;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class StreetList extends AddressParser {
	
	public StreetList() {
		setRegex(Pattern.compile("^\\s*(улицы|переулки):?\\s*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
		// (– частные домовладения)?
		setPriority(100);
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#parse(java.lang.String)
	 */
	@Override
	public ParsedAddress parse(String street_name_and_numbers, String org_address, String station_address) {
		Matcher m = regex.matcher(street_name_and_numbers);
		if (m.find()) 
			return newParsedAddress(street_name_and_numbers, org_address, station_address)
					.setListPart(street_name_and_numbers.substring(m.end()).trim())
					.setNamePart(m.group(1).toLowerCase().replaceAll("(ицы|еулки)", "").replaceAll("\\s*-частные домовладения\\s*", ""));
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#enumerateIndividualAddresses(java.lang.String, java.lang.String)
	 */
	@Override
	public Iterable<IndividualAddress> enumerateIndividualAddresses(
			ParsedAddress pa) {
		
		/* sanitization */
		String list = pa.getListPart();

		/* extract street type */
		String streetType = pa.getNamePart();
		
		ArrayList<IndividualAddress> addresses = new ArrayList<IndividualAddress>();
		for (String s : list.split("\\s*,\\s*")) {
			addresses.add(pa.newIndividualAddress().setStreetName(streetType + " " + s));
		}
		
		return addresses;
	}

	@Override
	public boolean isCompleteAddress(IndividualAddress ia) {
		return ia.getStreetName() != null;
	}

	@Override
	public boolean isTerminalParser() {
		return true;
	}

}

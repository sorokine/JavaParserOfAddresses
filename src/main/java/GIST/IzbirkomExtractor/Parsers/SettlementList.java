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
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class SettlementList extends AddressParser {

	/**
	 * 
	 */
	public SettlementList() {
		setRegex("^(пос|дер)\\s+");
		setPriority(310);
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#parse(java.lang.String)
	 */
	@Override
	public ParsedAddress parse(String street_name_and_numbers, String org_address, String station_address) {
		Matcher m = regex.matcher(street_name_and_numbers);
		if (m.find()) 
			return newParsedAddress(street_name_and_numbers, org_address, station_address)
					.setNamePart(m.group(1)) // this is settlement type
					.setListPart(street_name_and_numbers.substring(m.end()).trim());
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#enumerateIndividualAddresses(GIST.IzbirkomExtractor.ParsedAddress)
	 */
	@Override
	public Iterable<IndividualAddress> enumerateIndividualAddresses(
			ParsedAddress pa) {
		/* extract street type */
		String settlementType = pa.getNamePart();
		
		ArrayList<IndividualAddress> addresses = new ArrayList<IndividualAddress>();
		for (String s : pa.getListPart().split("\\s*,\\s*")) {
			addresses.add(pa.newIndividualAddress().setSettlement(settlementType + " " + s));
		}
		
		return addresses;
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#isCompleteAddress(GIST.IzbirkomExtractor.IndividualAddress)
	 */
	@Override
	public boolean isCompleteAddress(IndividualAddress ia) {
		return ia.getSettlement() != null;
	}

	@Override
	public boolean isTerminalParser() {
		return true;
	}

}

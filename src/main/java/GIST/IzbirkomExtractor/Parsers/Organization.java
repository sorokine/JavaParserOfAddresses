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
import GIST.IzbirkomExtractor.AddressParser;
import GIST.IzbirkomExtractor.IndividualAddress;
import GIST.IzbirkomExtractor.ParsedAddress;

/**
 * Class of addresses that are identified by the name of organization
 * 
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public abstract class Organization extends AddressParser {

	@Override
	public boolean isTerminalParser() {
		return false;
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#enumerateIndividualAddresses(GIST.IzbirkomExtractor.ParsedAddress)
	 */
	@Override
	public Iterable<IndividualAddress> enumerateIndividualAddresses(
			ParsedAddress pa) {
		ArrayList<IndividualAddress> addresses = new ArrayList<IndividualAddress>();
		addresses.add(pa.newIndividualAddress().setOrgName(pa.getNamePart()));
		return addresses;
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#isCompleteAddress(GIST.IzbirkomExtractor.IndividualAddress)
	 */
	@Override
	public boolean isCompleteAddress(IndividualAddress ia) {
		return ia.getOrgName() != null;
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#parse(java.lang.String)
	 */
	@Override
	public ParsedAddress parse(String street_name_and_numbers, String org_address, String station_address) {
		
		/* try regex for all address strings*/
		if (regex.matcher(street_name_and_numbers).matches()) 
			return newParsedAddress(street_name_and_numbers, org_address, station_address)
					.setNamePart(street_name_and_numbers);
		
		if (regex.matcher(org_address).matches()) 
			return newParsedAddress(street_name_and_numbers, org_address, station_address)
					.setNamePart(org_address);
		
		if (regex.matcher(station_address).matches()) 
			return newParsedAddress(street_name_and_numbers, org_address, station_address)
					.setNamePart(station_address);
		
		return null;
	}

}

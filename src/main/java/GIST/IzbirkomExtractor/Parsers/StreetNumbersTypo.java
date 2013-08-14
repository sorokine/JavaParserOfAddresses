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

import java.util.regex.Matcher;

import GIST.IzbirkomExtractor.ParsedAddress;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class StreetNumbersTypo extends StreetNumbers {

	/**
	 * 
	 */
	public StreetNumbersTypo() {
		setRegex("[А-Яа-я]\\s*\\d");
		setPriority(800);
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.Parsers.StreetNumbers#parse(java.lang.String)
	 */
	@Override
	public ParsedAddress parse(String street_name_and_numbers, String org_address, String station_address) {
		Matcher m = regex.matcher(street_name_and_numbers);
		if (m.find()) 
			return newParsedAddress(street_name_and_numbers, org_address, station_address)
					.setNamePart(street_name_and_numbers.substring(0, m.start()+1))
					.setListPart(street_name_and_numbers.substring(m.end()-1));
		else
			return null;
	}

}

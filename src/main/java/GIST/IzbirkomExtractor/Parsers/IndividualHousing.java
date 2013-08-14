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
import java.util.regex.Pattern;

import GIST.IzbirkomExtractor.ParsedAddress;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class IndividualHousing extends StreetList {
	
	
	public IndividualHousing() {
		setRegex(Pattern.compile("^([^: ]+):?\\s+(.+)-частные домовладения$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
		setPriority(80);
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#parse(java.lang.String)
	 */
	@Override
	public ParsedAddress parse(String street_name_and_numbers, String org_address, String station_address) {
		Matcher m = regex.matcher(street_name_and_numbers);
		if (m.find())
			return newParsedAddress(street_name_and_numbers, org_address, station_address)
					.setListPart(m.group(2))
					.setNamePart(m.group(1).toLowerCase().replaceAll("еул(ок|ки)", ""));
		return null;
	}

}

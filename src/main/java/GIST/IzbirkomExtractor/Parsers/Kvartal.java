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

import GIST.IzbirkomExtractor.ParsedAddress;


/**
 * Parser for kvartals in Капотня
 * 
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class Kvartal extends StreetNumbers {

	/**
	 * Kvartal number goes into the namePart of the {@link ParsedAddress} 
	 */
	public Kvartal() {
		setRegex("Капотня (\\d+) квартал,\\s*");
		setPriority(100); /* this parser should be called early */
	}

}

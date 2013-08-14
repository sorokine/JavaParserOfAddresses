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

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class JailPrison extends Organization {

	public JailPrison() {
		setRegexIgnoreUnicodeCase(".*(СИЗО|ФСИН|\\bИЗ\\b|следственный).*");
		setPriority(50);
	}

}

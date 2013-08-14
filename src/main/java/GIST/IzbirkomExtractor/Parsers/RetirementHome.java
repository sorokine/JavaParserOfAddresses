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
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class RetirementHome extends Organization {

	public RetirementHome() {
		setRegexIgnoreUnicodeCase(".*(пансионат|санатор|ПВТ|ПВВ).*");
		setPriority(50);
	}
	
	/**
	 * This is for testing only, will be removed
	 * @param args
	 */
	public static void main(String[] args) {
		
		RetirementHome rh = new RetirementHome();
		ParsedAddress pa = rh.parse("Пансионат 1 для ветеранов войны и труда", "", "");
		if (pa != null)
			System.out.println(pa);
		else
			System.out.println("Parse failed");
	}
}

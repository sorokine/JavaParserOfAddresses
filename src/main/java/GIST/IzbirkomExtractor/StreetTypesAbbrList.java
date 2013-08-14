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

public class StreetTypesAbbrList extends AbbrList {
	
	public StreetTypesAbbrList() {
		
		addAbbrev("ул", "улица");
		addAbbrev("б-р", "бульвар");
		addAbbrev("пр-д", "проезд");
		addAbbrev("пр-зд", "проезд");
		addAbbrev("пр-т", "проспект"); //FIXME: breaks пр parsing
		addAbbrev("пер", "переулок");
		addAbbrev("пр", "проезд");
		addAbbrev("просп", "проспект");
		addAbbrev("ш", "шоссе");
		addAbbrev("шосс", "шоссе");
		addAbbrev("бульв", "бульвар");
		addAbbrev("туп", "тупик");
		addAbbrev("наб", "набережная");
		addAbbrev("пл", "площадь");
		addAbbrev("ал-я", "аллея");
		addAbbrev("проез", "проезд");
		addAbbrev("Проезд", "проезд");  // FIXME: this is a work-around for non-working case insensitive match
		addAbbrev("Проспект", "проспект");
		addAbbrev("Переулок", "Переулок");
		addAbbrev("Шоссе", "шоссе");
		addAbbrev("Бульвар", "бульвар");
		// п проезд переулок проспект площадь
		
	}
	
}
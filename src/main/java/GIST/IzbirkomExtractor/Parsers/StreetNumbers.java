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
 * Parser for the address in the form <street name>, <list of house numbers>
 * 
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class StreetNumbers extends AddressParser {
	
	/**
	 * 
	 */
	public StreetNumbers() {
		setRegex("^([^,]+),");
		setPriority(300);
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressParser#parse(java.lang.String)
	 */
	@Override
	public ParsedAddress parse(String street_name_and_numbers, String org_address, String station_address) {
		Matcher m = regex.matcher(street_name_and_numbers);
		if (m.find()) 
			return newParsedAddress(street_name_and_numbers, org_address, station_address)
					.setNamePart(m.group(1).trim())
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
		
		String streetName = pa.getNamePart();
		String houseNumberList = pa.getListPart();
		
		/* clean the streets names that have leftovers from street numbers and put leftovers to street numbers*/
		Matcher number_tail_matcher_d = Pattern.compile("д\\s*([\\dкострАБ() /\\-:]+)$").matcher(streetName);
		Matcher number_tail_matcher_typo = Pattern.compile("[^к]\\s*(\\d[А\\s\\dк() /\\-–]*)$").matcher(streetName);
		if (number_tail_matcher_d.find()) {
			houseNumberList = number_tail_matcher_d.group(1) + ", " + houseNumberList;
			streetName = streetName.substring(0, number_tail_matcher_d.start());
		} else if (number_tail_matcher_typo.find()) {
			houseNumberList = number_tail_matcher_typo.group(1) + ", " + houseNumberList;
			streetName = streetName.substring(0, number_tail_matcher_typo.start()+1);
		}
		
		/* fix the street types and spaces in the in the names */
		streetName = streetName.
				replaceAll(":", "").
				replaceAll("ул\\.\\s*", "ул ").
				replaceAll("([а-яА-Я])([А-Я\\d][^А-Я])", "$1 $2"). // add spaces in CamelCase avoiding abbreviations
				replaceAll("(\\S)(туп|шоссе|просп(?!ект)|ул(?!иц)|пер|пр(?!о|-)|пр(?:-|оез)д)$", "$1 $2"). // TODO: generate these lists from streetname normalizer
				replaceAll("^(туп|шоссе|просп(?!ект)|ул(?!иц)|пер|пр(?!о|-)|пр(?:-|оез)д)(\\S)", "$1 $2");
		
		/* get rid of д, дом, дд */
		houseNumberList = houseNumberList.replaceAll("(д(ом(а)?)?|дд)\\s*", "");
		
		ArrayList<IndividualAddress> addresses = new ArrayList<IndividualAddress>();
		
		Pattern string_in_paren_pattern = Pattern.compile("\\(\\s*([^)]+)\\s*\\)");
		Pattern korpus_stroenie_pattern = Pattern.compile("\\s*(к(?:орп?\\.?)?|с(?:тр)?)\\s*");

		/* split the address line at commas but do not split if comma is inside of the parenthesis */
		int maxlen = houseNumberList.length(); // simple * in this regex does not work because of a presumed bug in Java regex engine http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6695369
		// I have to explicitly specify max negative lookahead/back length and presume it to be the string length
		String house_with_korpus_arr[] = houseNumberList.split("(?<!\\([^)]{0," + maxlen + "}),\\s?(?![^(]{0," + maxlen + "}\\))"); 
		
		for (String house_with_korpus : house_with_korpus_arr) {
			if (house_with_korpus.isEmpty()) continue;
			
			/* extract string in parenthesis */
			Matcher korpus_numbers_matcher = string_in_paren_pattern.matcher(house_with_korpus);
			if (korpus_numbers_matcher.find()) {
				
				IndividualAddress ia = pa.newIndividualAddress().setStreetName(streetName);

				if (korpus_numbers_matcher.start(1) > 0) {
					ia.setHouseNumber( house_with_korpus.substring(0,korpus_numbers_matcher.start(1)-1) );
				} else {
					ia.setParseError(true);
					ia.addMessage("Failed to parse out building number from korpus or stroenie list");
				}
				
				String korpus_numbers_list = korpus_numbers_matcher.group(1);
				char building_type = '?';
				for (String korpus_number_string : korpus_numbers_list.split("\\s*,\\s*|\\sи\\s")) {

					/* determine if it is a korpus, a stroenie or else and extract korpus number 
					 * presume that building type of the 1st element in the list defines building types of the rest of the elements */
					Matcher korpus_stroenie_matcher = korpus_stroenie_pattern.matcher(korpus_number_string);
					if (korpus_stroenie_matcher.find()) { 
						building_type = korpus_stroenie_matcher.group(1).charAt(0);
						korpus_number_string = korpus_number_string.substring(korpus_stroenie_matcher.end(1));
					}
					
					addresses.add(ia.clone().setExtendedBuilding( building_type, korpus_number_string));
				}
			} else { // no parenthesis in the string
				Matcher korpus_stroenie_matcher = korpus_stroenie_pattern.matcher(house_with_korpus);
				if (korpus_stroenie_matcher.find()) {
					
					IndividualAddress ia;
					
					if (korpus_stroenie_matcher.start(1) > 0) {
						ia = pa.newIndividualAddress().
								setStreetName(streetName).
								setHouseNumber(house_with_korpus.substring(0,korpus_stroenie_matcher.start(1)-1));
					} else if (addresses.size() > 0){ // no building number, only korpus or stroenie
						ia = addresses.get(addresses.size()-1);  // get previous address from the address list
						addresses.remove(addresses.size()-1); // and get rid of it
					} else {
						ia = pa.newIndividualAddress().
								setParseError(true);
						ia.addMessage("Korpus or stroenie without a building number");
					}
						
					char building_type = korpus_stroenie_matcher.group(1).charAt(0);
					String korpus_number = house_with_korpus.substring(korpus_stroenie_matcher.end(1));
					ia.setExtendedBuilding(building_type, korpus_number);
					
					addresses.add(ia);

				} else { // nothing looks like korpus or stroenie
					addresses.add(
							pa.newIndividualAddress()
							.setStreetName(streetName)
							.setHouseNumber(house_with_korpus)
					);
					
				}
			}
		}
		
		return addresses;
	}

	@Override
	public boolean isCompleteAddress(IndividualAddress ia) {
		return ia.getStreetName() != null && ia.getHouseNumber().getNumber() != null;
	}

	@Override
	public boolean isTerminalParser() {
		return true;
	}

}

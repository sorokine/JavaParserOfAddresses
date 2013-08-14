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

import java.util.ArrayList;

public class HouseNumber implements Cloneable {
	
	private String number 			= null;
	private String korpus 			= null;
	private String stroenie 		= null;
	private String building 		= null;
	
	/**
	 * string that lead to produced successful search
	 */
	private String searchString 	= null;
	
	/**
	 * resulting string from the database
	 */
	private String searchResult		= null;
	
	/**
	 * sequential number of the variation that resulted in a successful search
	 */
	private int variationCount 		= 0;
	
	/**
	 * Default constructor
	 */
	public HouseNumber() {}
	
	/**
	 * 
	 * @param number
	 * @param korpus
	 * @param stroenie
	 * @param building
	 * @param searchString
	 */
	public HouseNumber(String number, String korpus, String stroenie,
			String building, String matchedNumber) {
		this.number = number;
		this.korpus = korpus;
		this.stroenie = stroenie;
		this.building = building;
		this.searchString = matchedNumber;
	}

	/**
	 * @return the number part of the house number
	 */
	public String getNumber() {
		return number;
	}

	public HouseNumber setNumber(String number) {
		this.number = number.trim();
		return this;
	}

	public String getKorpus() {
		return korpus;
	}

	public HouseNumber setKorpus(String korpus) {
		this.korpus = korpus.trim();
		return this;
	}

	public String getStroenie() {
		return stroenie;
	}

	public HouseNumber setStroenie(String stroenie) {
		this.stroenie = stroenie.trim();
		return this;
	}

	public String getBuilding() {
		return building;
	}

	public HouseNumber setBuilding(String building) {
		this.building = building.trim();
		return this;
	}

	public String getSearchString() {
		return searchString;
	}

	public HouseNumber setSearchString(String matchedNumber) {
		this.searchString = matchedNumber;
		return this;
	}

	/**
	 * Formats a house number string depending upon the variable values.
	 * @param houseNumber_arg
	 * @param korpus_arg
	 * @param building_arg
	 * @param stroenie_arg
	 * @return
	 */
	private String formatHouseNumber(String houseNumber_arg, String korpus_arg, String building_arg, String stroenie_arg) {
		StringBuffer sb = new StringBuffer(houseNumber_arg);
		if (korpus_arg != null) {
			sb.append('к');
			sb.append(korpus_arg);
		}
		if (building_arg != null) {
			sb.append('к');
			sb.append(building_arg);
		}
		if (stroenie_arg != null) {
			sb.append('с');
			sb.append(stroenie_arg);
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getNormalizedHouseNumber() {
		return formatHouseNumber(
				getNumber() == null ? "" : getNumber(), 
				getKorpus(), 
				getBuilding(),
				getStroenie()
			);
	}
	
	/**
	 * Generates various combination of korpus and stroenie numbers and letters given housNumber string 
	 * @param houseNumber
	 * @return
	 */
	private ArrayList<String> flipKorpusStroenie(String houseNumber) {
		ArrayList<String> housenumbers  = new ArrayList<String>();

		/* flip korpus and stroenie */
		if (getKorpus() != null && getStroenie() != null && getBuilding() == null)
			housenumbers.add(formatHouseNumber(houseNumber, getStroenie(), getBuilding(), getKorpus()));
		
		/* use korpus in place of stroenie */
		if (getKorpus() != null && getStroenie() == null && getBuilding() == null) 
			housenumbers.add(formatHouseNumber(houseNumber, null, null, getKorpus()));
		
		/* use stroenie in place of korpus */
		if (getKorpus() == null && getStroenie() != null && getBuilding() == null) 
			housenumbers.add(formatHouseNumber(houseNumber, getStroenie(), null, null));
		
		/* use building instead of korpus */
		if (getKorpus() == null && getStroenie() == null && getBuilding() != null ) 
			housenumbers.add(formatHouseNumber(houseNumber, getBuilding(), null, null));

		return housenumbers;
	}
	
	/**
	 * Generates house number variants with dropped stroenie and korpus
	 * @param houseNumber
	 * @return
	 */
	private ArrayList<String> ignoreKorpusStroenie(String houseNumber) {
		ArrayList<String> housenumbers  = new ArrayList<String>();

		/* ignore stroenie */
		if (getStroenie() != null)
			housenumbers.add(formatHouseNumber(houseNumber, getKorpus(), getBuilding(), null));
		
		/* ignore korpus */
		if (getKorpus() != null)
			housenumbers.add(formatHouseNumber(houseNumber, null, getBuilding(), getStroenie()));
		
		return housenumbers;
	}
	
	/**
	 * Returns a list of possible variations of the house number.  Variations starting with ^
	 * should be treated as PostgreSQL regular expressions
	 * 
	 * @return
	 */
	public Iterable<String> getHouseNumberVariations() {
		ArrayList<String> housenumbers  = new ArrayList<String>();
		
		/* default address */
		housenumbers.add(getNormalizedHouseNumber());
		
		/* trying korpus, stroenie, building in different order */
		housenumbers.addAll(flipKorpusStroenie(getNumber()));
		
		/* for numbers containing - try / instead of - */
		if (getNumber().contains("-")) {
			String number_no_dash = getNumber().replaceFirst("-", "/");
			housenumbers.add(formatHouseNumber(number_no_dash, getKorpus(), getBuilding(), getStroenie()));
			housenumbers.addAll(flipKorpusStroenie(number_no_dash));
			
			/* try the number after dash as korpus or stroenie */
			String n[] = getNumber().split("-");
			if (n.length > 1) {
				housenumbers.add(formatHouseNumber(n[0], n[1], getBuilding(), getStroenie()));
				housenumbers.add(formatHouseNumber(n[0], getKorpus(), getBuilding(), n[1]));
			}
		}
		
		/* дробь variants */
		String number_no_дробь = getNumberBeforeSlash();
		String addr_regex = "^" + number_no_дробь + "[/\\-]\\d{1,3}[а-ж]?";

		if (isДробь()) {
			/* try with - instead of / */
			String dash_дробь = getNumber().replace('/', '-');
			housenumbers.add(formatHouseNumber(dash_дробь, getKorpus(), getBuilding(), getStroenie()));
			housenumbers.addAll(flipKorpusStroenie(dash_дробь));
			
			/* try with removed дробь */ 
			housenumbers.add(formatHouseNumber(number_no_дробь, getKorpus(), getBuilding(), getStroenie()));
			housenumbers.addAll(flipKorpusStroenie(number_no_дробь));
		}
		
		/* try addresses with any дробь */
		if (number_no_дробь.matches("^[^()]+$")) {/* avoid house numbers that contain regex symbols */
			/* create PostgreSQL regex to match slash address is slash is not present and address does not contain regex symbols */
			housenumbers.add(formatHouseNumber(addr_regex, getKorpus(), getBuilding(), getStroenie()));
			housenumbers.addAll(flipKorpusStroenie(addr_regex));
		}
		
		/* дробь variants with ignored korpus and stroenie */
		if (isДробь()) /* try with removed дробь */ 
			housenumbers.addAll(ignoreKorpusStroenie(number_no_дробь));
		
		/* try addresses with any дробь and removes no strett or korpus */
		if (number_no_дробь.matches("^[^()]+$")) /* avoid house numbers that contain regex symbols */
			housenumbers.addAll(ignoreKorpusStroenie(addr_regex));
		
		return housenumbers;
	}

	/**
	 * Sets korpus, stroenie, or building to korpus_stroenie_building depending upon building_type
	 * @param building_type
	 * @param korpus_stroenie_building
	 * @return
	 */
	public HouseNumber setExtendedBuilding(char building_type,
			String korpus_stroenie_building) {
		switch (building_type) {
		case 'к':
			setKorpus(korpus_stroenie_building);
			break;

		case 'с':
			setStroenie(korpus_stroenie_building);
			break;
			
		default:
			setBuilding(korpus_stroenie_building);
			break;
		}
		return this;
	}
	
	/**
	 * True if address contains a Дробь
	 * @return
	 */
	public boolean isДробь() {
		return number.contains("/");
	}

	/**
	 * 
	 */
	public String getNumberBeforeSlash() {
		if (isДробь()) {
			return number.split("/")[0];
		} else 
			return number;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HouseNumber [");
		if (number != null) {
			builder.append("number=");
			builder.append(number);
			builder.append(", ");
		}
		if (korpus != null) {
			builder.append("korpus=");
			builder.append(korpus);
			builder.append(", ");
		}
		if (stroenie != null) {
			builder.append("stroenie=");
			builder.append(stroenie);
			builder.append(", ");
		}
		if (building != null) {
			builder.append("building=");
			builder.append(building);
			builder.append(", ");
		}
		if (searchString != null) {
			builder.append("searchString=");
			builder.append(searchString);
			builder.append(", ");
		}
		if (searchResult != null) {
			builder.append("searchResult=");
			builder.append(searchResult);
			builder.append(", ");
		}
		if (variationCount != 0) {
			builder.append("variationCount=");
			builder.append(variationCount);
//			builder.append(", ");
		}
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected HouseNumber clone() throws CloneNotSupportedException {
		return new HouseNumber(number, korpus, stroenie, building, searchString);
	}

	/**
	 * @return the searchResult
	 */
	public String getSearchResult() {
		return searchResult;
	}

	/**
	 * @param searchResult the searchResult to set
	 */
	public HouseNumber setSearchResult(String searchResult) {
		this.searchResult = searchResult;
		return this;
	}

	/**
	 * @return the variationCount
	 */
	public int getVariationCount() {
		return variationCount;
	}

	/**
	 * @param variationCount the variationCount to set
	 */
	public HouseNumber setVariationCount(int variationCount) {
		this.variationCount = variationCount;
		return this;
	}

	/**
	 * This is for testing only, will be removed
	 * @param args
	 */
	public static void main(String[] args) {
		
		HouseNumber hn = (new HouseNumber()).setNumber("5").setKorpus("2").setStroenie("Б");
		try {
			for (String string : hn.getHouseNumberVariations()) 
				System.out.println(hn + " => " + string);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
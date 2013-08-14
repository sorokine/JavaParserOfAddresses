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

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class ParsedAddress {

	private String namePart 		= null; // variable for part like street name
	private String listPart 		= null; // variable for the part of an address that contains a list (may be house numbers, street names, etc.)
	private double confidence 		= 1.0;
	private AddressParser addressParser = null;
	private String srcText			= null; // text that the record was parsed from
	
	private String srcOrgAddr	 	= null; // text of the organization name (column 3 from address list)
	private String srcStationAddr	= null; // text of the polling station address (column 4 from the address list)

	// count of secondary matches (i.e., successfully matched parsers of lower priority) 
	private int parserMatchCount = 0;

	public static ParsedAddress createParsedAddress() {
		return new ParsedAddress();
	}
	
	/**
	 * Default constructor
	 */
	public ParsedAddress() {}

	/**
	 * @return the streetName
	 */
	public String getNamePart() {
		return namePart;
	}

	/**
	 * @return the houseNumberList
	 */
	public String getListPart() {
		return listPart;
	}

	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * @return the addressParser
	 */
	public AddressParser getAddressParser() {
		return addressParser;
	}

	/**
	 * @param streetName the streetName to set
	 */
	public ParsedAddress setNamePart(String streetName) {
		this.namePart = streetName;
		return this;
	}

	/**
	 * @param houseNumberList the houseNumberList to set
	 */
	public ParsedAddress setListPart(String houseNumberList) {
		this.listPart = houseNumberList;
		return this;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public ParsedAddress setConfidence(double confidence) {
		this.confidence = confidence;
		return this;
	}

	/**
	 * @param addressParser the addressParser to set
	 */
	public ParsedAddress setAddressParser(AddressParser addressParser) {
		this.addressParser = addressParser;
		return this;
	}

	/**
	 * Returns a list of individual addresses from the ParsedAddress streetName and 
	 * houseNumberList.  The call is chained to the similar method in the associated parser.
	 * @return
	 */
	Iterable<IndividualAddress> enumerateIndividualAddresses() {
		return addressParser.enumerateIndividualAddresses(this);
	}

	/**
	 * Creates an IndividualAddress withe preset source address 
	 * @return
	 */
	public IndividualAddress newIndividualAddress() {
		return IndividualAddress.createIndividualAddress().setSourceAddress(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParsedAddress [");
		if (namePart != null) {
			builder.append("namePart=");
			builder.append(namePart);
			builder.append(", ");
		}
		if (listPart != null) {
			builder.append("listPart=");
			builder.append(listPart);
			builder.append(", ");
		}
//		builder.append("confidence=");
//		builder.append(confidence);
//		builder.append(", ");
		if (addressParser != null) {
			builder.append("addressParser=");
			builder.append(addressParser.getClass().getSimpleName());
			builder.append(", ");
		}
		builder.append("parserMatchCount=");
		builder.append(parserMatchCount);
		builder.append(", ");
		if (srcText != null) {
			builder.append("srcText=\"");
			builder.append(srcText);
			builder.append("\", ");
		}
		builder.append("]");
		if (srcOrgAddr != null) {
			builder.append("srcOrgAddr=\"");
			builder.append(srcOrgAddr);
			builder.append("\", ");
		}
		if (srcStationAddr != null) {
			builder.append("srcStationAddr=\"");
			builder.append(srcStationAddr);
			builder.append("\", ");
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the srcText
	 */
	public String getSrcText() {
		return srcText;
	}

	/**
	 * @param srcText the srcText to set
	 */
	public ParsedAddress setSrcText(String srcText) {
		this.srcText = srcText;
		return this;
	}

	/**
	 * @return the srcOrgAddr
	 */
	public String getSrcOrgAddr() {
		return srcOrgAddr;
	}

	/**
	 * @param srcOrgAddr the srcOrgAddr to set
	 */
	public ParsedAddress setSrcOrgAddr(String srcOrgAddr) {
		this.srcOrgAddr = srcOrgAddr;
		return this;
	}

	/**
	 * @return the srcStationAddr
	 */
	public String getSrcStationAddr() {
		return srcStationAddr;
	}

	/**
	 * @param srcStationAddr the srcStationAddr to set
	 */
	public ParsedAddress setSrcStationAddr(String srcStationAddr) {
		this.srcStationAddr = srcStationAddr;
		return this;
	}

	/**
	 * @return the parserMatchCount
	 */
	public int getParserMatchCount() {
		return parserMatchCount;
	}

	/**
	 * @param parserMatchCount the parserMatchCount to set
	 */
	public ParsedAddress setParserMatchCount(int secondaryMatchCount) {
		this.parserMatchCount = secondaryMatchCount;
		return this;
	}

}

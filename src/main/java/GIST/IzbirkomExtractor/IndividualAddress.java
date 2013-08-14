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

import gnu.trove.set.hash.TLongHashSet;

import java.util.ArrayList;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class IndividualAddress implements Cloneable {
	
	private String raion 			= null;
	private int psid 				= -1; // polling station ID
	private String settlement		= null; // поселок и пр.
	private String streetName 		= null; // TODO: should I keep in a parsed form?
//  private String streetNameContent = null; // street name type (person, date, event, etc.)
//	private String streetType 		= null; //  add support for street type
	private String orgName     		= null; // organization name 
	private boolean parseError 		= false;
	
	private int matchCount 		= 0; // count of matching street names 
	private String matchedStreetName  = null; // a form of the street name that was found on during address matching
	
	private HouseNumber houseNumber = new HouseNumber();
	private TLongHashSet osmid 	= new TLongHashSet(); // set of corresponding OSM IDs, all OSM ID should be unique 
	private ArrayList<String> msgs = new ArrayList<String>(); // array of parsing and/or address matching messages

	private ParsedAddress sourceAddress = null;  // ParsedAddress from which this address was created
	private ArrayList<ParsedAddress> otherParses = null; // list of other parsed address that are able to parse current address
	
	/**
	 * Default constructor
	 */
	public IndividualAddress() {}
	
	/**
	 * Factory method to be used with setters.
	 * @return
	 */
	public static IndividualAddress createIndividualAddress() {
		return new IndividualAddress();
	}

	/**
	 * @return the raion
	 */
	public String getRaion() {
		return raion;
	}

	/**
	 * @return the psid
	 */
	public int getPsid() {
		return psid;
	}

	/**
	 * @return the settlement
	 */
	public String getSettlement() {
		return settlement;
	}

	/**
	 * @return the streetName
	 */
	public String getStreetName() {
		return streetName;
	}

	/**
	 * @return the houseNumber
	 */
	public HouseNumber getHouseNumber() {
		return houseNumber;
	}

	/**
	 * @return the parse_error
	 */
	public boolean hasParseError() {
		return parseError;
	}

	/**
	 * @return the matchedStreetName
	 */
	public String getMatchedStreetName() {
		return matchedStreetName;
	}

	/**
	 * @return the osmid
	 */
	public TLongHashSet getOsmid() {
		return osmid;
	}

	/**
	 * @param matchedStreetName the matchedStreetName to set
	 * @return 
	 */
	public IndividualAddress setMatchedStreetName(String matchedStreetName) {
		this.matchedStreetName = matchedStreetName;
		return this;
	}

	/**
	 * @param raion the raion to set
	 */
	public IndividualAddress setRaion(String raion) {
		this.raion = raion.trim();
		return this;
	}

	/**
	 * @param psid the psid to set
	 */
	public IndividualAddress setPsid(int psid) {
		this.psid = psid;
		return this;
	}

	/**
	 * @param settlement the settlement to set
	 */
	public IndividualAddress setSettlement(String settlement) {
		this.settlement = settlement.trim();
		return this;
	}

	/**
	 * @param streetName the streetName to set
	 */
	public IndividualAddress setStreetName(String streetName) {
		this.streetName = streetName.trim();
		return this;
	}

	/**
	 * @param houseNumber the houseNumber to set
	 */
	public IndividualAddress setHouseNumber(String houseNumber) {
		this.houseNumber.setNumber(houseNumber.trim());
		return this;
	}

	public IndividualAddress setHouseNumber(HouseNumber hn) {
		this.houseNumber = hn;
		return this;
	}

	/**
	 * @param parse_error the parse_error to set
	 */
	public IndividualAddress setParseError(boolean parseError) {
		this.parseError = parseError;
		return this;
	}

	/**
	 * @return the sourceAddress
	 */
	public ParsedAddress getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * @param sourceAddress the sourceAddress to set
	 */
	public IndividualAddress setSourceAddress(ParsedAddress sourceAddress) {
		this.sourceAddress = sourceAddress;
		return this;
	}

	/**
	 * Add another OSM ID to the list of osm ids
	 * @param osmid the osmid to set
	 */
	public IndividualAddress addOsmid(long osmid) {
		this.osmid.add(osmid);
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IndividualAddress clone() {
		IndividualAddress ia_cloned = null;
		try {
			ia_cloned = (IndividualAddress) super.clone();
			ia_cloned.msgs = new ArrayList<String>(msgs);
			ia_cloned.osmid = new TLongHashSet(this.osmid);
			ia_cloned.setHouseNumber(houseNumber.clone());
		} catch (CloneNotSupportedException e) {
			/* this can never happen but anyway*/
			System.err.println("INTERNAL ERROR:");
			e.printStackTrace();
			System.exit(1);
		}
		return ia_cloned;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IndividualAddress [");
		if (raion != null) {
			builder.append("raion=\"");
			builder.append(raion);
			builder.append("\", ");
		}
		if (psid != -1) {
			builder.append("psid=");
			builder.append(psid);
			builder.append(", ");
		}
		if (settlement != null) {
			builder.append("settlement=\"");
			builder.append(settlement);
			builder.append("\", ");
		}
		if (streetName != null) {
			builder.append("streetName=\"");
			builder.append(streetName);
			builder.append("\", ");
		}

		builder.append("houseNumber=");
		builder.append(houseNumber.toString());
		builder.append(", ");

		if (orgName != null) {
			builder.append("orgName=\"");
			builder.append(orgName);
			builder.append("\", ");
		}
		if (sourceAddress != null) {
			builder.append("sourceAddress=");
			builder.append(sourceAddress);
			builder.append(", ");
		}
		if (parseError) {
			builder.append("parseError=\"");
			builder.append(parseError);
			builder.append("\", ");
		}
		if (otherParses != null && !otherParses.isEmpty()) {
			builder.append("otherParses=[");
			builder.append(getOtherParsesAsString());
			builder.append("], ");
		}
		if (!osmid.isEmpty()) {
			builder.append("osmid=");
			builder.append(osmid);
			builder.append(", ");
		}
		if (!msgs.isEmpty()) {
			builder.append("msgs=");
			builder.append(msgs);
		}
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * @return
	 * @see GIST.IzbirkomExtractor.ParsedAddress#getAddressParser()
	 */
	public AddressParser getAddressParser() {
		return sourceAddress.getAddressParser();
	}

	/**
	 * @return
	 * @see GIST.IzbirkomExtractor.ParsedAddress#getSrcText()
	 */
	public String getSrcText() {
		return sourceAddress.getSrcText();
	}

	/**
	 * @return
	 * @see GIST.IzbirkomExtractor.ParsedAddress#getSrcOrgAddr()
	 */
	public String getSrcOrgAddr() {
		return sourceAddress.getSrcOrgAddr();
	}

	/**
	 * @return
	 * @see GIST.IzbirkomExtractor.ParsedAddress#getSrcStationAddr()
	 */
	public String getSrcStationAddr() {
		return sourceAddress.getSrcStationAddr();
	}

	/**
	 * Adds a message to list of error and information messages list of the address
	 * @param msg
	 */
	public void addMessage(String msg) {
		this.msgs.add(msg);
	}

	public String[] toStringArray() {
		return new String[]{ 
				raion 		== null ? "" : raion, 
				psid		==  -1  ? "" : Integer.toString(psid),
				streetName 	== null ? "" : streetName, 
				houseNumber.getNumber() == null ? "" : houseNumber.getNumber(), 
				houseNumber.getKorpus() 		== null ? "" : houseNumber.getKorpus(), 
				houseNumber.getStroenie() 	== null ? "" : houseNumber.getStroenie(), 
				houseNumber.getBuilding()	== null ? "" : houseNumber.getBuilding(), 
				parseError ? "ERROR" : "", 
				"" + osmid,
				this.toString()
				};
	}

	/**
	 * checks if all address components are in place
	 * @return
	 */
	public boolean hasCompleteAddress() {
		if (getAddressParser() == null)
			return false;
		else
			return getAddressParser().isCompleteAddress(this);
	}

	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}

	/**
	 * @param orgName the orgName to set
	 */
	public IndividualAddress setOrgName(String orgName) {
		this.orgName = orgName;
		return this;
	}

	/**
	 * @param building_type
	 * @param korpus_stroenie_building
	 * @return
	 * @see GIST.IzbirkomExtractor.HouseNumber#setExtendedBuilding(char, java.lang.String)
	 */
	public IndividualAddress setExtendedBuilding(char building_type,
			String korpus_stroenie_building) {
		houseNumber.setExtendedBuilding(building_type,
				korpus_stroenie_building);
		return this;
	}

	/**
	 * @return
	 * @see GIST.IzbirkomExtractor.HouseNumber#getKorpus()
	 */
	public String getKorpus() {
		return houseNumber.getKorpus();
	}

	/**
	 * @param korpus
	 * @return
	 * @see GIST.IzbirkomExtractor.HouseNumber#setKorpus(java.lang.String)
	 */
	public IndividualAddress setKorpus(String korpus) {
		houseNumber.setKorpus(korpus);
		return this;
	}

	/**
	 * @return the matchCount
	 */
	public int getMatchCount() {
		return matchCount;
	}

	/**
	 * @param matchCount the matchCount to set
	 */
	public IndividualAddress setMatchCount(int matchingCount) {
		this.matchCount = matchingCount;
		return this;
	}

	/**
	 * @return the otherParses
	 */
	public ArrayList<ParsedAddress> getOtherParses() {
		return otherParses;
	}

	/**
	 * @param otherParses the otherParses to set
	 */
	public IndividualAddress setOtherParses(ArrayList<ParsedAddress> otherParses) {
		this.otherParses = otherParses;
		return this;
	}
	
	/**
	 * 
	 * @return otherParses as String
	 */
	public String getOtherParsesAsString() {
		StringBuilder sb = new StringBuilder();
		for (ParsedAddress pa : otherParses) {
			sb.append(pa.getAddressParser().getClass().getSimpleName());
			sb.append(", ");
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @return error messages as one string
	 */
	public String getMsgsAsString() {
		return msgs.toString();
	}

	/**
	 * @return
	 * @see GIST.IzbirkomExtractor.ParsedAddress#getParserMatchCount()
	 */
	public int getSecondaryMatchCount() {
		return sourceAddress.getParserMatchCount();
	}

}

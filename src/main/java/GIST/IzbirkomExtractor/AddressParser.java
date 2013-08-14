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

import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public abstract class AddressParser implements Comparable<AddressParser>{
	
	/**
	 * Main logger
	 */
	protected final static Logger logger = Logger.getLogger("ALL");
	
	/**
	 * defines the sequence of the parsers called (lower the number high the priority is.
	 */
	private int priority;
	
	/**
	 * Main regex
	 */
	protected Pattern regex;
	
	/**
	 * default constructor
	 */
	protected AddressParser() {}
	
	/**
	 * sets internal regex to Pattern
	 * @param regex
	 */
	protected void setRegex(Pattern regex) {
		this.regex = regex;
	}
	
	/**
	 * set internal regex to string
	 * @param regex_string
	 */
	protected void setRegex(String regex_string) {
		setRegex(Pattern.compile(regex_string));
	}
	
	/**
	 * set internal regex to regex_string with flag to ignore case
	 * @param regex_string
	 */
	protected void setRegexIgnoreUnicodeCase(String regex_string) {
		setRegex(Pattern.compile(regex_string, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
	}
	
	/**
	 * Parses a string into Parsed address object.  This method return NULL if parsing fails.
	 * 
	 * @param street_name_and_numbers
	 * @param station_address 
	 * @param org_address 
	 * @return
	 */
	public abstract ParsedAddress parse(String street_name_and_numbers, String org_address, String station_address);

	/**
	 * Returns a list of individual house addresses.
	 * @param pa Parsed Address to extract from
	 * @return
	 */
	public abstract Iterable<IndividualAddress> enumerateIndividualAddresses(
			ParsedAddress pa);
	
	/**
	 * factory method for new ParsedAddress objects
	 * @param station_address 
	 * @param org_address 
	 * @return
	 */
	protected ParsedAddress newParsedAddress(String srcText, String org_address, String station_address) {
		ParsedAddress pa = new ParsedAddress();
		pa.setAddressParser(this).setSrcText(srcText).setSrcOrgAddr(org_address).setSrcStationAddr(station_address);
		return pa;
	}

	/**
	 * Check if {@link IndividualAddress} is complete.
	 * This method is implemented in the specific parser and result depends upon the address type. 
	 * @param ia
	 * @return true if address has all information necessary for mathcing
	 */
	public abstract boolean isCompleteAddress(IndividualAddress ia);
	
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * 
	 * @return true if the parser is confident enough that other parsers will not be able to process the string successfully
	 */
	public abstract boolean isTerminalParser();
	
	/**
	 * Confidence level of the parser
	 * @return
	 */
	public double getConfidence() {
		return 1.0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AddressParser o) {
		// return Integer.compare(this.getPriority(), o.getPriority()); // this is how it should be but does not work before Java 7
		return this.getPriority() - o.getPriority(); // danger of overflow but not important
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("" + this.getClass().getSimpleName() + " [priority=");
		builder.append(priority);
		builder.append(", ");
		if (regex != null) {
			builder.append("regex=");
			builder.append(regex);
		}
		if (isTerminalParser()) 
			builder.append(", terminalParser=yes");
		builder.append("]");
		return builder.toString();
	}

	/**
	 * decreases parser priority by one  
	 */
	public void incPriority() {
		priority++;
	}

}

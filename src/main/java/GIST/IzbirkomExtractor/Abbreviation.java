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
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class Abbreviation implements Comparable<Abbreviation>{
	
	protected String abbrev;
	protected ArrayList<String> expansions = new ArrayList<String>();
	protected Pattern pat = null;
	
	/**
	 * @param abbrev
	 * @param expansion
	 * @param pat
	 */
	public Abbreviation(String abbrev) {
		super();
		this.abbrev = abbrev;
	}
	
	/**
	 * factory method
	 * @param abbrev
	 * @return
	 */
	public static Abbreviation createAbbreviation(String abbrev) {
		return new Abbreviation(abbrev);
	}

	/**
	 * @return the abbrev
	 */
	public String getAbbrev() {
		return abbrev;
	}

	/**
	 * @return the expansion
	 */
	public ArrayList<String> getExpansions() {
		return expansions;
	}

	/**
	 * @return the pat
	 */
	public Pattern getPattern() {
		return pat;
	}

	/**
	 * @param pat the pat to set
	 */
	public void setPattern(Pattern pat) {
		this.pat = pat;
	}

	/**
	 * compares two abbreviations
	 */
	public int compareTo(Abbreviation o) {
		return abbrev.compareTo(o.abbrev);
	}

	/**
	 * add an expansion to the abbreviation
	 * @param exp
	 * @return
	 */
	public Abbreviation addExpansion(String exp) {
		expansions.add(exp);
		return this;
	}
	
	/**
	 * adds abbreviations from a array of expansions
	 * @param exp
	 * @return
	 */
	public Abbreviation addExpandions(String [] exp) {
		expansions.addAll(Arrays.asList(exp));
		return this;
	}
}

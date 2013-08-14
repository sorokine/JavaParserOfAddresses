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
import java.util.LinkedHashMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * This class handles abbreviations and expansion of abbreviations in the street
 * names.
 * 
 * TODO: faster algorithm of finding and replacing of abbreviation has to be implemented:
 * 1.  abbreviations have to be compiled into a single regex in the for \b(abbr1|abbr2|abbr3)\b
 * 2.  matcher.find extracts the group, the group is used as a hash table key to retrieve expansion
 * Problem of using pattern object as a hashmap key is that the same patterns have different hash values
 * 
 * @author Alex Sorokine <sorokine@gmail.com>
 * 
 */
public abstract class AbbrList {

	protected LinkedHashMap<String, Abbreviation> abbrevs = new LinkedHashMap<String, Abbreviation>();
	protected Pattern expansionsPattern = null;
	protected Pattern abbreviationsPattern = null;

	/**
	 * Adds an abbreviation-expansion pair to the Abbreviation list
	 * 
	 * @param abbrev
	 * @param expansion
	 */
	protected void addAbbrev(String abbrev, String expansion) {
		addAbbrev(abbrev, new String[] { expansion });
	}

	/**
	 * Adds several abbreviation-expansions pairs to the Abbreviation list.
	 * 
	 * @param abbrevs
	 * @param expansions
	 */
	protected void addAbbrev(String[] abbrevs, String[] expansions) {
		for (String abbrev : abbrevs)
			addAbbrev(abbrev, expansions);
	}

	/**
	 * Adds an abbreviation and a set of its expansions to abbreviation list.
	 * 
	 * @param abbr_string
	 * @param expansions
	 */
	protected void addAbbrev(String abbr_string, String[] expansions) {
		
		if (!abbrevs.containsKey(abbr_string)) {
			Abbreviation abbr = Abbreviation.createAbbreviation(abbr_string);
			Pattern pat = Pattern.compile("\\b" + abbr_string + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);  
			// FIXME: ignore case flag does not seem to work because abbreviations are retrieved by original case
			abbr.setPattern(pat);
			abbrevs.put(abbr_string, abbr);
		}
		abbrevs.get(abbr_string).addExpandions(expansions);
		
		expansionsPattern = abbreviationsPattern = null; /* reset the pattern to indicate modification  of the abbreviation list */
	}

	/**
	 * Expand all abbreviations in the given streetname; only the first
	 * expansion will be used
	 * 
	 * @param streetName
	 * @return street name with expanded abbreviations
	 */
	public String expandAbbreviations(String streetName) {

		Matcher mat = getAbbreviationsPattern().matcher(streetName);
		if (mat.find()) {
			// FIXME: must perform exactly like "create all expansions"
			String match = mat.group(1);
			Abbreviation ab = abbrevs.get(match);
			String exp = ab.getExpansions().get(0);
			streetName = mat.replaceFirst(exp);
		}

		return streetName;
	}

	/**
	 * Creates a list of all possible expansions of a given streetName.
	 * stretName itself will be the first element of the expansion list.
	 * 
	 * @param streetName
	 * @return
	 */
	public ArrayList<String> createAllExpansions(String streetName) {
		
		ArrayList<String> expansions = new ArrayList<String>();
		expansions.add(streetName);
		
		Matcher mat = getAbbreviationsPattern().matcher(streetName);
		if (mat.find()) {
			for (String exp : abbrevs.get(mat.group(1)).getExpansions()) {
				StringBuffer str = new StringBuffer(mat.replaceFirst(exp));
				
				/* get rid of abbreviations with dash */
				if (streetName.length() > mat.start(1) + 1 && streetName.charAt(mat.start(1) + 1) == '-') {
					str.setCharAt(mat.start(1) + exp.length(), ' ');
				}
				
				if (exp.endsWith("-")) { // support for Ново-, Нижне-, Верхне-; remove space, downcase following letter
					
					/* skip replacement and the end of string */
					if ( str. toString().endsWith(exp) ) continue;
					
					str.delete(mat.start(1) + exp.length() - 1, mat.start(1) + exp.length() + 1 );
					char upcase = str.charAt(mat.start(1) + exp.length() - 1);
					str.setCharAt(mat.start(1) + exp.length() - 1, Character.toLowerCase(upcase)) ;
				}
				
				expansions.add(str.toString());
			}
		}

		return expansions;
	}

	/**
	 * returns a pattern in the form \\(exp1|exp2|exp3)\||
	 * with all expansions
	 * @return
	 */
	public Pattern getExpansionsPattern() {

		if (expansionsPattern == null) {
			/* collect all unique expansions in a set */
			TreeSet<String> expansionSet = new TreeSet<String>();
			for (Abbreviation ar : abbrevs.values()) 
				expansionSet.addAll(ar.getExpansions());
	
			StringBuilder sb = new StringBuilder("\\s*\\b(");
			sb.append(StringUtils.join(expansionSet, '|'));
			sb.append(")\\b\\s*");
			expansionsPattern = Pattern.compile(sb.toString());
		}
		
		return expansionsPattern;
	}

	/**
	 * returns a pattern in the form \\(exp1|exp2|exp3)\||
	 * with all abbreviations
	 * @return
	 */
	public Pattern getAbbreviationsPattern() {

		if (abbreviationsPattern == null) {
			StringBuilder sb = new StringBuilder("\\b(");
			sb.append(StringUtils.join(abbrevs.keySet(), '|'));
			sb.append(")\\b");
			abbreviationsPattern = Pattern.compile(sb.toString());//, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		}
		
		return abbreviationsPattern;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbbrList [abbrevs=").append(abbrevs).append("]");
		return builder.toString();
	}

}
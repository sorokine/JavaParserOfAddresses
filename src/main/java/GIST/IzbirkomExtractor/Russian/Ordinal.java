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

package GIST.IzbirkomExtractor.Russian;

/**
 * Russian ordinal
 * 
 * @author Alex Sorokine <sorokine@gmail.com>
 */
public class Ordinal implements Comparable<Ordinal> {
	
	/**
	 * Reference to the factory that implements common method and data structures.
	 */
	private OrdinalFactory factory;

	/**
	 * Grammatical case
	 */
	private GCase gcase = null;

	/**
	 * Grammatical gender
	 */
	private GGender ggender = null;

	/**
	 * Grammatical number (singular, plural)
	 */
	private GNumber gnumber = null;

	/**
	 * Numeric value of the ordinal
	 */
	private int value = Integer.MIN_VALUE;

	/**
	 * The string this ordinal was created from
	 */
	private String origStr = null;

	/**
	 * Flag if the string representation of a numeral should contain dash.
	 */
	private boolean dash = false;

	/**
	 * Constructor with defaults
	 */
	public Ordinal(OrdinalFactory factory) {
		this.factory = factory;
	}

	/**
	 * @param value
	 * @param gcase
	 * @param ggender
	 * @param gnumber
	 */
	public Ordinal(OrdinalFactory factory, int value, GCase gcase, GGender ggender, GNumber gnumber) {
		this(factory);
		this.value = value;
		this.gcase = gcase;
		this.ggender = ggender;
		this.gnumber = gnumber;
	}

	/**
	 * Sets grammatical case, number, and gender from numeral ending. Flag for
	 * all possible cases, numbers, and genders are raised.  Empty or null string
	 * rises all flags.
	 * 
	 * @param end
	 *            Numeral's ending
	 */
	public Ordinal setGrammaticalProperties(String end) {
		// TODO Auto-generated method stub
		return this;
	}

	/**
	 * @return the gcase
	 */
	public GCase getGcase() {
		return gcase;
	}

	/**
	 * @return the ggender
	 */
	public GGender getGgender() {
		return ggender;
	}

	/**
	 * @return the gnumber
	 */
	public GNumber getGnumber() {
		return gnumber;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @return the origStr
	 */
	public String getOrigStr() {
		return origStr;
	}

	/**
	 * @param gcase
	 *            the gcase to set
	 */
	public Ordinal setGcase(GCase gcase) {
		this.gcase = gcase;
		return this;
	}

	/**
	 * @param ggender
	 *            the ggender to set
	 */
	public Ordinal setGgender(GGender ggender) {
		this.ggender = ggender;
		return this;
	}

	/**
	 * @param gnumber
	 *            the gnumber to set
	 */
	public Ordinal setGnumber(GNumber gnumber) {
		this.gnumber = gnumber;
		return this;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public Ordinal setValue(int value) {
		this.value = value;
		return this;
	}

	/**
	 * @param origStr
	 *            the origStr to set
	 */
	public Ordinal setOrigStr(String origStr) {
		this.origStr = origStr;
		return this;
	}

	/**
	 * Compares two ordinals by comparing their numeric values comparison works
	 * as numeric value -> grammatical number -> gender -> case
	 */
	public int compareTo(Ordinal o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the dash
	 */
	public boolean hasDash() {
		return dash;
	}

	/**
	 * @param dash
	 *            the dash to set
	 */
	public Ordinal setDash(boolean dash) {
		this.dash = dash;
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Ordinal [value=");
		builder.append(value);
		builder.append(", ");
		if (gcase != null) {
			builder.append("gcase=");
			builder.append(gcase);
			builder.append(", ");
		}
		if (ggender != null) {
			builder.append("ggender=");
			builder.append(ggender);
			builder.append(", ");
		}
		if (gnumber != null) {
			builder.append("gnumber=");
			builder.append(gnumber);
			builder.append(", ");
		}
		if (origStr != null) {
			builder.append("origStr=");
			builder.append(origStr);
			builder.append(", ");
		}
		builder.append("dash=");
		builder.append(dash);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * generates a part of SQL regex to match all forms of the ordinal in text
	 * @return
	 */
	public String getSQLRegex() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		sb.append(getValue());
		sb.append('|');
		sb.append(getStem());
		sb.append(")(-?(");
//		sb.append(StringUtils.join(getEndings(), '|')); TODO: this is how it should be
		sb.append("[а-я]{1,2}"); // shortcut
		sb.append("))?");
		
		return sb.toString();
	}

	/**
	 * Returns a stem of the ordinal
	 * @return
	 */
	private String getStem() {
		return factory.getStem(value);
	}

}

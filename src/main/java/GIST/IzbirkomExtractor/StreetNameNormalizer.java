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

import info.sorokine.utils.FischerKrause;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.apache.commons.lang3.text.WordUtils;

import GIST.IzbirkomExtractor.Russian.Ordinal;
import GIST.IzbirkomExtractor.Russian.OrdinalFactory;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class StreetNameNormalizer {

	private AbbrList streetTypeAbbrList = new StreetTypesAbbrList();
	private AbbrList streetNamePartList = new StreetNamePartsAbbrList();
	private OrdinalFactory ordinalFactory = new OrdinalFactory();
	
	/**
	 * Main logger
	 */
	private final static Logger logger = Logger.getLogger("ALL");
	
	public StreetNameNormalizer() {}
	
	/**
	 * 
	 * @param streetName street name to be normalized
	 * @return normalized street name
	 */
	public ArrayList<String> normalize(String streetName) {
		
		/**
		 * Array for street name variations.
		 */
		ArrayList<String> variations = new ArrayList<String>();
		
		/* expand abbreviated street type, extract street type, and extract it from the street name */ 
		Matcher m = streetTypeAbbrList.getExpansionsPattern().matcher(streetTypeAbbrList.expandAbbreviations(streetName.toLowerCase()));
		
		String streetType;
		String streetNoType;
		if (!m.find()) {
			logger.warning("No street type in " + streetName);
			streetType = "улица";
			streetNoType = streetName;
		} else {
			streetType = m.group(1);
			streetNoType = m.replaceFirst(" ");
		}
		streetNoType = streetNoType.replaceAll("\\s+", " ").trim(); /* this is to fix case when street type is in the middle */
		streetNoType = WordUtils.capitalizeFully(streetNoType).replaceAll("\\bИ\\b", "и"); /* capitalize word parts but avoid 
																								capitalizing single и */
		
		/* replacement from abbreviated street name parts */

		/* permute street type place in the street name */
		for (String streetNameVar : streetNamePartList.createAllExpansions(streetNoType)) {
			
			String [] streetNameParts = streetNameVar.split("\\s+");
			
			/* check each if each street name part is an ordinal */
			ArrayList<Ordinal> ordinals = new ArrayList<Ordinal>(streetNameParts.length);
			int ordinalCount = 0;
			for (int i = 0; i < streetNameParts.length; i++) {
				Ordinal o = ordinalFactory.parse(streetNameParts[i]);
				ordinals.add(o);
				if (o != null) ordinalCount++;
			}
			
			/* permute all parts of the street name except for street type */
			for (FischerKrause fk = new FischerKrause(streetNameParts.length); fk.hasNext(); ) {
				int idx[] = fk.next();
				
				StringBuilder sb = new StringBuilder();
				StringBuilder sb_regex = new StringBuilder(); /* StringBuilder for queries with regex */
				for (int i = 0; i < idx.length; i++) {
					sb.append(streetNameParts[idx[i]]);
					
					if (ordinals.get(idx[i]) != null)
						sb_regex.append(ordinals.get(idx[i]).getSQLRegex());
					else
						sb_regex.append(streetNameParts[idx[i]]);
					
					if (i == idx.length - 1) continue; /* avoid adding space at the end of the string */
					
					sb.append(' ');
					sb_regex.append(' ');
				}

				/* permutation of the words without street types */
				variations.add( sb.toString() + ' ' + streetType);
				variations.add( streetType + ' ' + sb.toString());

				/* permutation for regexped form of the street name with ordinals regexps */
				if (ordinalCount > 0 && !sb.toString().contains("(")) { /* make sure that streetname itself does not contain regex-like symbols (typically resulting from parse errors) */
					variations.add( "^" + sb_regex.toString() + ' ' + streetType + '$');
					variations.add( "^" + streetType + ' ' + sb_regex.toString() + '$');

					/* if the street name starts with an ordinal add permutations with the street type after the 1st ordinal */
					if (ordinals.get(idx[0]) != null && streetNameParts.length > 1) {
						variations.add(sb.insert( streetNameParts[idx[0]].length() + 1, streetType + ' ').toString());
						variations.add("^" + sb_regex.insert( ordinals.get(idx[0]).getSQLRegex().length() + 1, streetType + ' ').toString() + '$');
					}
				}
			}
		}
		
		return variations;
	}

	/**
	 * This is for testing only, will be removed
	 * @param args
	 */
	public static void main(String[] args) {
		
		String s[] = {
				 "5-я Чоботовская аллея",
				 "ул 26 Бакинских комиссаров",             
				 "1-ая Муравская ул",                       
				 "3-го Интернационала ул",
				 "2-я Железногорская ул",              
				 "1-я Чоботовская аллея",           
				 "9 Северная линия",                 
				 "1 Северная линия ",              
				 "3-я Павлоградская ул",           
				 "Вторая Павлоградская ул  ",      
				 "1-я Павлоградская ул ",        
				 "5 Северная линия      ",        
				 "2 Северная линия",           
				 "Капотня 3 квартал ",           
				 "6-я Чоботовская аллея",            
				 "Капотня 5 квартал",             
				 "2-я Лазенки ул",                 
				 "4 Северная линия",                   
				 "Одиннадцатая Чоботовская аллея",         
				 "3-я Лазенки ул"     
		};
		
		try {
			String ars[] = args.length > 0 ? args : s;
			for (String string : ars) 
				System.out.println(s + " => " + (new StreetNameNormalizer().normalize(string)).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

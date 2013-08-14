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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * TODO: code all the forms of ordinal numbers; specific endings 
 * can be found by intersecting sets of endings by number, gender, 
 * value, and case  
 * 
 * 			муж			жен			ср			мн
 * есть     первый  	первая 		первое  	первые 0 1 4 5 9 10-20      
 * нет      первого 	первой 		первого 	первых    
 * давать   первому 	первой 		первому 	первым
 * винить   первого 	первую 		первое  	первых
 * сделан   первым  	первой 		первым  	первыми
 * думать о первом  	первой 		первом  	первых
 * 
 * есть		второй 		вторая 		второе 		вторые 2 6 7 8 
 * нет		второго		второй		второго		вторых
 * давать	второму		второй		второму		вторым
 * винить	второго		вторую		второго		вторых	
 * сделан	вторым		второй		вторым		вторыми
 * думать о	втором		второй		втором		вторых
 * 
 * есть		третий 		третья 		третье 		третьи 3
 * нет		третьего	третьей		третьего	третьих
 * давать	третьему	третьей		третьему	третьим
 * винить	третьего	третью		третье		третьих
 * сделан	третьим		третьей		третьим		третьеми		
 * думать о	третьем		третьей		третьем		третьих
 * 
 * есть		четвертый	четвертая	четвертое	четвертые 5
 * нет		четвертого	четвертой	четвертого	четвертвых
 * давать	четвертому	четвертой	четвертому	четвертым	
 * винить	четвертого	четвертую	четвертое	четвертых
 * сделан	четвертым	четвертой	четвертым	четвертыми
 * думать о	четвертом	четвертой	четвертом	четвертых	
 * 
 * есть		
 * нет		
 * давать	
 * винить	
 * сделан	
 * думать о	
 * 
 * нуле перв втор трет четверт пят шест сем восьм девят десят
 * один две три четыр пят шест сем восем девят
 * 
 * Russian ordinals factory
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class OrdinalFactory {

	/**
	 * Stems for numeral from 0 to 10 and their lookup table
	 */
	private String[] stems0_10 = { "нуле", "перв", "втор", "трет", "четверт",
			"пят", "шест", "сем", "восьм", "девят", "десят" };
	private HashMap<String, Integer> stems0_10_lookup;
	
	/**
	 * Stems for numerals from 11 to 19
	 */
	private String[] stems11_19 = { "один", "две", "три", "четыр", "пят",
			"шест", "сем", "восем", "девят" };
	private HashMap<String, Integer> stems11_19_lookup;

	private Pattern digits_numeral_pat;
	private Pattern stem0_10_numeral_pat;
	private Pattern stem11_19_numeral_pat;

	/**
	 * Default constructor
	 */
	public OrdinalFactory() {
		
		/* creating varions maps and hash tables */
		stems0_10_lookup = new HashMap<String, Integer>(stems0_10.length);
		for (int i = 0; i < stems0_10.length; i++) 
			stems0_10_lookup.put(stems0_10[i], i);
		
		stems11_19_lookup = new HashMap<String, Integer>(stems11_19.length);
		for (int i = 0; i < stems11_19.length; i++) 
			stems11_19_lookup.put(stems11_19[i], i);
		
		/* create matching patterns for parsing */
		/* pattern for 1-й, 2-й, ... */
		digits_numeral_pat = Pattern.compile("\\b(\\d{1,2})(?:(-)?([а-я]{1,2}))?\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		
		/* pattern for первый, второй, ... */
		StringBuilder sb0_10_pat = new StringBuilder();
		sb0_10_pat.append("\\b(");
		sb0_10_pat.append(StringUtils.join(stems0_10, '|'));
		sb0_10_pat.append(")([а-я]{1,3})\\b");
		stem0_10_numeral_pat = Pattern.compile(sb0_10_pat.toString(), 
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		
		/* pattern for первый, второй, ... */
		StringBuilder sb11_19_pat = new StringBuilder();
		sb11_19_pat.append("\\b(");
		sb11_19_pat.append(StringUtils.join(stems11_19, '|'));
		sb11_19_pat.append(")надцат([а-я]{1,2})\\b");
		stem11_19_numeral_pat = Pattern.compile(sb11_19_pat.toString(), 
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		
		// TODO: patterns for 20+
	}

	/**
	 * Factory method to create a new ordinal
	 * 
	 * @param value
	 * @return
	 */
	public Ordinal create(int value) {
		Ordinal o = new Ordinal(this);
		return o.setValue(value);
	}

	/**
	 * Factory method to create a new ordinal with preset properties
	 * 
	 * @param value
	 * @param gcase
	 * @param ggender
	 * @param gnumber
	 * @return
	 */
	public Ordinal create(int value, GCase gcase, GGender ggender,
			GNumber gnumber) {
		return new Ordinal(this, value, gcase, ggender, gnumber);
	}

	/**
	 * Factory method to parse an ordinal from a string. Sets all ordinal
	 * properties.
	 * 
	 * @param s
	 * @return a new Ordinal withh all properties set or null if the string is not parsable
	 */
	public Ordinal parse(String s) {
	
		/* checking for 1-й, 2-й, ... */
		Matcher m1 = digits_numeral_pat.matcher(s);
		if (m1.find()) {
			Ordinal o = new Ordinal(this).setOrigStr(s);
			o.setValue(Integer.valueOf(m1.group(1))); // FIXME: catch number format exception
			o.setDash(m1.group(2) != null && !m1.group(2).isEmpty());
			o.setGrammaticalProperties(m1.group(3));
			return o;
		}
		
		/* checking for первый, второй, ... */
		Matcher m2 = stem0_10_numeral_pat.matcher(s);
		if (m2.find()) {
			Ordinal o = new Ordinal(this).setOrigStr(s);
			o.setValue(stems0_10_lookup.get(m2.group(1).toLowerCase()));
			o.setGrammaticalProperties(m2.group(2));
			return o;
		}
	
		/* checking for одиннадцатый, двенадцатый, ... */
		Matcher m3 = stem11_19_numeral_pat.matcher(s);
		if (m3.find()) {
			Ordinal o = new Ordinal(this).setOrigStr(s);
			o.setValue(11 + stems11_19_lookup.get(m3.group(1).toLowerCase()));
			o.setGrammaticalProperties(m3.group(2));
			return o;
		}
	
		return null;
	}

	/**
	 * returns the stem for a given value
	 * @param value
	 * @return
	 */
	public String getStem(int value) {
		if (value >= 0 && value < 11)
			return stems0_10[value];
		else if (value > 10 && value < 20)
			return stems11_19[value - 11];
		else
			return "";
	}

	/**
	 * This is for testing only, will be removed
	 * @param args
	 */
	public static void main(String[] args) {
		try {
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
			
			OrdinalFactory of = new OrdinalFactory();
			for (String string : s) 
				System.out.println(string + " => " + of.parse(string));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

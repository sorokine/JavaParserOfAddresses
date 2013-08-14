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
public class StreetNamePartsAbbrList extends AbbrList {

	/**
	 * 
	 */
	public StreetNamePartsAbbrList() {
		addAbbrev(
				new String[]{"Б", "Бол"},   
				new String[]{"Большой", "Большая", "Большое", "Большие"});
		addAbbrev(
				new String[]{"М", "Мал"},   
				new String[]{"Малый", "Малая", "Малое", "Малые"});
		addAbbrev(
				new String[]{"Н", "Нижн"}, 
				new String[]{"Нижний", "Нижняя", "Нижнее", "Нижние", "Нижне-"}); // special processing for dash
		addAbbrev(
				new String[]{"Н"}, 
				new String[]{"Николая"}); 
		addAbbrev(
				new String[]{"В", "Верх", "Верхн"}, 
				new String[]{"Верхний", "Верхняя", "Верхнее", "Верхние", "Верхне-"}); // special processing for dash
		addAbbrev(
				new String[]{"Н", "Нов"}, 
				new String[]{"Новый", "Новая", "Новое", "Новые", "Ново-"});
		addAbbrev(
				new String[]{"Ср", "С"}, 
				new String[]{"Средний", "Средняя", "Среднее", "Средние"});
		addAbbrev(
				new String[]{"С", "Ст", "Стар"}, 
				new String[]{"Старый", "Старая", "Старое", "Старые", "Старо-"}); // special processing for dash
		addAbbrev(
				new String[]{"А", "Ак", "Акад", "академика "}, 
				new String[]{"Академика"});
		addAbbrev(
				new String[]{"М"}, 
				new String[]{"Маршала"});
		
		addAbbrev("З и А", 	"Зои и Александра");
		addAbbrev("Ф",		"Федора");
		addAbbrev("Екат",	"Екатерины");
		addAbbrev("О",		"Олеко");
		addAbbrev("В",		"Василисы");
	}

	/**
	 * This is for testing only, will be removed
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			StreetNamePartsAbbrList snpal = new StreetNamePartsAbbrList();
			System.out.println(snpal.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

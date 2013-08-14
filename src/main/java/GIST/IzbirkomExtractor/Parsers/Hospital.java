/**
 * Copyright © 2013 , UT-Battelle, LLC
 * All rights reserved
 *                                        
 * JavaParserOfAddresses, Version 1.0
 * http://github.com/sorokine/JavaParserOfAddresses
 * 
 * This program is freely distributed under UT-Batelle, LLC
 * open source license.  Read the file LICENSE.txt for details.
 * 
 */
package GIST.IzbirkomExtractor.Parsers;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 * 
 * Parses out hospital and resort names and addresses 
 *
 */
public class Hospital extends Organization {

	public Hospital() {
		setRegexIgnoreUnicodeCase(".*(больн|клини|(М|м)ед|(Н|н)ауч|Вишневского|логич|ГКБ|СНТ|Родильный|Госпиталь|диспансер|роддом|реабил|лечебн|псих|НИИ).+");
		setPriority(50);
	}

}

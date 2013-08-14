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

import java.io.File;

import org.jsoup.nodes.Element;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class TableExtractorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public TableExtractorException() {}

	/**
	 * @param arg0
	 */
	public TableExtractorException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public TableExtractorException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public TableExtractorException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public TableExtractorException(String msg, Element el,
			File input_html) {
		super("Failed parsing HTML file, reason: " + msg + "\nelement: '" + el.html() + "'\nin file '" + input_html +"'");
	}

}

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
public class ResultSinkException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ResultSinkException() {}

	/**
	 * @param arg0
	 */
	public ResultSinkException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public ResultSinkException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ResultSinkException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}

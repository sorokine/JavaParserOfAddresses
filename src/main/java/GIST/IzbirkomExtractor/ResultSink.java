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

import java.io.IOException;
import java.util.logging.Logger;

public abstract class ResultSink {

	/**
	 * Main logger
	 */
	protected final static Logger logger = Logger.getLogger("ALL");
	
	/**
	 * Posts a single matched address to the output system.
	 * 
	 * @param ia
	 * @throws IOException
	 */
	public abstract void postResult(IndividualAddress ia) throws ResultSinkException;
	
	/**
	 * Commits posted results (typically databases commit or file system flush)
	 * 
	 * @throws IOException
	 */
	public abstract void commit() throws ResultSinkException;
	
}

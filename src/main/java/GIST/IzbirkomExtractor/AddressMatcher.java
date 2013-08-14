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

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 * 
 */
public abstract class AddressMatcher {

	/**
	 * Main logger
	 */
	protected final static Logger logger = Logger.getLogger("ALL");
	
	/**
	 * Tries to match address an street addressed contained in ia. On successful
	 * match returns true and adds identifiers of the matched object to ia. On
	 * failure returns false and does not make any changes to ia.
	 * 
	 * @param ia address object to match and update
	 * @return
	 * @throws SQLException
	 */
	public abstract boolean matchAddress(IndividualAddress ia)
			throws SQLException;

//	public abstract boolean matchAddress(String street_name, String house_number)
//			throws SQLException;
}

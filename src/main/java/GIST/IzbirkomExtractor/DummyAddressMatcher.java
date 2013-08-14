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

/**
 * This class does not do anything, only pretends. 
 * 
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class DummyAddressMatcher extends AddressMatcher {

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressMatcher#matchAddress(GIST.IzbirkomExtractor.IndividualAddress)
	 */
	@Override
	public boolean matchAddress(IndividualAddress ia) throws SQLException {
		return false;
	}

}

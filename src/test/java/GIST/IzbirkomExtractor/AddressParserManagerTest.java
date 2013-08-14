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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class AddressParserManagerTest {
	
	private AddressParserManager apm;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		apm = new AddressParserManager();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link GIST.IzbirkomExtractor.AddressParserManager#AddressParserManager()}.
	 */
	@Test
	public void testAddressParserManager() {
	}

	/**
	 * Test method for {@link GIST.IzbirkomExtractor.AddressParserManager#loadAllParsers()}.
	 */
	@Test
	public void testLoadAllParsers() {
		try {
			apm.loadAllParsers();
		} catch (Exception e) {
			fail(e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link GIST.IzbirkomExtractor.AddressParserManager#parseThroughAllParsers(java.lang.String)}.
	 */
	@Test
	public void testParseThroughAllParsers() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link GIST.IzbirkomExtractor.AddressParserManager#toString()}.
	 */
	@Test
	public void testToString() {
		System.out.println(apm.toString());
	}

}

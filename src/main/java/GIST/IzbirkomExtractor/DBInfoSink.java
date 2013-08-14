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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates a table with misc. address information in the database.
 * 
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class DBInfoSink extends ResultSink {
	
	Connection con;
	PreparedStatement info_pst;

	/**
	 * 
	 */
	public DBInfoSink(Connection con) throws ResultSinkException  {
		try {
			this.con = con;
			con.setAutoCommit(false);
			
			/* create database tables for results */
			Statement st = con.createStatement();
			st.executeUpdate("DROP TABLE IF EXISTS addrinfo");
			st.executeUpdate("CREATE TABLE addrinfo (" +
					"raion text, " +			// 1
					"psid int, " +				// 2
					"street_name text, " +		// 3
					"house_number text, " +		// 4
					"korpus text, " +			// 5
					"stroenie text, " +			// 6
					"building text, " +			// 7
					"osmid_count int, " +		// 8
					"parser text, " +			// 9
					"parseerror boolean, " +	// 10
					"parser_list text, " +		// 11 list of parsers that matched the address
					"matcherror text, " +		// 12 match error message
					"src_addr text, " +			// 13 source address text
					"src_org text, " +			// 14 source organization text
					"src_station text, " +		// 15 source polling station address
					"iadump text, " +			// 16
					"matched_parsers int " +	// 17 count of parsers that have matching addresses 
					// PRIMARY KEY (raion, psid) 
					")");
			info_pst = con
					.prepareStatement("INSERT INTO addrinfo VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
			
		} catch (SQLException e) {
			throw new ResultSinkException(e);		
		}
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.ResultSink#postResult(GIST.IzbirkomExtractor.IndividualAddress)
	 */
	@Override
	public void postResult(IndividualAddress ia) throws ResultSinkException {
		try {
			
			info_pst.setString(  1, ia.getRaion());
			info_pst.setInt(     2, ia.getPsid());
			info_pst.setString(  3, ia.getStreetName());
			info_pst.setString(  4, ia.getHouseNumber().getNumber());
			info_pst.setString(  5, ia.getHouseNumber().getKorpus());
			info_pst.setString(  6, ia.getHouseNumber().getStroenie());
			info_pst.setString(  7, ia.getHouseNumber().getBuilding());
			info_pst.setInt(     8, ia.getOsmid().size());
			info_pst.setString(  9, ia.getAddressParser().getClass().getSimpleName());
			info_pst.setBoolean(10, ia.hasParseError());
			info_pst.setString( 11, ia.getOtherParsesAsString());
			info_pst.setString( 12, ia.getMsgsAsString());
			info_pst.setString( 13, ia.getSrcText());
			info_pst.setString( 14, ia.getSrcOrgAddr());
			info_pst.setString( 15, ia.getSrcStationAddr());
			info_pst.setString( 16, ia.toString());
			info_pst.setLong(   17, ia.getSecondaryMatchCount());
			
			info_pst.executeUpdate();
			
		} catch (SQLException e) {
			throw new ResultSinkException(e);
		}
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.ResultSink#commit()
	 */
	@Override
	public void commit() throws ResultSinkException {
		try {
			con.commit();
		} catch (SQLException e) {
			throw new ResultSinkException(e);		
		}
	}

}

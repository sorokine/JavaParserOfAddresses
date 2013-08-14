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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class DBSink extends ResultSink {

	Connection con;
	PreparedStatement bldg_pst, dupkey_pst;
	
	String previous_psid = null;
	
	/**
	 * @throws SQLException 
	 * 
	 */
	public DBSink(Connection con) throws ResultSinkException {

		try {
			this.con = con;
			con.setAutoCommit(false);
			
			/* create database tables for results */
			Statement st = con.createStatement();
			st.executeUpdate("DROP TABLE IF EXISTS bldg_psids");
			// FIXME: add constraint on polling station list
			st.executeUpdate("CREATE TABLE bldg_psids (" +
					"way_id bigint, " +
					"psid int, " +
					"origaddr text, " +
					"orighousenumber text, " +
					"CONSTRAINT con1 PRIMARY KEY (way_id, psid) )");
			bldg_pst = con
					.prepareStatement("INSERT INTO bldg_psids VALUES ( ?, ?, ?, ? )");
			
			dupkey_pst = con.prepareStatement("SELECT * FROM bldg_psids WHERE way_id = ? AND psid = ?");
			
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
			for (long osmid : ia.getOsmid().toArray()) {
				
				/* first check duplicate key violation */
				dupkey_pst.setLong(1, osmid);
				dupkey_pst.setInt(2, ia.getPsid());
				ResultSet rs = dupkey_pst.executeQuery();
				if (rs.next()) {
					logger.warning("Duplicate building-psid " + osmid + "-" + ia.getPsid() + " in " + ia);
					return;
				}
				
				bldg_pst.setLong(	1, osmid);
				bldg_pst.setInt(	2, ia.getPsid());
				bldg_pst.setString(	3, ia.getMatchedStreetName());
				bldg_pst.setString(	4, ia.getHouseNumber().getSearchString());

				bldg_pst.executeUpdate();
			}
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

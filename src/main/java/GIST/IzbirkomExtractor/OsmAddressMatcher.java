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
import java.util.HashMap;
import java.util.HashSet;

import GIST.IzbirkomExtractor.Parsers.Korpus;
import GIST.IzbirkomExtractor.Parsers.Kvartal;
import GIST.IzbirkomExtractor.Parsers.Organization;
import GIST.IzbirkomExtractor.Parsers.StreetList;
import GIST.IzbirkomExtractor.Parsers.StreetNumbers;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class OsmAddressMatcher extends AddressMatcher {
	
	private PreparedStatement find_addr_pst, find_street_pst, find_korpus_pst, find_addr_regex_pst;
	private PreparedStatement find_street_lits_pst;
	private PreparedStatement find_street_regex_pst;

	private StreetNameNormalizer streetNameNormalizer = new StreetNameNormalizer();
	private HashMap<String, HashSet<String>> streetNameCache = new HashMap<String, HashSet<String>>(1000);
	private PreparedStatement find_kvartal_regex_pst;
	private PreparedStatement find_kvartal_pst;

	/**
	 * 
	 * @param con connection to the database with apidb schema
	 * @throws SQLException 
	 */
	public OsmAddressMatcher(Connection con) throws SQLException {

		/* statement for street name verification and caching */
		String find_street_stmt_string =
				"SELECT id, no_yo_streetname " +
				"FROM ways_helper " +
				"WHERE " +
					"no_yo_streetname = lower(?)";
		find_street_pst = con.prepareStatement(find_street_stmt_string);
		
		/* statement for street name verification and caching with regex */
		String find_street_stmt_regex_string =
				"SELECT id, no_yo_streetname " +
				"FROM ways_helper " +
				"WHERE " +
					"no_yo_streetname ~* ?";
		find_street_regex_pst = con.prepareStatement(find_street_stmt_regex_string);
		
		/* statement for address matching for street-house address */
		String find_addr_stmt_string = 
				"SELECT id, no_yo_streetname, housenumber " +
				"FROM ways_helper " +
				"WHERE " +
					"no_yo_streetname = lower(?) AND " +
					"housenumber = lower(?) AND " +
					"raion iLIKE ?";
		find_addr_pst = con.prepareStatement(find_addr_stmt_string);
		
		/* statement for address matching for street-house address using regex for house number */
		String find_addr_stmt_regex_string = 
				"SELECT id, no_yo_streetname, housenumber " +
				"FROM ways_helper " +
				"WHERE " +
					"no_yo_streetname = lower(?) AND " +
					"housenumber ~* ? AND " +
					"raion iLIKE ?";
		find_addr_regex_pst = con.prepareStatement(find_addr_stmt_regex_string);
		
		/* statement for address matching for Korpus address */
		String find_korpus_stmt_string = 
				"SELECT id, housenumber " +
				"FROM ways_helper " +
				"WHERE " +
					"housenumber = lower(?) AND " +
					"raion iLIKE ?";
		find_korpus_pst = con.prepareStatement(find_korpus_stmt_string);
		
		/* statement for address matching for street list address */
		String find_street_lits_stmt_string = 
				"SELECT id, no_yo_streetname " +
				"FROM ways_helper " +
				"WHERE " +
					"no_yo_streetname = lower(?) AND " +
					"raion iLIKE ?";
		find_street_lits_pst = con.prepareStatement(find_street_lits_stmt_string);
		
		/* statement for address matching for street-house address */
		String find_kvartal_stmt_string = 
				"SELECT id, housenumber " +
				"FROM kapotnya " +
				"WHERE " +
					"kvartal = ? AND " +
					"housenumber = lower(?)";
		find_kvartal_pst = con.prepareStatement(find_kvartal_stmt_string);
		
		/* statement for address matching for street-house address using regex for house number */
		String find_kvartal_stmt_regex_string = 
				"SELECT id, housenumber " +
				"FROM kapotnya " +
				"WHERE " +
					"kvartal = ? AND " +
					"housenumber ~* ?";
		find_kvartal_regex_pst = con.prepareStatement(find_kvartal_stmt_regex_string);
		
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.AddressMatcher#matchAddress(GIST.IzbirkomExtractor.IndividualAddress)
	 */
	@Override
	public boolean matchAddress(IndividualAddress ia) throws SQLException {

		if (!ia.hasCompleteAddress()) {
			ia.addMessage("INCOMPLETE ADDRESS");
			logger.warning("Incomplete address: " + ia);
			return false;
		} 
		
		if (ia.getAddressParser() instanceof Kvartal)
			return matchKvartal(ia);
		else if (ia.getAddressParser() instanceof StreetNumbers)
			return matchStreetNumber(ia);
		else if (ia.getAddressParser() instanceof Korpus)
			return matchKorpus(ia);
		else if (ia.getAddressParser() instanceof StreetList)
			return matchStreetList(ia);
		else if (ia.getAddressParser() instanceof Organization)
			return matchOrganization(ia);
		else {
			logger.warning("No address matcher for " + ia);
			return false;
		}
	}

	/**
	 * @param ia
	 * @throws SQLException
	 */
	private void cacheStreetName(String streetName) throws SQLException {
		/* not in cache, try to find a proper street name variation */
	
		boolean street_name_found = false;
		for (String street_name_variation : streetNameNormalizer.normalize(streetName)) {
	
			ResultSet rs = null;
			if (!street_name_variation.startsWith("^")) { /* non-regex query */
				find_street_pst.setString(1, street_name_variation);
				rs = find_street_pst.executeQuery();
			} else { /* use a regex query */
				find_street_regex_pst.setString(1, street_name_variation);
				//System.out.println(street_name_variation);
				rs = find_street_regex_pst.executeQuery();
			}
	
			while (rs.next()) {
				if (!streetNameCache.containsKey(streetName))
					streetNameCache.put(streetName, new HashSet<String>());
				streetNameCache.get(streetName).add(rs.getString(2));
				street_name_found = true;
			}
	
		}
	
		if (!street_name_found)  
			streetNameCache.put(streetName, null);
	}

	/**
	 * Finds all houses on a specified street
	 * @param ia
	 * @return
	 * @throws SQLException
	 */
	private boolean matchStreetList(IndividualAddress ia) throws SQLException {
		
		if (!streetNameCache.containsKey(ia.getStreetName())) 
			cacheStreetName(ia.getStreetName()); 
		
		if (streetNameCache.get(ia.getStreetName()) == null) { 

			/* quit if null in cache -- such address has never been found */
			ia.addMessage("STREET NAME NOT FOUND");
			logger.warning("Street name not found:" + ia);
			return false;

		} else {
			/* street name in cache => try to find building with the house number */
			if (streetNameCache.get(ia.getStreetName()).size() > 1) {
				ia.addMessage("MULTIPLE STREET NAME VARIATION MATCHES");
				logger.warning("Multiple street name variation matches: " + ia);
			}
			
			for (String streetName : streetNameCache.get(ia.getStreetName())) {
				/* iterate through all cached street names */

				find_street_lits_pst.setString(1, streetName);
				find_street_lits_pst.setString(2, "%" + ia.getRaion() + "%");
				ResultSet rs = find_street_lits_pst.executeQuery();

				while (rs.next()) {
					long osmId = rs.getLong(1);
					ia.setMatchedStreetName(rs.getString(2));
					ia.addOsmid(osmId);
				}
			}

			/* continue search only for a single permutation */
			if (ia.getOsmid().isEmpty()) {
				ia.addMessage("NO MATCHING ADDRESS FOUND");
				logger.warning("No matching address found: " + ia);
			}
		}
		
		return false;
	}

	/**
	 * Searches for Korpus addresses
	 * @param ia
	 * @return
	 * @throws SQLException
	 */
	private boolean matchKorpus(IndividualAddress ia) throws SQLException {

		HouseNumber hn = ia.getHouseNumber();
		find_korpus_pst.setString(1, hn.getKorpus());
		hn.setSearchString(hn.getKorpus());
		find_korpus_pst.setString(2, "%" + ia.getRaion() + "%");
		ResultSet rs = find_korpus_pst.executeQuery();

		while (rs.next()) {
			long osmId = rs.getLong(1);
			hn.setSearchResult(rs.getString(2));
			ia.addOsmid(osmId);
		}

		if (ia.getOsmid().isEmpty()) {
			ia.addMessage("NO MATCHING ADDRESS FOUND");
			logger.warning("No matching address found: " + ia);
			return false;
		} else
			return true;
		
	}

	/**
	 * Searches for StreetName - Housenumber addresses
	 * @param ia
	 * @throws SQLException
	 */
	private boolean matchStreetNumber(IndividualAddress ia) throws SQLException {
		
		if (!streetNameCache.containsKey(ia.getStreetName())) 
			cacheStreetName(ia.getStreetName()); 
		
		if (streetNameCache.get(ia.getStreetName()) == null) { 

			/* quit if null in cache -- such address has never been found */
			ia.addMessage("STREET NAME NOT FOUND");
			logger.warning("Street name not found:" + ia);
			return false;

		} else {
			/* street name in cache => try to find building with the house number */
			int match_count = streetNameCache.get(ia.getStreetName()).size();
			ia.setMatchCount(match_count);
			if (match_count > 1) {
				ia.addMessage("MULTIPLE STREET NAME VARIATION MATCHES");
				logger.warning("Multiple street name variation matches: " + ia);
			}
			
			HouseNumber hn = ia.getHouseNumber();
			for (String streetName : streetNameCache.get(ia.getStreetName())) {
				/* iterate through all cached street names */

				find_addr_pst.setString(1, streetName);
				find_addr_pst.setString(3, "%" + ia.getRaion() + "%");
				
				find_addr_regex_pst.setString(1, streetName);
				find_addr_regex_pst.setString(3, "%" + ia.getRaion() + "%");

				//System.out.println(streetName);

				int variation_count = 0;
				housenumber_loop:
				for (String houseNumber : hn.getHouseNumberVariations()) {
					variation_count++;
					//System.out.println("\t"+houseNumber);
					
					/* if house number contains any regex characters then use regexed select statement */
					ResultSet rs;
					if (!houseNumber.startsWith("^")) { /* houseNumber is not a regex */
						find_addr_pst.setString(2, houseNumber);
						rs = find_addr_pst.executeQuery();
					} else { /* houseNumber is a regex */
						find_addr_regex_pst.setString(2, houseNumber);
						rs = find_addr_regex_pst.executeQuery();
					}
					
					while (rs.next()) {
						long osmId = rs.getLong(1);
						ia.addOsmid(osmId);
						ia.setMatchedStreetName(rs.getString(2));
						
						hn.setSearchResult(rs.getString(3));
						hn.setSearchString(houseNumber);
						hn.setVariationCount(variation_count);
						
						break housenumber_loop; /* stop on the 1st house number found */
					}
				}
			}

			/* continue search only for a single permutation */
			if (ia.getOsmid().isEmpty()) {
				ia.addMessage("NO MATCHING HOUSENUMBER FOUND");
				logger.warning("No matching house number found: " + ia);
			} else
				return true;
			
		}
		
		return false;
	}

	/**
	 * Kapotnya-specific address matching
	 * @param ia
	 * @return
	 * @throws SQLException 
	 */
	private boolean matchKvartal(IndividualAddress ia) throws SQLException {
		
		HouseNumber hn = ia.getHouseNumber();
		find_kvartal_pst.setString(1, ia.getStreetName());
		find_kvartal_regex_pst.setString(1, ia.getStreetName());
			
		int variation_count = 0;
	
		housenumber_loop:
			for (String houseNumber : hn.getHouseNumberVariations()) {
				variation_count++;
				//System.out.println("\t"+houseNumber);
				
				/* if house number contains any regex characters then use regexed select statement */
				ResultSet rs;
				if (!houseNumber.startsWith("^")) { /* houseNumber is not a regex */
					find_kvartal_pst.setString(2, houseNumber);
					rs = find_kvartal_pst.executeQuery();
				} else { /* houseNumber is a regex */
					find_kvartal_regex_pst.setString(2, houseNumber);
					rs = find_kvartal_regex_pst.executeQuery();
				}
				
				while (rs.next()) {
					long osmId = rs.getLong(1);
					ia.addOsmid(osmId);
					
					hn.setSearchResult(rs.getString(2));
					hn.setSearchString(houseNumber);
					hn.setVariationCount(variation_count);
					
					break housenumber_loop; /* stop on the 1st house number found */
				}
			}
		
		/* continue search only for a single permutation */
		if (ia.getOsmid().isEmpty()) {
			ia.addMessage("NO MATCHING HOUSENUMBER FOUND");
			logger.warning("No matching housenumber found: " + ia);
		} else
			return true;
	
	return false;
	}

	/*
	 * address matcher for organizations
	 */
	private boolean matchOrganization(IndividualAddress ia) {
		// TODO Auto-generated method stub
		return false;
	}

}

--
-- Copyright © 2013 , UT-Battelle, LLC
-- All rights reserved
--                                        
-- JavaParserOfAddresses, Version 1.0
-- http://github.com/sorokine/JavaParserOfAddresses
-- 
-- This program is freely distributed under UT-Batelle, LLC
-- open source license.  Read the file LICENSE.txt for details.
--

--
-- create a table with geometries for showing in QGIS
--

DROP TABLE IF EXISTS nodes_psid;

SELECT 
	way_id, 
	nodes[1], 
	psid, 
	origaddr, 
	orighousenumber, 
	geom 
INTO 
	nodes_psid 
FROM 
	ways, 
	bldg_psids, 
	nodes 
WHERE 
	ways.id = bldg_psids.way_id AND 
	nodes[1] = nodes.id;

SELECT Populate_Geometry_Columns('nodes_psid'::regclass);

--
-- calculate matching statistics
--
DROP TABLE IF EXISTS raion_matches;

SELECT 
	count(*) AS total, 
	to_char(100.00 * (
		SELECT count(*) 
		FROM addrinfo b 
		WHERE 
			a.raion = b.raion AND 
			b.osmid_count > 0
		)/count(*), 
		'99D99'
	) AS matched, 
	raion 
INTO raion_matches 
FROM 
	addrinfo a 
GROUP BY raion 
ORDER BY matched;

ALTER TABLE raion_matches ADD primary key(raion);


DROP TABLE IF EXISTS raion_joined;

SELECT 
	a.gid, 
	r.raion, 
	r.matched::real, 
	r.total, 
	a.the_geom 
INTO raion_joined 
FROM 
	adm8_municipal AS a, 
	raion_matches AS r 
WHERE 
	a.okato_name ilike '%' || r.raion || '%';
	
SELECT 
	Populate_Geometry_Columns('raion_joined'::regclass);
	
	
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
-- createas a helper table for OSM table "ways" containing:
--  - no_yo_streetname from ways.tags->'addr:street' with [Russian] ё replaced by е in the streetnames
--  - housenumber from ways.tags->'addr:housenumber' 
--  - raion from adm8_municipal_f.name (matching record by geometry)
--
-- IMPORTANT:
--  - selected points are clipped by raion boundaries 

-- helper functions
CREATE OR REPLACE FUNCTION fix_housenumber (hn text)
RETURNS text AS $$
	BEGIN
	RETURN 		
		replace(
			regexp_replace( 
				translate (
					lower(hn), 'sc', 'сс'
				),
				'([кс])\D+(\d)', 
				'\1\2', 
				'g' 
			),
			' ',
			''
		);
	END;
$$ LANGUAGE plpgsql;

DROP TABLE IF EXISTS ways_helper;

SELECT 
		w.id, 
		lower(replace(w.tags->'addr:street', 'ё', 'е')) AS no_yo_streetname,
		fix_housenumber(w.tags->'addr:housenumber') AS housenumber,
		replace(a.name, 'ё', 'е') AS raion,
		lower(replace(w.tags->'name', 'ё', 'е')) AS no_yo_name,
		n.geom AS a_point
INTO ways_helper 
FROM 
	ways w, 
	adm8_municipal_f a, 
	nodes n 
WHERE
	w.nodes[1] = n.id AND 
	n.geom && a.the_geom AND 
	ST_Contains(a.the_geom, n.geom);
	
ALTER TABLE ways_helper 
	ADD PRIMARY KEY (id);

CREATE INDEX ways_helper_street_idx 
	on ways_helper (no_yo_streetname);
	
CREATE INDEX ways_helper_housenumber_idx 
	on ways_helper (housenumber);

SELECT Populate_Geometry_Columns('ways_helper'::regclass); 

--
-- creates a helper table for район Капотня
--

DROP TABLE IF EXISTS kapotnya;

SELECT 
		w.id, 
		substring(w.tags->'addr:full', '^Капотня (\d)-й квартал') AS kvartal,
		fix_housenumber(substring(w.tags->'addr:full', '^Капотня \d-й квартал,\s*(.+)$')) AS housenumber,
		n.geom AS a_point
INTO kapotnya 
FROM 
	ways w, 
	adm8_municipal_f a, 
	nodes n 
WHERE
	w.nodes[1] = n.id AND
	a.name ILIKE '%Капотня%' AND
	n.geom && a.the_geom AND 
	ST_Contains(a.the_geom, n.geom);

ALTER TABLE kapotnya 
	ADD PRIMARY KEY (id);

CREATE INDEX kapotnya_kvartal_idx 
	on kapotnya (kvartal);
	
CREATE INDEX kapotnya_housenumber_idx 
	on kapotnya (housenumber);

SELECT Populate_Geometry_Columns('kapotnya'::regclass); 


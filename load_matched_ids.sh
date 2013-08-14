#
# loads matched ids from parsed addresses file
#

# * Copyright © 2013 , UT-Battelle, LLC
# * All rights reserved
# *                                        
# * JavaParserOfAddresses, Version 1.0
# * http://github.com/sorokine/JavaParserOfAddresses
# * 
# * This program is freely distributed under UT-Batelle, LLC
# * open source license.  Read the file LICENSE.txt for details.

grep -v '{}' parsed_addresses-2012-11-01T15:47.csv | \
	cut -d\| -f 2,9 | \
	sed -e 's/{//g' | \
	sed -e 's/}//g' | \
	cut -d, -f 1 | \
	ghead -n -1 > wayids-incomplete.txt
	
psql90 -U osm -h localhost moscow <<EOF
DROP TABLE IF EXISTS matchids;
create TABLE matchids ( psid int, wayid bigint);
\copy matchids FROM 'wayids-incomplete.txt' WITH DELIMITER '|';
DROP TABLE IF EXISTS matched_nodes;
SELECT wayid, nodes[1], psid, geom INTO matched_nodes FROM ways, matchids, nodes where ways.id = wayid AND nodes[1] = nodes.id;
SELECT Populate_Geometry_Columns('matched_nodes'::regclass);
EOF

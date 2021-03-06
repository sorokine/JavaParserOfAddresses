Title: JavaParserOfAddresses
Author:	Alex Sorokine
Base Header Level: 2

# Java Parser of Addresses for Polling Station Boundaries #

This program extracts streets addresses depicting polling station boundaries 
and matches them with building outlines in the OpenStreetMap (OSM).  

Current version of the program is intended to process polling station addresses 
published by Moscow Municipal Election Commission for Russian Federation Parliamentary
Election of 2011 and Presidential Election of 2012.   

## Requirements ##

1.  Java 7
2.  maven 2.+
2.  Polling station boundaries (HTML pages)
3.  PostgreSQL 9.1/PostGIS 1.5 server with OpenStreetMap database
4.  osmosis (if you do not have OSM database in PostgreSQL already)
5.  shp2pgsql from PostGIS
5.  Standard and not so standard UNIX utilities: bash, bzip2, bc, 7z, etc.

The program was verified to work on OS X and Ubuntu Linux (recent versions).

## Operation Overview ##

1.  Download and compile the code
2.  Download the source data 
3.  Run the program 

The program will create a database table containing OSM identifier of buildings 
with corresponding identifiers of the polling stations.

## Downloading and Compiling the Code ##

	git clone https://github.com/sorokine/FischerKrause.git
	cd FischerKrause
	mvn install
	cd ..
	git clone http://github.com/sorokine/JavaParserOfAddresses
	cd JavaParserOfAddresses
	mvn install
	
This will download all dependencies and create a jar file in the target/ directory 
of the project. 

## Downloading and Preprocessing Source Data ##

### OpenStreetMap ###

OpenStreetMap for Moscow can be downloaded from the following web page
<http://gis-lab.info/projects/osm_dump/>.  This is a daily dump of the main
OpenStreetMap database clipped to the boundaries of Moscow.  Direct download link is
<http://data.gis-lab.info/osm_dump/dump/latest/RU-MOW.osm.bz2>.  This
step is not needed if you already have an OpenStreetMap database.  Using
Moscow-specific fragment reduces import time and data size by approx. 1000x.  

The files have to be placed into the `OSM/` subdirectory of the project:

	mkdir OSM
	cd OSM
	wget http://data.gis-lab.info/osm_dump/dump/latest/RU-MOW.osm.bz2

The code was tested to work with the following snapshots of OSM database:

	RU-MOW-2012-10-12.osm.pbf
	RU-MOW-2013-02-19.osm.bz2

Some time between these two dates OSM was updated to accommodate "New Moscow"
that has almost doubled the area of the city.  This is not an error!

#### Administrative Boundaries ####

OSM dataset contains administrative boundaries.  However, it is easier to use 
precompiled boundaries from the gis-lab.info web site.
Two versions "latest" and "stable" of administrative boundaries (level 8
"inner-city municipal administrative units for Moscow and S.-Petersburg", see
<http://gis-lab.info/qa/osm-adm.html>) can be downloaded from corresponding
directories in <http://gis-lab.info/data/yav/adm/>.  Place them under `admin/`
subdirectory.  The version without clipping by coastline was used.  

	cd admin/stable
	wget http://gis-lab.info/data/yav/adm/stable/adm8_municipal_f.7z
	7z x adm8_municipal_f.7z
	cd ../latest
	wget http://gis-lab.info/data/yav/adm/latest/adm8_municipal_f.7z
	7z x adm8_municipal_f.7z

### Election Data ###

#### Polling Stations Boundaries Addresses ####

List of polling station addresses used to be available from
<http://www.mosgorizbirkom.ru/> but it was gone by 2013-02-19. 
Google search for "Сведения об избирательных участках района Арбат" leads to a
MSWord document on several file sharing sites with the same content that was
available from <http://www.mosgorizbirkom.ru/>.  If you cannot find it email 
me for help.

HTML files have to be downloaded into `MosIzbirKomAddresses 2012/` directory
(total size is about 142MB).  Addresses are organized into several levels of 
hierarchy starting from city level to Округ to район.  Each район page contains 
a list of polling stations with boundaries described as a list of street addresses.

## Loading Source Data into the Database ##

OSM data is loaded into PostGIS database using `osmosis` tool.

Software versions:

	| PostgreSQL | `SELECT VERSION();`             | `PostgreSQL 9.1.8` |
	| PostGIS    | `SELECT PostGIS_Lib_Version();` | `1.5.3`            |
	| osmosis    | `ls lib/default/osmosis*.jar`   | 0.42               | 
	| Linux      | `cat /etc/lsb-release`          | Ubuntu 12.04.2 LTS |

1.  Osmosis install

		wget http://bretth.dev.openstreetmap.org/osmosis-build/osmosis-latest.tgz
		mkdir osmosis
		cd osmosis
		chmod +x bin/osmosis

2.  Create two empty PostGIS databases with hstore extension one for each OSM
    snapshot (RU-MOW-2012-10-12.osm.pbf -> moscow1210, RU-MOW-2013-02-19.osm.bz2
    -> moscow1302):

		createuser -ds osm
		psql -U osm -h localhost moscow
			CREATE DATABASE moscow1302;
			\c moscow1210
			CREATE LANGUAGE plpgsql;
			\i /usr/share/postgresql/9.1/contrib/postgis-1.5/postgis.sql
			\i /usr/share/postgresql/9.1/contrib/postgis_comments.sql
			\i /usr/share/postgresql/9.1/contrib/postgis-1.5/spatial_ref_sys.sql
			CREATE EXTENSION hstore ;
			CREATE EXTENSION "fuzzystrmatch";
			CREATE DATABASE moscow1210 TEMPLATE moscow1302 ;

3.  Load OSM snapshot schema with support for supplementary information

			\i script/pgsnapshot_schema_0.6.sql
			\i script/pgsnapshot_schema_0.6_action.sql
			\i script/pgsnapshot_schema_0.6_bbox.sql
			\i script/pgsnapshot_schema_0.6_linestring.sql

4.  Load OSM data

		./bin/osmosis --read-pbf file=../RU-MOW-2012-10-12.osm.pbf --write-pgsql user=osm password=XXX database=moscow1210 2>&1 | less
		./bin/osmosis --read-xml file=../RU-MOW-2013-02-19.osm.bz2 --write-pgsql user=osm password=XXX database=moscow1302 2>&1 | less

5.  Populate helper tables for address matching (in psql, use scripts from IzbirkomExtractor/)

		psql -U osm -h localhost moscow1210 -f ways_helper.sql
		psql -U osm -h localhost moscow1302 -f ways_helper.sql

6.  Load administraive boundaries "stable" version (done using shp2pgsql
    utility on Ubuntu 12.10 on localhost):

		shp2pgsql -s 4326 -I -W UTF8 adm8_municipal_f adm8_municipal_f > adm8_municipal_f.sql
		psql -U osm -h localhost moscow1210 -f adm8_municipal_f.sql
		psql -U osm -h localhost moscow1302 -f adm8_municipal_f.sql

### HTML Parsing and Address Extraction ###

Address matching is performed using IzbirkomExtractor java program:

	java -Dfile.encoding=UTF8 -jar target/IzbirkomExtractor-1.0-SNAPSHOT.jar -i -s localhost -d moscow1210 -u osm -p osm MosIzbirKomAddresses\ 2012/
	java -Dfile.encoding=UTF8 -jar target/IzbirkomExtractor-1.0-SNAPSHOT.jar -i -s localhost -d moscow1302 -u osm -p osm MosIzbirKomAddresses\ 2012/

IzbirkomExtractor creates a table `bldg_psids` in the target database, a CSV
file with parsed address `parsed_addresses-*.csv`, and a log file with
excrutiating matching details. *-i* option creates `addrinfo` with parsing
details (required for further processing).

Number of failed matches can be found by counting the number of lines in the
parsed_addresses*.csv file that contain {} (empty set of matching OSM IDs).   
This can be done using count_matches.sh shell script:   

	./count_matches.sh 
	
Results for moscow1302

	Counting matches in parsed_addresses-2013-03-25T17:41.csv
	Total parsed addresses: 36651
	Unmatched addresses: 9245
	Match failure rate 25.22%

Summary of matching errors:

	SELECT 
		parser, 
		regexp_matches(iadump, 'msgs=\[([^]]+)\]') AS merr, 
		count(*) 
	FROM addrinfo 
	GROUP BY parser, merr 
	ORDER BY count DESC;
	
	       parser       |                                 merr                                 | count
	--------------------+----------------------------------------------------------------------+-------
	 StreetNumbers      | {"NO MATCHING HOUSENUMBER FOUND"}                                    |  5868
	 StreetNumbers      | {"STREET NAME NOT FOUND"}                                            |  2720
	 Korpus             | {"NO MATCHING ADDRESS FOUND"}                                        |   443
	 IndividualHousing  | {"STREET NAME NOT FOUND"}                                            |    18
	 StreetNumbersTypoD | {"NO MATCHING HOUSENUMBER FOUND"}                                    |    16
	 StreetList         | {"STREET NAME NOT FOUND"}                                            |    14
	 StreetNumbers      | {"Korpus or stroenie without a building number, INCOMPLETE ADDRESS"} |     6
	 StreetNumbersTypoD | {"STREET NAME NOT FOUND"}                                            |     2
	 StreetList         | {"NO MATCHING ADDRESS FOUND"}                                        |     2
	 StreetNumbersTypo  | {"STREET NAME NOT FOUND"}                                            |     2
	 StreetNumbersTypo  | {"NO MATCHING HOUSENUMBER FOUND"}                                    |     1
	 Kvartal            | {"NO MATCHING HOUSENUMBER FOUND"}                                    |     1

#### Random Observation per OSM Addresses ####

1.  Tag `addr:district` sometimes contains municipal district, sometimes okrug,
    and sometimes kvartal.


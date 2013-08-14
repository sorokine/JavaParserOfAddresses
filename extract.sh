#!/bin/bash 

# * Copyright © 2013 , UT-Battelle, LLC
# * All rights reserved
# *                                        
# * JavaParserOfAddresses, Version 1.0
# * http://github.com/sorokine/JavaParserOfAddresses
# * 
# * This program is freely distributed under UT-Batelle, LLC
# * open source license.  Read the file LICENSE.txt for details.

set -x
db=moscow1210
java -Dfile.encoding=UTF8 -jar target/IzbirkomExtractor-0.0.2-SNAPSHOT.jar -i -s localhost -d $db -u osm -p XXX MosIzbirKomAddresses\ 2012/


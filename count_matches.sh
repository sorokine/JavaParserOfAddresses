#!/bin/bash

# * Copyright © 2013 , UT-Battelle, LLC
# * All rights reserved
# *                                        
# * JavaParserOfAddresses, Version 1.0
# * http://github.com/sorokine/JavaParserOfAddresses
# * 
# * This program is freely distributed under UT-Batelle, LLC
# * open source license.  Read the file LICENSE.txt for details.

#
# counts the number of successful matches 
#

if [ -z "$1" ]; then
	# get the most recent parsed address file if none is specififed on the command line
	paf=$(ls parsed_addresses-*.csv | sort | tail -1)
else
	paf="$1"
fi

echo Counting matches in $paf

total=$(cat "$paf" | wc -l )
unmatched=$(grep '{}' "$paf" | wc -l)  
perc=$(bc <<< "scale=2; 100.0 * $unmatched / $total")

echo Total parsed addresses: $total
echo Unmatched addresses: $unmatched
echo Match failure rate $perc%

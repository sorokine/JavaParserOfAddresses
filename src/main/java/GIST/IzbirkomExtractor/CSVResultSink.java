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

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVStrategy;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class CSVResultSink extends ResultSink {
	
	private CSVPrinter csvPrinter;

	public CSVResultSink(OutputStreamWriter csvout, CSVStrategy csvStrategy) {
		csvPrinter = new CSVPrinter(csvout, csvStrategy);
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.ResultSink#postResult(GIST.IzbirkomExtractor.IndividualAddress)
	 */
	@Override
	public void postResult(IndividualAddress ia) throws ResultSinkException {
		try {
			csvPrinter.println(ia.toStringArray());
		} catch (IOException e) {
			throw new ResultSinkException(e);
		}
	}

	@Override
	public void commit() throws ResultSinkException {
		try {
			csvPrinter.flush();
		} catch (IOException e) {
			throw new ResultSinkException(e);
		}
	}

}

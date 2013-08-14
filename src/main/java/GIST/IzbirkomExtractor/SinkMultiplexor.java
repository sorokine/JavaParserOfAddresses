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

import java.util.ArrayList;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class SinkMultiplexor extends ResultSink {
	
	ArrayList<ResultSink> sinks = new ArrayList<ResultSink>();

	/**
	 * Factory method.
	 */
	public static SinkMultiplexor newSinkMultiplexor() {
		return new SinkMultiplexor();
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	public ResultSink addResultSink(ResultSink e) {
		sinks.add(e);
		return this;
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.ResultSink#postResult(GIST.IzbirkomExtractor.IndividualAddress)
	 */
	@Override
	public void postResult(IndividualAddress ia) throws ResultSinkException {
		for (ResultSink sink : sinks) {
			sink.postResult(ia);
		}
	}

	/* (non-Javadoc)
	 * @see GIST.IzbirkomExtractor.ResultSink#commit()
	 */
	@Override
	public void commit() throws ResultSinkException {
		for (ResultSink sink : sinks) {
			sink.commit();
		}
	}

}

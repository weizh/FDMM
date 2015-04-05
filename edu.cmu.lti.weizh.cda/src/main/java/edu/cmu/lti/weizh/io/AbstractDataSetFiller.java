package edu.cmu.lti.weizh.io;



import edu.cmu.lti.weizh.docmodel.AbstractDataSet;


public abstract class AbstractDataSetFiller implements Filler{

	protected AbstractDataSet fdad;
	protected AbstractSentenceReader reader;
	
	protected AbstractDataSetFiller(AbstractDataSet ds,AbstractSentenceReader reader){
		this.fdad=ds;
		this.reader = reader;
	}
	
}

package edu.cmu.lti.weizh.data;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import edu.cmu.lti.weizh.data.conll2000.CONLL2kReader;
import edu.cmu.lti.weizh.data.ontonotes.OntoNotesReader;
import edu.cmu.lti.weizh.docmodel.AbstractDataSet;
import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.NamedEntity;
import edu.cmu.lti.weizh.docmodel.Paragraph;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.eval.EVAL_CONSTS;
import edu.cmu.lti.weizh.io.AbstractDataSetFiller;
import edu.cmu.lti.weizh.io.AbstractSentenceReader;

public class CONLLFormatDataFiller extends AbstractDataSetFiller {

	protected CONLLFormatDataFiller(AbstractDataSet ds,AbstractSentenceReader reader) {
		super(ds, reader);
	}

	/**
	 * Single Doc -> Single Para -> Multi Sentence.
	 */
	@Override
	public void fill() throws Exception {
			
			Document d = new Document("DUMMY NAME");
			Paragraph para = new Paragraph();
			Sentence s = null;
			while ((s = reader.readNextSentence()) != null)
				para.addSentence(s);

			d.addParagraph(para);
			fdad.getDocuments().add(d);
			reader.close();
	}
	
	public static void main(String argv[]){
		//create a reader specific to dataset.
		// create a Conll dataset
		// create a filler to fill the dataset
	}

}

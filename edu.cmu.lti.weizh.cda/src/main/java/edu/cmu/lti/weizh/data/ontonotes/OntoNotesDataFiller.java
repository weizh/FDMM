package edu.cmu.lti.weizh.data.ontonotes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import edu.cmu.lti.weizh.docmodel.AbstractDataSet;
import edu.cmu.lti.weizh.docmodel.Document;
import edu.cmu.lti.weizh.docmodel.NamedEntity;
import edu.cmu.lti.weizh.docmodel.OntonotesDataSet;
import edu.cmu.lti.weizh.docmodel.Paragraph;
import edu.cmu.lti.weizh.docmodel.Sentence;
import edu.cmu.lti.weizh.docmodel.Word;
import edu.cmu.lti.weizh.eval.EVAL_CONSTS;
import edu.cmu.lti.weizh.io.AbstractDataSetFiller;

public class OntoNotesDataFiller extends AbstractDataSetFiller {

	/**
	 * ontonotes data has to be read as a dir. So single reader does not satisfy
	 * the need. Use null reader in constructor. Create readers in fill.
	 * 
	 * @param ds
	 */
	public OntoNotesDataFiller(AbstractDataSet ds) {
		super(ds, null);
	}

	public static void main(String argv[]) throws Exception {
		OntonotesDataSet fdad = new OntonotesDataSet(19999, EVAL_CONSTS.NER_TYPE);
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations";
		try {
			new OntoNotesDataFiller(fdad).fill(new File(path),false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fill in the named entity in each sentence to get the gold standard of
	 * NEs. This is different from the default method where the word feature is
	 * used as gold standard.
	 * Each file is a document. One document only contain one paragraph.
	 * @param collapseTag 
	 */
	public void fill(File node, boolean collapseTag) throws Exception {

		// System.out.println(node.getAbsoluteFile());

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				fill(new File(node, filename),collapseTag);
			}
		} else {
			if (node.isFile() && node.getAbsolutePath().endsWith(".onf")) {
				// System.out.println(node.getAbsolutePath());
				OntoNotesReader onfr = new OntoNotesReader(new FileReader(node.getAbsoluteFile()));
				Document d = new Document(node.getAbsolutePath());
				Paragraph para = new Paragraph();
				Sentence s = null;
				while ((s = onfr.readNextSentence()) != null)
					para.addSentence(s);

				/*
				 * loop over the sentences in paragraph.
				 */
				for (Sentence sent : para.getSentences()) {

					List<Word> words = sent.getWords();
					for (Word word : words) {
						word.setEntityType("O");
					}

					for (NamedEntity ne : sent.getNamedEntities()) {
						for (int i = ne.getStart(); i <= ne.getEnd(); i++) {
							words.get(i).setEntityType(collapseTag?getCollapsed(ne.getEntityType()):ne.getEntityType());
						}
					}
				}
				d.addParagraph(para);
				fdad.getDocuments().add(d);
				onfr.close();
			}
		}
	}

	private String getCollapsed(String entityType) {
		switch(entityType){
		case "GPE": 
		case "PERSON":
		case "ORG":
			return entityType;
		default:
			return "MISC";
		}
	}

	@Override
	public void fill() throws Exception {
		// TODO Auto-generated method stub
		
	}
}

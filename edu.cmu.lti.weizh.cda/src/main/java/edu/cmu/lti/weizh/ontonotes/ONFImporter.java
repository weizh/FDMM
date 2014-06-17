package edu.cmu.lti.weizh.ontonotes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.NamedEntity;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;
import edu.cmu.lti.weizh.models.Word;

public class ONFImporter {

	public static void main(String argv[]) throws Exception {
		ONF_DataSet fdad = new ONF_DataSet();
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations";
		try {
			fill(new File(path), fdad);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void fill(File node, ONF_DataSet fdad) throws Exception {

		// System.out.println(node.getAbsoluteFile());

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				fill(new File(node, filename), fdad);
			}
		} else {
			if (node.isFile() && node.getAbsolutePath().endsWith(".onf")) {
//				 System.out.println(node.getAbsolutePath());
				ONFReader onfr = new ONFReader(new FileReader(node.getAbsoluteFile()));
				Document d = new Document(node.getAbsolutePath());
				Paragraph para = new Paragraph();
				Sentence s = null;
				while ((s = onfr.readNextSentence()) != null)
					para.addSentence(s);
				
				/*
				 * loop over the sentences in paragraph.
				 */
				for (Sentence sent : para.getSentences()){

					List<Word> words = sent.getWords();
					for (Word word : words) {
						word.setEntityType("[O]");
					}

					for (NamedEntity ne : sent.getNamedEntities()) {
						for (int i = ne.getStart(); i <= ne.getEnd(); i++) {
							words.get(i).setEntityType(ne.getEntityType());
						}
					}
				}
				d.addParagraph(para);
				fdad.getDocuments().add(d);
			}
		}
	}
}

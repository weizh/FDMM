package edu.cmu.lti.weizh.fda;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;

public class ONF2FDADImporter {

	public static void main(String argv[]) throws Exception {
		FDA_DataSet fdad = new FDA_DataSet();
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations";
		try {
			fill(new File(path), fdad);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void fill(File node, FDA_DataSet fdad) throws Exception {

		// System.out.println(node.getAbsoluteFile());

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				fill(new File(node, filename), fdad);
			}
		} else {
			if (node.isFile() && node.getAbsolutePath().endsWith(".onf")) {
//				 System.out.println(node.getAbsolutePath());
				OntoNotesFormatReader onfr = new OntoNotesFormatReader(new FileReader(node.getAbsoluteFile()));
				Document d = new Document(node.getAbsolutePath());
				Paragraph para = new Paragraph();
				Sentence s = null;
				while ((s = onfr.readNextSentence()) != null)
					para.addSentence(s);
				d.addParagraph(para);
				fdad.getDocuments().add(d);
			}
		}
	}
}

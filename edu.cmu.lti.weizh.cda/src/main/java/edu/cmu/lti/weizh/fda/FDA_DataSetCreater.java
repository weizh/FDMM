package edu.cmu.lti.weizh.fda;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.cmu.lti.weizh.models.Document;
import edu.cmu.lti.weizh.models.Paragraph;
import edu.cmu.lti.weizh.models.Sentence;

public class FDA_DataSetCreater {


	public static void main(String argv[]) {
		FDA_DataSet fdad = new FDA_DataSet();
		String path = "C:/Users/Wynn/Documents/wynnzh/data/LDC/data/english/annotations";
		try {
			filterTags(new File(path), fdad);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void filterTags(File node, FDA_DataSet fdad) throws IOException {

		System.out.println(node.getAbsoluteFile());

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				filterTags(new File(node, filename), fdad);
			}
		} else {
			if (node.isFile() && node.getAbsolutePath().endsWith(".onf")) {
				OntoNotesFormatReader onfr = new OntoNotesFormatReader(new FileReader(new File(node.getName())));
				Document d = new Document();
				Paragraph para = new Paragraph();
				Sentence s = null;
				while ((s = onfr.readNextSentence()) != null)
					para.addSentence(s);
				d.addParagraph(para);
			}
		}
	}
}

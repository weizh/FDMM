package edu.cmu.lti.weizh.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Document implements Serializable{

	String documentID;
	List<Paragraph> paragraphs;

	public Document(String id) {
		this.documentID=id;
		paragraphs = new ArrayList<Paragraph>();
	}

	public List<Paragraph> getParagraphs() throws Exception {

		return paragraphs;
	}

	public void addParagraph(Paragraph para) {
		// TODO Auto-generated method stub
		this.paragraphs.add(para);
	}

	public String getDocId() {
		// TODO Auto-generated method stub
		return this.documentID;
	}
}

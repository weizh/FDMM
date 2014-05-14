package edu.cmu.lti.weizh.models;

import java.util.ArrayList;
import java.util.List;

public class Document {

  List<Paragraph> paragraphs;

  public Document(){
	  paragraphs = new ArrayList<Paragraph>();
  }
  public  List<Paragraph> getParagraphs() throws Exception {
 
    return paragraphs;
  }
public void addParagraph(Paragraph para) {
	// TODO Auto-generated method stub
	this.paragraphs.add(para);
}
}

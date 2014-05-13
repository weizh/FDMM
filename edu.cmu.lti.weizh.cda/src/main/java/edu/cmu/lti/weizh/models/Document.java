package edu.cmu.lti.weizh.models;

import java.util.List;

public class Document {

  List<Paragraph> paragraphs;

  public  List<Paragraph> getParagraphs() throws Exception {
    if (paragraphs ==null){
      throw new Exception("Paragraphs not created for document.");
    }
    return paragraphs;
  }
}

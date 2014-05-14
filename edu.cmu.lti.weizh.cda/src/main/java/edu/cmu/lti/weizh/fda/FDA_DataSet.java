package edu.cmu.lti.weizh.fda;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.weizh.models.DataSet;
import edu.cmu.lti.weizh.models.Document;

public class FDA_DataSet extends DataSet {
  
  List<Document> documents;

  public FDA_DataSet(){
	  documents = new ArrayList<Document>();
  }
  public List<Document> getDocuments(){
    return documents;
  }
  
  
}

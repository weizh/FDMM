package edu.cmu.lti.weizh.fda;

import java.util.List;

import edu.cmu.lti.weizh.models.DataSet;
import edu.cmu.lti.weizh.models.Document;

public class FDA_DataSet extends DataSet {
  
  List<Document> documents;

  public List<Document> getDocuments() throws Exception {
    if (documents==null){
      throw new Exception("Documents not created for dataset");
    }
    return documents;
  }
  
  
}

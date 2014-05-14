package edu.cmu.lti.weizh.Interface;

import edu.cmu.lti.weizh.models.DataSet;

public interface Trainer<T> {
  
  public T train(DataSet d) throws Exception;
  
}

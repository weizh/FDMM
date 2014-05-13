package edu.cmu.lti.weizh.Interface;

import edu.cmu.lti.weizh.models.DataSet;
import edu.cmu.lti.weizh.models.MLModel;

public interface Trainer {
  
  public MLModel train(DataSet d) throws Exception;
  
}

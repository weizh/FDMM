package edu.cmu.lti.weizh.Interface;


public interface Trainer<M,D> {
  
  public M train(D d) throws Exception;
  
}

package edu.cmu.lti.weizh.Interface;


public interface Trainer<M,D> {
  
  public M train(D d, double fold, int foldId) throws Exception;
}

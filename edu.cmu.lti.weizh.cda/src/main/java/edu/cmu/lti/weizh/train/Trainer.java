package edu.cmu.lti.weizh.train;


public interface Trainer<M,D> {
  
  public M train(D d) throws Exception;
}
